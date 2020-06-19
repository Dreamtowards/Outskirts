package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.vector.Vector3f;

public class SphereShape extends ConvexShape {

    private float radius;

    public SphereShape(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        return dest
                .set(d)
                .scale(radius);
    }

    @Override
    public AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(radius, radius, radius);
    }

    /**
     * I = (2/5)mr^2
     */
    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f inertia) {
        float f = 0.4f * mass * getRadius() * getRadius();
        return inertia.set(f, f, f);
    }
}
