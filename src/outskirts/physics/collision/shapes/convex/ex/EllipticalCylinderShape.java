package outskirts.physics.collision.shapes.convex.ex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

public class EllipticalCylinderShape extends ConvexShape {

    private static final float SQRT2 = 1.4142135624f;

    // [radius-x, halfheight, radius-z]
    private Vector3f axes = new Vector3f();

    public EllipticalCylinderShape(Vector3f axes) {
        getAxes().set(axes);
    }

    public Vector3f getAxes() {
        return axes;
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        if (d.x==0 && d.z==0)
            return dest.set(axes.x/SQRT2, d.y>=0?axes.y: -axes.y, axes.z/SQRT2);
        float hLen = Maths.sqrt(d.x*d.x + d.z*d.z);
        return dest
                .set(d.x/hLen *axes.x, d.y>=0?axes.y: -axes.y, d.z/hLen *axes.z);
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(axes);
    }

    /**
     * Solid cylinder
     * I_y = 1/2mr^2
     * I_x = I_z = 1/12m(3r^2 + h^2)
     * there Elliptical is Unoffical.
     */
    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        float xSq = axes.x*axes.x;
        float zSq = axes.z*axes.z;
        float hSq = (axes.y*2)*(axes.y*2);
        return dest.set(
                1/12f*mass *(3f*zSq + hSq),
                1/2f*mass * axes.x*axes.z,
                1/12f*mass *(3f*xSq + hSq)
        );
    }
}
