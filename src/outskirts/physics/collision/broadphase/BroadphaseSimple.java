package outskirts.physics.collision.broadphase;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class BroadphaseSimple extends Broadphase {

    private List<CollisionObject> broadphaseObjects = new ArrayList<>();

    @Override
    public void addObject(CollisionObject body) {
        broadphaseObjects.add(body);
    }

    @Override
    public void removeObject(CollisionObject body) {
        broadphaseObjects.remove(body);
        removePairsContainingBody(body);
    }

    @Override
    public void calculateOverlappingPairs() {

        for (CollisionObject body1 : broadphaseObjects) {
            for (CollisionObject body2 : broadphaseObjects) {
                if (body1 == body2) continue;

                int idx = CollisionManifold.indexOf(getOverlappingPairs(), body1, body2);

                if (AABB.intersects(body1.getAABB(), body2.getAABB())) { // overlapping
                    if (idx == -1) // !containsPair
                        getOverlappingPairs().add(new CollisionManifold((RigidBody)body1, (RigidBody)body2));
                } else if (idx != -1) {
                    getOverlappingPairs().remove(idx);
                    Log.LOGGER.info("rem pair");
                }
            }
        }
    }
}
