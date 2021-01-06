package outskirts.physics.collision.shapes;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

public final class GhostShape extends CollisionShape {

    @Override
    protected AABB getAABB(AABB dest) {
        return dest.set(AABB.ZERO);  // any. do not matter. this shape just do not needsCollision() in N.P. at all.
    }

    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        return dest.set(0, 0, 0);
    }
}
