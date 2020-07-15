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
    private int detectedCPs;

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {
        CollisionObject convexBody, concaveBody; if (bodyA.getCollisionShape() instanceof ConvexShape) { convexBody=bodyA;concaveBody=bodyB; } else { convexBody=bodyB;concaveBody=bodyA; }
        assert concaveBody.transform().basis.equals(Matrix3f.IDENTITY) : "Concave Rotations was not supports.";
        ConcaveShape concaveShape = (ConcaveShape)concaveBody.getCollisionShape();

        detectedCPs=0;
        concaveShape.processAllTriangles((trig, idx) -> {
            if (detectedCPs >= CollisionManifold.MAX_CONTACT_POINTS) // sometimes had lots lite triangles. when ContactPoints enought this time , just dosen't needs more detection.
                return;

            // the Real Worldspace CD.
            concaveBody.setCollisionShape(trigshape.setVertices(trig[0], trig[1], trig[2]));  // tmp set
            int i = manifold.narrowphase.detectCollision(manifold);
            concaveBody.setCollisionShape(concaveShape); // setback

            detectedCPs += i;
        }, tmpAabb.set(convexBody.getAABB()).translate(-1, concaveBody.transform().origin)); // convexBody aabb to the Concave.Space.
    }
}
