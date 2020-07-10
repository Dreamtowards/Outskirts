package outskirts.physics.collision.narrowphase.collisionalgorithm;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;

/**
 * The-Really-CollisionDetectionAlgorithm (in narrowphase)
 * not have narrowphase prefix because really collision detection algorithm are only in narrowphase,
 * broadphase/midphase mainly more tend to a filter type algorithm.
 * so the narrowphase prefix for CollisionAlgorithm is unnecessary
 */
public abstract class CollisionAlgorithm {

    // name as bodyA and bodyB .? num may is not vivid enough like normOnB
    /**
     * discrete collision detection.
     * (dosen't needs return true/false if had collision. manifold will records collision results. even how much collision the difference.)
     */
    public abstract void detectCollision(CollisionObject bodyA, CollisionObject bodyB, CollisionManifold manifold);

}
