package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.client.gui.screen.tools.GuiScreen3DVertices;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.physics.collision.shapes.convex.TriangleShape;
import outskirts.util.Colors;
import outskirts.util.vector.Vector3f;

public class CollisionAlgorithmConvexConcave extends CollisionAlgorithm {

    private TriangleShape trigshape = new TriangleShape();

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {
        CollisionObject bodyConvex, bodyConcave;
        if (bodyA.getCollisionShape() instanceof ConvexShape) {
            bodyConvex=bodyA;bodyConcave=bodyB;
        } else {
            bodyConvex=bodyB;bodyConcave=bodyA;
        }

        ConcaveShape concaveShape = (ConcaveShape)bodyConcave.getCollisionShape();

        concaveShape.processAllTriangles((trig, idx) -> {

            bodyConcave.setCollisionShape(trigshape.setVertices(trig[0], trig[1], trig[2]));  // tmp set
            manifold.narrowphase.detectCollision(manifold);
            bodyConcave.setCollisionShape(concaveShape); // setback

        }, bodyConvex.getAABB());

    }
}
