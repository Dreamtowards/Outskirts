package outskirts.physics.collision.narrowphase;

import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.narrowphase.collisionalgorithm.CollisionAlgorithm;
import outskirts.physics.collision.narrowphase.collisionalgorithm.CollisionAlgorithmConvexConcave;
import outskirts.physics.collision.narrowphase.collisionalgorithm.CollisionAlgorithmConvexConvex;
import outskirts.physics.collision.narrowphase.collisionalgorithm.CollisionAlgorithmSphereSphere;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.ConcaveShape;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.physics.collision.shapes.convex.SphereShape;

import java.util.ArrayList;
import java.util.List;

/**
 * not like Broadphase, this Narrowphase commonly do not needs extends, that almost just a dispatcher.
 * the point of narrowphase is in CollisionAlgorithm
 */
public class Narrowphase {

    // there just tmp CA fields for simple pre ext.test
    private CollisionAlgorithmSphereSphere collisionAlgorithmSphereSphere = new CollisionAlgorithmSphereSphere();
    private CollisionAlgorithmConvexConvex collisionAlgorithmConvexConvex = new CollisionAlgorithmConvexConvex();
    private CollisionAlgorithmConvexConcave collisionAlgorithmConvexConcave = new CollisionAlgorithmConvexConcave();

    // requires immediate speed.
    protected CollisionAlgorithm findAlgorithm(CollisionShape shape1, CollisionShape shape2) {
        if (shape1 instanceof SphereShape && shape2 instanceof SphereShape)
            return collisionAlgorithmSphereSphere;
        if (shape1 instanceof ConvexShape && shape2 instanceof ConvexShape)
            return collisionAlgorithmConvexConvex;
        if ((shape1 instanceof ConvexShape && shape2 instanceof ConcaveShape) || (shape1 instanceof ConcaveShape && shape2 instanceof ConvexShape))
            return collisionAlgorithmConvexConcave;

//        return new CollisionAlgorithm() { // not this. when bodies dont needsCollision, just exlusion in Broadphase.
//            @Override
//            public void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold) {
//                // empty
//            }
//        };
        throw new UnsupportedOperationException("No CollisionAlgorithm for this pair.");
    }

    // dispatch narrowphase the really collision detection

    /**
     * @return detected/'added' ContactPoints count, during this time. >= 0.
     * (note that when == 0 dosen't means No-Collision.(.?) the No-Collision manifold just when manifold.getNumContactPoints() == 0.
     */
    public int detectCollision(CollisionManifold manifold) {
        if (manifold.narrowphase == null)
            manifold.narrowphase = this;
        CollisionAlgorithm algorithm = findAlgorithm(manifold.bodyA().getCollisionShape(), manifold.bodyB().getCollisionShape());
        int n = manifold.cpAdded;

        // collision detection queries
        algorithm.detectCollision(manifold.bodyA(), manifold.bodyB(), manifold);

        manifold.refreshContactPoints(); // adjust/clear manifold contactpoints.

        return manifold.cpAdded - n;
    }

    /**
     * @return manifolds which still had ContcatPoints. (still had Collision.)
     */
    public final List<CollisionManifold> detectCollisions(List<CollisionManifold> manifolds) {
        List<CollisionManifold> l = new ArrayList<>();
        for (CollisionManifold manifold : manifolds) {

            detectCollision(manifold);

            if (manifold.getNumContactPoints() > 0)  // or just (i=detectCollision(manifold)) > 0 .?
                l.add(manifold);
        }
        return l;
    }
}
