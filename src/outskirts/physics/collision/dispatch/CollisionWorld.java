package outskirts.physics.collision.dispatch;

import outskirts.physics.collision.broadphase.Broadphase;
import outskirts.physics.collision.broadphase.BroadphaseDbvt;
import outskirts.physics.collision.broadphase.BroadphaseSimple;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.narrowphase.Narrowphase;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CollisionWorld {

    protected List<CollisionObject> collisionObjects = new ArrayList<>();

    protected Broadphase broadphase = new BroadphaseSimple();

    protected Narrowphase narrowphase = new Narrowphase();


    public void addCollisionObject(CollisionObject collisionObject) {
        Validate.isTrue(!collisionObjects.contains(collisionObject), "This CollisionObject already existed.");

        collisionObjects.add(collisionObject);
    }

    public void removeCollisionObject(CollisionObject collisionObject) {
        collisionObjects.remove(collisionObject);
    }

    public List<CollisionObject> getCollisionObjects() {
        return Collections.unmodifiableList(collisionObjects);
    }


    public final Broadphase getBroadphase() {
        return broadphase;
    }

    public final Narrowphase getNarrowphase() {
        return narrowphase;
    }


    protected final void updateAABBs() {
        for (CollisionObject body : getCollisionObjects()) {
            body.getCollisionShape().getAABB(body.getAABB(), body.transform());

            broadphase.updateAABB(body);
        }
    }
}
