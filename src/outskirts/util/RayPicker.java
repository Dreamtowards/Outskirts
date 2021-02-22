package outskirts.util;

import outskirts.client.Outskirts;
import outskirts.entity.Entity;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.Raycastable;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static outskirts.util.logging.Log.LOGGER;

// have not really implement now yet
@SideOnly(Side.CLIENT)
public class RayPicker {

    private Vector3f currentPoint = new Vector3f();
    private Entity currentEntity;
    private Vector3f rayOrigin = new Vector3f();
    private Vector3f rayDirection = new Vector3f(Vector3f.UNIT_X);



    public void update(Vector3f rpos, Vector3f rdir) {
        rayOrigin.set(rpos);
        rayDirection.set(rdir);
        update();
    }

    public void update() {
        Val t = Val.of(Float.MAX_VALUE);
        float collT = Float.MAX_VALUE;  // t of only really exact raycast.
        List<Entity> excepts = new ArrayList<>(Collections.singletonList(Outskirts.getPlayer()));

        currentEntity=null;
        while (true) {
            Entity found = getCloserAabbEntity(rayOrigin, rayDirection, t, excepts);
            if (found != null) {
                excepts.add(found);
                CollisionShape collshape = found.getRigidBody().getCollisionShape();
                if (collshape instanceof Raycastable) {
                    Val tmp =Val.zero();
                    Vector3f relpos = new Vector3f(rayOrigin).sub(found.getRigidBody().transform().origin);
                    try {
                        if (((Raycastable)collshape).raycast(relpos, rayDirection, tmp)) {  // rayDirection modelMatrix rot.
                            // the casted result is more 'Near'.
                            if (tmp.val < collT) {
                                currentEntity=found;
                                collT = tmp.val;
                                t.val = tmp.val;
                            }
                        } else {
                            // not-cast. find from 'start'. the actually casted aabb-t should behind curr aabb-t.
                            t.val = Float.MAX_VALUE;
                        }
                    } catch (ArithmeticException ex) {
                        LOGGER.warn("RayPicker failed raycast: "+ex.getMessage());
                    }
                    continue;
                }
            }
            break;
        }
        if (currentEntity != null) {  // found.
            currentPoint = new Vector3f(rayOrigin).addScaled(collT, rayDirection);
        } else {
            currentPoint = null;
        }
    }

    private Entity getCloserAabbEntity(Vector3f raypos, Vector3f raydir, Val thanT, List<Entity> excepts) {
        Vector2f TMP = new Vector2f();
        Entity closestAabbEntity = null;
        for (Entity entity : Outskirts.getWorld().getEntities()) {
            if (excepts.contains(entity)) continue;
            if (Maths.intersectRayAabb(raypos, raydir, entity.getRigidBody().getAABB(), TMP) && TMP.x < thanT.val) {
                thanT.val = TMP.x;
                closestAabbEntity = entity;
            }
        }
        return closestAabbEntity;
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }

    public Vector3f getCurrentPoint() {
        return currentPoint;
    }

    public Vector3f getRayOrigin() {
        return rayOrigin;
    }

    public Vector3f getRayDirection() {
        return rayDirection;
    }

}
