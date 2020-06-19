package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

/**
 * a simple example for CollisionAlgorithm class struc
 */
public class CollisionAlgorithmSphereSphere extends CollisionAlgorithm {

    @Override
    public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {

        Vector3f diff = Vector3f.sub(bodyA.transform().origin, bodyB.transform().origin, null);

        float diffLen = diff.length();
        float radA = ((SphereShape)bodyA.getCollisionShape()).getRadius();
        float radB = ((SphereShape)bodyB.getCollisionShape()).getRadius();

        // not collision
        if (diffLen >= radA+radB)
            return; // false

        // > 0
        float penetration = (radA+radB) - diffLen; // -(diffLen - (radA+radB))

        Vector3f normOnB = Maths.fuzzyZero(diffLen) ? new Vector3f(1, 0, 0) : new Vector3f(diff).normalize();

        // worldpoint on B
        Vector3f pointOnB = new Vector3f(bodyB.transform().origin).addScaled(radB, normOnB);

        manifold.addContactPoint(normOnB, penetration, pointOnB);

    }
}
