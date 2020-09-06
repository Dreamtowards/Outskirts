package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.physics.collision.shapes.convex.TriangleShape;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;

public final class CollisionAlgorithmConvexConcave extends CollisionAlgorithm {

    private TriangleShape trigshape = new TriangleShape();
    private AABB tmpAabb = new AABB();  // tmpConcaveSpaceAabb

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {
        CollisionObject convexbody, concavebody; if (bodyA.getCollisionShape() instanceof ConvexShape) { convexbody=bodyA;concavebody=bodyB; } else { convexbody=bodyB;concavebody=bodyA; }
        ConcaveShape concaveshape = (ConcaveShape)concavebody.getCollisionShape();
        assert concavebody.transform().basis.equals(Matrix3f.IDENTITY) : "Concave Rotations is not supported.";

        // convexbody aabb relative to the concavebody aabb. in concavebody localspace.  i.e. the (Probably)Collide-Area.
        AABB localCollidedAabb = tmpAabb.set(convexbody.getAABB()).translate(-1, concavebody.transform().origin);
        concaveshape.collideTriangles(localCollidedAabb, (idx, trig) -> {
            // actually needs more detection. even may more than 4 (MAX_CONTACT_POINTS) at once concave vs convex.
            // because if just get first 4 CP and do not care latter CPs, may first 4CPs is just a little part of the collision. this makes CD not complete enough.

            // the actually worldspace C.D.
            concavebody.setCollisionShape(trigshape.setVertices(trig[0], trig[1], trig[2]));  // tmp set
            manifold.narrowphase.detectCollision(manifold);
            concavebody.setCollisionShape(concaveshape); // setback
        });
    }

}
