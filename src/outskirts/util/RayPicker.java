package outskirts.util;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import javax.annotation.Nullable;

// have not really implement now yet
public class RayPicker {

    private float rayLength = 10;

    private Vector3f currentPoint = new Vector3f();
    private Entity currentEntity;

    public final void update() {
        this.update(Outskirts.getCamera().getPosition(), Outskirts.getCamera().getCameraUpdater().getDirection());
    }

    private void update(Vector3f origin, Vector3f ray) {
        Vector2f TMP = new Vector2f();
        float closestv = Float.MAX_VALUE;
        currentEntity = null;
        for (Entity entity : Outskirts.getWorld().getEntities()) {
            if (entity == Outskirts.getPlayer()) continue;
            if (Maths.intersectRayAabb(origin, ray, entity.getRigidBody().getAABB(), TMP) && TMP.x < closestv) {
                closestv = TMP.x;
                currentEntity = entity;
                currentPoint.set(origin).addScaled(closestv, ray);
            }
        }
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }

    public Vector3f getCurrentPoint() {
        return currentPoint;
    }
}
