package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.physics.collision.shapes.convex.TriangleShape;
import outskirts.util.vector.Matrix3f;

public final class CollisionAlgorithmConvexConcave extends CollisionAlgorithm {

    private TriangleShape trigshape = new TriangleShape();
    private AABB tmpAabb = new AABB();  // tmpConcaveSpaceAabb

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {
        CollisionObject convexbody, concavebody; if (bodyA.getCollisionShape() instanceof ConvexShape) { convexbody=bodyA;concavebody=bodyB; } else { convexbody=bodyB;concavebody=bodyA; }
        ConcaveShape concaveshape = (ConcaveShape)concavebody.getCollisionShape();
        assert concavebody.transform().basis.equals(Matrix3f.IDENTITY) : "Concave Rotations is not supported.";
        int n = manifold.cpAdded;

        // convexbody aabb relative to the concavebody aabb. in concavebody localspace.  i.e. the (Probably)Collide-Area.
        AABB localCollidedAabb = tmpAabb.set(convexbody.getAABB()).translate(-1, concavebody.transform().origin);
        concaveshape.collideTriangles(localCollidedAabb, (idx, trig) -> {
            if (manifold.cpAdded-n >= CollisionManifold.MAX_CONTACT_POINTS) // sometimes had lots lite triangles. when ContactPoints detected enought this time, just dosen't needs more detection.
                return;
            // the actually worldspace C.D.
            concavebody.setCollisionShape(trigshape.setVertices(trig[0], trig[1], trig[2]));  // tmp set
            manifold.narrowphase.detectCollision(manifold);
            concavebody.setCollisionShape(concaveshape); // setback
        });
    }

}
