package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.physics.collision.shapes.convex.TriangleShape;
import outskirts.util.vector.Matrix3f;

public class CollisionAlgorithmConvexConcave extends CollisionAlgorithm {

    private TriangleShape trigshape = new TriangleShape();
    private AABB tmpConcaveSpaceAABB = new AABB();
    private int tmpProducedCPsNum;

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {
        CollisionObject convexBody, concaveBody;
        if (bodyA.getCollisionShape() instanceof ConvexShape) { convexBody=bodyA;concaveBody=bodyB; } else { convexBody=bodyB;concaveBody=bodyA; }
        assert concaveBody.transform().basis.equals(Matrix3f.IDENTITY) : "Concave Rotations was not supports.";

        ConcaveShape concaveShape = (ConcaveShape)concaveBody.getCollisionShape();

//        GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();
        tmpProducedCPsNum=0;
        concaveShape.processAllTriangles((trig, idx) -> {
            if (tmpProducedCPsNum >= CollisionManifold.MAX_CONTACT_POINTS) // sometimes had lots lite triangles. when ContactPoints enought this time , just dosen't needs more detection.
                return;

            concaveBody.setCollisionShape(trigshape.setVertices(trig[0], trig[1], trig[2]));  // tmp set
            int i = manifold.narrowphase.detectCollision(manifold);
            concaveBody.setCollisionShape(concaveShape); // setback

            tmpProducedCPsNum += i;
//            GuiScreen3DVertices.addTri("", new Vector3f(trig[0]).add(concaveBody.transform().origin), new Vector3f(trig[1]).add(concaveBody.transform().origin), new Vector3f(trig[2]).add(concaveBody.transform().origin), i>0?Colors.YELLOW:Colors.WHITE, null);

        }, tmpConcaveSpaceAABB.set(convexBody.getAABB()).translateScaled(-1f, concaveBody.transform().origin)); // aabb in Concave-Space.

    }
}
