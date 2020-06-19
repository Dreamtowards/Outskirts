package assets;

import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.narrowphase.CollisionManifold;
import outskirts.physics.dynamics.RigidBody;

import java.util.List;

//todo: reduce useless class. just use Lazy-CollisionManifold .?
/**
 * BroadphaseOverlappingPersistentPair.
 * this object will keeping exixts in Broadphase-Overlapping.
 * create/destroy when Broadphase start/do-not overlapping
 *
 * split to a single package, for avoid types confusion. this is just a Entity Entry class.
 */
public final class BroadphasePair {

    // the Persistent CollisionManifold.
    private final CollisionManifold collisionManifold;

    public BroadphasePair(CollisionObject bodyA, CollisionObject bodyB) {
        this.collisionManifold = new CollisionManifold((RigidBody)bodyA, (RigidBody)bodyB);
    }

    public CollisionManifold manifold() {
        return collisionManifold;
    }

    public static int findPair(List<BroadphasePair> list, CollisionObject b1, CollisionObject b2) {
        for (int i = 0;i < list.size();i++) {
            BroadphasePair pair = list.get(i);
            if (pair.manifold().containsBody(b1) && pair.manifold().containsBody(b2))
                return i;
        }
        return -1;
    }
}
