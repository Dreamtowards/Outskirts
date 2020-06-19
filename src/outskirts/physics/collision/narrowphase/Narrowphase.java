package outskirts.physics.collision.narrowphase;

import outskirts.physics.collision.dispatch.CollisionManifold;
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

        throw new UnsupportedOperationException("No CollisionAlgorithm for this pair.");
    }

    // dispatch narrowphase the really collision detection
    /**
     * @return num detected ContactPoints
     */
    public int detectCollision(CollisionManifold manifold) {
        if (manifold.narrowphase == null)
            manifold.narrowphase = this;

        CollisionAlgorithm algorithm = findAlgorithm(manifold.bodyA().getCollisionShape(), manifold.bodyB().getCollisionShape());

        int n = manifold.cpAddedCount;

        // collision detection queries
        algorithm.detectCollision(manifold.bodyA(), manifold.bodyB(), manifold);

        return manifold.cpAddedCount - n;
    }

    public final List<CollisionManifold> detectCollisions(List<CollisionManifold> manifolds) {
        List<CollisionManifold> l = new ArrayList<>();
        for (CollisionManifold manifold : manifolds) {
            if (detectCollision(manifold) > 0)
                l.add(manifold);
        }
        return l;
    }
}
