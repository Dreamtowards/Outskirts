package outskirts.util;

import outskirts.block.Block;
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

// have not really implement now yet
@SideOnly(Side.CLIENT)
public class RayPicker {

    private Vector3f currentPoint = new Vector3f();
    private Entity currentEntity;
    private Vector3f rayOrigin = new Vector3f();
    private Vector3f rayDirection = new Vector3f(Vector3f.UNIT_X);

    private Vector3f currentBlockPos;
    private Vector3f prevousBlockPos;  // for detect block-switching

    public void update() {
        Ref<Float> t = Ref.wrap(Float.MAX_VALUE);
        float collT = Float.MAX_VALUE;  // t of only really exact raycast.
        List<Entity> excepts = new ArrayList<>(Collections.singletonList(Outskirts.getPlayer()));

        currentEntity=null;
        while (true) {
            Entity found = getCloserAabbEntity(rayOrigin, rayDirection, t, excepts);
            if (found != null) {
                excepts.add(found);
                CollisionShape collshape = found.getRigidBody().getCollisionShape();
                if (collshape instanceof Raycastable) {
                    Ref<Float> tmp = Ref.wrap();
                    Vector3f relpos = new Vector3f(rayOrigin).sub(found.getRigidBody().transform().origin);
                    try {
                        if (((Raycastable)collshape).raycast(relpos, rayDirection, tmp)) {  // rayDirection modelMatrix rot.
                            // the casted result is more 'Near'.
                            if (tmp.value < collT) {
                                currentEntity=found;
                                collT = tmp.value;
                                t.value = tmp.value;
                            }
                        } else {
                            // not-cast. find from 'start'. the actually casted aabb-t should behind curr aabb-t.
                            t.value = Float.MAX_VALUE;
                        }
                    } catch (Vector3f.IllegalTriangleException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }
            }
            break;
        }
        if (currentEntity != null) {  // found.
            currentPoint.set(rayOrigin).addScaled(collT, rayDirection);
        }

        prevousBlockPos=currentBlockPos;
        currentBlockPos=null;
        if (currentEntity != null) {
            currentBlockPos = new Vector3f(currentPoint).addScaled(0.0001f, rayDirection);
            currentBlockPos.set(Maths.floor(currentBlockPos.x), Maths.floor(currentBlockPos.y), Maths.floor(currentBlockPos.z));
            if (Outskirts.getWorld().getBlock(currentBlockPos) == null) {
                currentBlockPos = null;
            }
        }
    }

    private Entity getCloserAabbEntity(Vector3f raypos, Vector3f raydir, Ref<Float> thanT, List<Entity> excepts) {
        Vector2f TMP = new Vector2f();
        Entity closestAabbEntity = null;
        for (Entity entity : Outskirts.getWorld().getEntities()) {
            if (excepts.contains(entity)) continue;
            if (Maths.intersectRayAabb(raypos, raydir, entity.getRigidBody().getAABB(), TMP) && TMP.x < thanT.value) {
                thanT.value = TMP.x;
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

    public Vector3f getCurrentBlockPos() {
        return currentBlockPos;
    }
    public Block getCurrentBlock() {
        return Outskirts.getWorld().getBlock(currentBlockPos);
    }
    public boolean isBlockSwitched() {
        return !Objects.equals(prevousBlockPos, currentBlockPos);
    }
}
