package outskirts.util.mx;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.logging.Log;

public class PlacementUtils {

    // assume the origin in Center AABB.
    public static void setOnTopOf(RigidBody bodyUp, RigidBody bodyFloor) {
        AABB buAABB = bodyUp.refreshAABB();
        AABB bfAABB = bodyFloor.refreshAABB();

        Log.LOGGER.info(bfAABB.max.y);
        bodyUp.transform().origin.y = bfAABB.max.y + (buAABB.max.y-buAABB.min.y)/2f;
    }

}
