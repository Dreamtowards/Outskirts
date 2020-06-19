package outskirts.physics.collision.narrowphase.collisionalgorithm.epa;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.narrowphase.collisionalgorithm.gjk.Gjk;
import outskirts.util.Maths;
import outskirts.util.QuickExitException;
import outskirts.util.Validate;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.*;

/**
 * Expanding-Polytope-Algorithm.
 *
 * for 2 convex overlapping situation in 3D,
 * we say the Closest-Point is a point closest to the Origin on the boundary of Minkowski Difference.
 * and the Closest-Point is always on a Feature(Triangle in 3D, Edge in 2D) of the boundary, i.e the point always is not directly a Vert.point in Simplex.
 *
 * then, the vector of the Closest-Point's length is the {Collision-Penetration-Depth},
 * and the vector itself is the Separating-Vector or the normalize of the vector is the {Collision-Normal}.
 *
 * Additionly, the {Contact-Point} is the Closest-Point's localed Feature's Vertices's correspounding FarthestPoint/SupportPoint-OnSameEitherBody's
 * weighted sum by BarycentricCoordinateCoefficient of the Closest-Point of its localed Feature.
 *
 * which (in 3D):
 * penetrationDepth = length(closestTriangle.closestPoint); {or} dot(closestTriangle.normal, closestTriangle.v0);
 * normal = normalize(closestTriangle.closestPoint); {or} closestTriangle.normal
 *
 * _ext_barycentric = calculateBarycentricCoordinateCoefficient(closestTriangle.closestPoint, closestTriangle.v0,v1,v2);
 * pointOnB = vec3(_ext_barycentric.x*closestTriangle.v0.theFarthestPointOnBodyB) +
 *            vec3(_ext_barycentric.y*closestTriangle.v1.theFarthestPointOnBodyB) +
 *            vec3(_ext_barycentric.z*closestTriangle.v2.theFarthestPointOnBodyB);
 *
 * Moreover, for get pointOnA which is Contact-Point-OnBodyA is just applies/add the CollisionNormalOnB*CollisionPenetrationDepth vector to the pointOnB vector.
 */
public class Epa {

    // MinimumTransformationVector
    public static final class MTV {
        private MTV() {}
        public float penetration;                  // overlapping > 0, touching == 0, separating < 0.  dependent the Actually MinkowskiDifference.
        public Vector3f normal = new Vector3f();   // worldspace separating normal "on bodyA" (pointing to bodyB
        public Vector3f pointOnB = new Vector3f(); // worldspace ContactPoint-OnB.

        private static MTV fromClosestTriangle(Triangle trig) {
            MTV mtv = new MTV();
            mtv.penetration = trig.distance;
            mtv.normal.set(trig.normal);

            Vector3f closestPoint = Maths.findClosestPointOnTriangle(Vector3f.ZERO, trig.v0.point, trig.v1.point, trig.v2.point, new Vector3f()); // the Closest-Point on the Triangle to the Origin
            Vector3f closestPointBarycentricCoordinates = Maths.calculateBarycentric(closestPoint, trig.v0.point, trig.v1.point, trig.v2.point, new Vector3f());
            mtv.pointOnB.addScaled(closestPointBarycentricCoordinates.x, trig.v0.farthestpointOnB)
                        .addScaled(closestPointBarycentricCoordinates.y, trig.v1.farthestpointOnB)
                        .addScaled(closestPointBarycentricCoordinates.z, trig.v2.farthestpointOnB);
            return mtv;
        }
    }

    /**
     * Compute Closest Points on the Simplex.
     *
     * there should had a Iteration limit. when expanding Surve shape's MinkowskiDifference, like sphere, iteration may get to bigger.
     *
     * @param simplex 4 vertices simplex, contains the origin. always from Gjk passed-simplex.
     */
    public MTV computeMTV(CollisionObject bodyA, CollisionObject bodyB, List<Gjk.SupportPoint> simplex) {
        List<Triangle> triangles = new ArrayList<>();

        // helps correct the triangle normal. even the Origin is in outside of MinkowDiff. but failed when point/line/plane Simplex.
        Vector3f _ABCDCenter = new Vector3f(simplex.get(0).point).add(simplex.get(1).point).add(simplex.get(2).point).add(simplex.get(3).point).scale(1/4f);

        triangles.add(newTriangle(simplex.get(0), simplex.get(1), simplex.get(2), _ABCDCenter)); // ABC
        triangles.add(newTriangle(simplex.get(0), simplex.get(1), simplex.get(3), _ABCDCenter)); // ABD
        triangles.add(newTriangle(simplex.get(1), simplex.get(2), simplex.get(3), _ABCDCenter)); // BCD
        triangles.add(newTriangle(simplex.get(2), simplex.get(0), simplex.get(3), _ABCDCenter)); // CAD

        int itrN = 0;
        while (true) {
            if (++itrN > 10)
                Log.LOGGER.info("Err. computeMTV() out of itr.");
//            if (triangles.size() == 0)  // when Bad Simplex. all normal in one side. like "Plane" Simplex.
//                return null;

            Triangle trig = findClosestTriangleToOrigin(triangles);  // the Closest-Triangle. (to the Origin.)

            // the Point "Behand"(view from origin) the ClosestTriangle. keep variname simple.
            Gjk.SupportPoint furSp = Gjk.getSupportPoint(bodyA, bodyB, trig.normal);

            // the Vert dosen't Behand the ClosestTriangle anymore. Done.
            if (itrN > 10 || Vector3f.dot(trig.normal, furSp.point) <= trig.distance + 0.01f) {
                return MTV.fromClosestTriangle(trig);
            } else {
                doExpandSimplex(triangles, furSp, _ABCDCenter);
            }
        }
    }

    // Expand the Simplex (along the "furSp")
    private static void doExpandSimplex(List<Triangle> triangles, Gjk.SupportPoint furSp, Vector3f _ABCDCenter) {
        List<Gjk.SupportPoint[]> boundaryEdges = new ArrayList<>();

        // remove all the triangles which can been "seen" from the "furSp".
        // and records those removed triangles's Total-Boundary-Edges. (use for Expand the Simplex along the "furSp" in later.)
        for (int i = triangles.size()-1;i >= 0;i--) {
            Triangle t = triangles.get(i);

            // the "furSp" in the side of Triangle.normal. (dot(vert.point - t.v0.point, t.norm) > 0)
            if (Vector3f.dot(Vector3f.sub(furSp.point, t.v0.point, null), t.normal) > 0) {
                addBoundaryEdge(boundaryEdges, t.v0, t.v1);
                addBoundaryEdge(boundaryEdges, t.v1, t.v2);
                addBoundaryEdge(boundaryEdges, t.v2, t.v0);
                triangles.remove(i);
            }
        }

        // build Expansion
        for (Gjk.SupportPoint[] edge : boundaryEdges) {
            triangles.add(newTriangle(edge[0], edge[1], furSp, _ABCDCenter));
        }
    }
    /**
     * add Edge only when the list is not contains the edge. when contains, just remove that existed edge and do not add.
     */
    private static void addBoundaryEdge(List<Gjk.SupportPoint[]> boundaryEdges, Gjk.SupportPoint v0, Gjk.SupportPoint v1) {
        if (!boundaryEdges.removeIf(edge -> (edge[0]==v0 && edge[1]==v1) || (edge[0]==v1 && edge[1]==v0))) {
            boundaryEdges.add(new Gjk.SupportPoint[]{v0, v1});
        }
    }

    private static Triangle findClosestTriangleToOrigin(List<Triangle> triangles) {
        int idx = 0;
        for (int i = 1;i < triangles.size();i++) {
            if (triangles.get(i).distance < triangles.get(idx).distance)
                idx = i;
        }
        return triangles.get(idx);
    }
    private static final class Triangle {
        private Gjk.SupportPoint v0, v1, v2;      // 3 vertices of the Triangle. (from the Simplex
        private Vector3f normal = new Vector3f(); // pointing outward of simplex/triangle from the origin
        private float    distance;                // (closest) disnance from the origin. >= 0 (really ..? this is projection. but how < 0.
    }
    private static Triangle newTriangle(Gjk.SupportPoint v0, Gjk.SupportPoint v1, Gjk.SupportPoint v2, Vector3f _ABCDCenter) {
        Triangle trig = new Triangle();
        trig.v0 = v0; trig.v1 = v1; trig.v2 = v2;

        Vector3f normal = trig.normal;
        {   // Setup the Normal
            Vector3f v01 = Vector3f.sub(v1.point, v0.point, null); // v0 -> v1
            Vector3f v02 = Vector3f.sub(v2.point, v0.point, null); // v0 -> v2
            Vector3f.cross(v01, v02, normal); // arbitrary either side normal. (later checks side

            // normalize or set "DEF"
            if (normal.lengthSquared() == 0f) { // the triangle is a "line" or "point".
                normal.set(Vector3f.UNIT_X);
                normal.set(v0.point);
                Log.LOGGER.info("triangle zero len Norm");
            }
            normal.normalize(); // may should last do. this may makes values litter.

            // makesure the norm is pointing outward. when norm is pointing inward: norm.neagte()
            Vector3f v0Center = Vector3f.sub(_ABCDCenter, v0.point, null);
            if (Vector3f.dot(normal, v0Center) > 0) {
                normal.negate();
            }
            if (Maths.fuzzyZero(Vector3f.dot(normal, v0Center))) {
                Log.LOGGER.info("Risk: Plane Simplex");
            }
        }

        {   // Setup the Distance.
            trig.distance = Vector3f.dot(v0.point, normal);
        }
        return trig;
    }

}
