package outskirts.physics.collision.narrowphase.collisionalgorithm.gjk;

import outskirts.client.gui.screen.tools.GuiScreen3DVertices;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Gilbert-Johnson-Keerthi Algorithm / in 3D.
 *
 * when two convex shape overlapping/intersecting, the 'Minkowski Difference' of them'll contains the Origin.
 *
 * in the Impl, we maintaing a size[0-3-4] Simplex which is a subset of Points on the MinkowskiDifference's boundary.
 */
public class Gjk {

    private static final int MAX_ITERATION_COUNT = 30;  // 30


    /**
     * the Point on the MinkowskiDifference's boundary.
     */
    public static final class SupportPoint { // a.k.a SimplexVert
        private SupportPoint() {}
        public final Vector3f point            = new Vector3f(); // the Point. value == (farthestPointOnA - farthestPointOnB)
        public final Vector3f farthestpointOnB = new Vector3f(); // Farthest-Point on bodyB. worldspace. (for getting ContactPoint-onB

        @Override
        public String toString() { return "{point="+point+"}"; }
    }
    /**
     * GJK - the SupportFunction: get the Point on the MinkowskiDifference's boundary in direction.
     * take the farthest point on shape1 in direction d (as p1), and take the farthest point on shape2 in
     * direction -d (opposite direction of d) (as p2), then return p1 - p2.
     */
    public static SupportPoint getSupportPoint(CollisionObject bodyA, CollisionObject bodyB, Vector3f d) {
        SupportPoint sp = new SupportPoint(); Vector3f TMPfarthestpointOnA = new Vector3f();// STACK ALLOC
        ((ConvexShape)bodyA.getCollisionShape()).getFarthestPoint(d,          TMPfarthestpointOnA, bodyA.transform());
        ((ConvexShape)bodyB.getCollisionShape()).getFarthestPoint(d.negate(), sp.farthestpointOnB, bodyB.transform()); d.negate();// negate back
        Vector3f.sub(TMPfarthestpointOnA, sp.farthestpointOnB, sp.point); // the p1 - p2.
        return sp;
    }


    /**
     * Generation a Simplex on MinkowskiDifference/CSO try to Enclose the Origin.
     *
     * @return when collision detected, return 4-vertices-simplex and its contains the origin. else return null.
     */
    public List<SupportPoint> detectCollision(CollisionObject bodyA, CollisionObject bodyB) {
        List<SupportPoint> simplex = new ArrayList<>(4); // there just 4 vertex

        Vector3f d = new Vector3f(Vector3f.UNIT_X); // init first d

        for (int i = 0;i < MAX_ITERATION_COUNT;i++) {
//            Log.LOGGER.info("GjkItr: {}", i);
//            GuiScreen3DVertices.addNorm("sp"+i+".norm", Vector3f.ZERO, d, Colors.YELLOW);
//            SystemUtils.debugCanContinue();

            SupportPoint sp = Gjk.getSupportPoint(bodyA, bodyB, d.normalize());
            simplex.add(sp);
//            GuiScreen3DVertices.addVert("sp"+i, sp.point, Colors.YELLOW);


            if (Vector3f.dot(sp.point, d) <= 0.001f) {
                // exit. when you want encloses the Origin. the boundary-point must further along the "Point/Line/Face to Origin direction".  when == 0 is touching.
                // this the tolerance working for 1. prevents "Plane Simplex". 2. sometimes avoid getting duplicated SupportPoint.
                return null;
            }
            if (isContainsOrigin_updateSimplex__updateDirection(simplex, d)) {
                return simplex;
            }
        }
        Log.LOGGER.warn("Out of iter {}.", MAX_ITERATION_COUNT);
        return null;
    }


    private Vector3f[] TMP_VEC = CollectionUtils.fill(new Vector3f[8], Vector3f::new);

    private boolean isContainsOrigin_updateSimplex__updateDirection(List<SupportPoint> simplex, Vector3f d) {
        final int A=0, B=1, C=2, D=3;
        switch (simplex.size()) {
            case 1: { // updateDirection -> let area big as possible
                d.negate();
                return false;
            }
            case 2: { // updateDirection -> prep of AB to origin
                Vector3f AB = Vector3f.sub(simplex.get(B).point, simplex.get(A).point, TMP_VEC[0]);
                Vector3f AO = Vector3f.sub(Vector3f.ZERO,        simplex.get(A).point, TMP_VEC[1]);
                Vector3f perp = Vector3f.cross(Vector3f.cross(AB, AO, TMP_VEC[2]), AB, TMP_VEC[2]); // a perpendicular(dir) (of AB) pointing to the origin
                if (Maths.fuzzyZero(perp.lengthSquared())) // AB, AO parallels.  or either/both is ZeroVector
                    perp.set(Vector3f.UNIT_X);
                d.set(perp);
                return false;
            }
            case 3: { // updateDirection -> norm of ABC to origin
                Vector3f AB = Vector3f.sub(simplex.get(B).point, simplex.get(A).point, TMP_VEC[0]);
                Vector3f AC = Vector3f.sub(simplex.get(C).point, simplex.get(A).point, TMP_VEC[1]);
                Vector3f norm = Vector3f.cross(AB, AC, TMP_VEC[2]);
                if (Maths.fuzzyZero(norm.lengthSquared())) // 2body compeletly point2point overlapping
                    norm.set(Vector3f.UNIT_X);
                Vector3f AO = Vector3f.sub(Vector3f.ZERO, simplex.get(A).point, TMP_VEC[3]);
                if (Vector3f.dot(norm, AO) < 0) // make sure the triangle norm facing/pointing to the origin
                    norm.negate();
                d.set(norm);
                return false;
            }
            case 4: { // isContainsOrigin, updateDirection, updateSimplex    // ignored ABC triangle-side detection, because the Origin is certianly in D side of ABC
                Vector3f DA = Vector3f.sub(simplex.get(A).point, simplex.get(D).point, TMP_VEC[0]);
                Vector3f DB = Vector3f.sub(simplex.get(B).point, simplex.get(D).point, TMP_VEC[1]);
                Vector3f DC = Vector3f.sub(simplex.get(C).point, simplex.get(D).point, TMP_VEC[2]);

                Vector3f ABDNorm = Vector3f.cross(DA, DB, TMP_VEC[3]);  // norm should pointing outward (from origin)
                Vector3f BCDNorm = Vector3f.cross(DB, DC, TMP_VEC[4]);
                Vector3f CADNorm = Vector3f.cross(DC, DA, TMP_VEC[5]);

                // ensure the norm is facing to outer.
                Vector3f ABCDCenter = TMP_VEC[6].set(simplex.get(A).point).add(simplex.get(B).point).add(simplex.get(C).point).add(simplex.get(D).point).scale(1/4f);
                Vector3f DCenter = Vector3f.sub(ABCDCenter, simplex.get(D).point, TMP_VEC[6]);
                if (Vector3f.dot(DCenter, ABDNorm) > 0) { // when ABDNorm facing to inner
                    ABDNorm.negate();
                    BCDNorm.negate();
                    CADNorm.negate();
                }

                Vector3f DO = Vector3f.sub(Vector3f.ZERO, simplex.get(D).point, TMP_VEC[7]);

                // detect 3 FaceNorm-toOrigin-Projection
                if (Vector3f.dot(ABDNorm, DO) >= 0) {
                    simplex.remove(C);
                    d.set(ABDNorm);  if (d.lengthSquared()==0)d.set(Vector3f.UNIT_X);
                    return false;
                } else if (Vector3f.dot(BCDNorm, DO) >= 0) {
                    simplex.remove(A);
                    d.set(BCDNorm);  if (d.lengthSquared()==0)d.set(Vector3f.UNIT_X);
                    return false;
                } else if (Vector3f.dot(CADNorm, DO) >= 0) {
                    simplex.remove(B);
                    d.set(CADNorm);  if (d.lengthSquared()==0)d.set(Vector3f.UNIT_X);
                    return false;
                } else {
                    return true;
                }
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

}
