package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.vector.Vector3f;

// a.k.a. SpheroidShape.
public class EllipsoidShape extends ConvexShape {

    // semi-axes
    private Vector3f radius = new Vector3f();

    public EllipsoidShape(Vector3f radius) {
        getRadius().set(radius);
    }

    public Vector3f getRadius() {
        return radius;
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        return dest.set(d.x*radius.x, d.y*radius.y, d.z*radius.z);
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(radius);
    }

    /**
     * Solid Ellipsoid
     * I_x = 1/5m(y^2 + z^2)
     * I_y = 1/5m(x^2 + z^2)
     * I_z = 1/5m(x^2 + y^2)
     */
    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        float f = 1/5f * mass;
        float xSq = radius.x * radius.x;
        float ySq = radius.y * radius.y;
        float zSq = radius.z * radius.z;
        return dest.set(f*(ySq+zSq), f*(xSq+zSq), f*(xSq+ySq));
    }
}
