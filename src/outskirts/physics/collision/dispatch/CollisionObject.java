package outskirts.physics.collision.dispatch;

import outskirts.util.Transform;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.CollisionShape;
import outskirts.physics.collision.shapes.convex.BoxShape;
import outskirts.util.vector.Vector3f;

public abstract class CollisionObject {

    /**
     * world space/coordinate Transform
     * transform.origin == center of mass
     */
    protected Transform transform = new Transform();

    private CollisionShape collisionShape;

    //should be keep away in broadphase..?
    private AABB aabb = new AABB();

    public Object broadphaseAttachment;


    public CollisionShape getCollisionShape() {
        return collisionShape;
    }
    public CollisionObject setCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
        return this;
    }

    public final AABB getAABB() {
        return aabb;
    }
    public final AABB refreshAABB() {  // needs.?
        return getCollisionShape().getAABB(getAABB(), transform());
    }

    // should getWorldTransform() ..?
    public Transform transform() {
        return transform;
    }
}
