package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

// Y-Up
public class CylinderShape extends ConvexShape {

    private float radius;
    private float halfHeight;

    public CylinderShape(float radius, float halfHeight) {
        this.radius = radius;
        this.halfHeight = halfHeight;
    }

    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getHalfHeight() {
        return halfHeight;
    }
    public void setHalfheight(float halfHeight) {
        this.halfHeight = halfHeight;
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        if (d.x==0 && d.z==0)
            return dest.set(radius, d.y>=0?halfHeight: -halfHeight, 0);
        return dest
                .set(d.x, 0, d.z)
                .normalize()
                .scale(radius)
                .setY(d.y>=0?halfHeight: -halfHeight);
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(radius, halfHeight, radius);
    }

    /**
     * Solid cylinder
     * I_y = 1/2mr^2
     * I_x = I_z = 1/12m(3r^2 + h^2)
     */
    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        float rSq = radius*radius;
        float hSq = (halfHeight*2)*(halfHeight*2);
        float v = 1/2f*mass*rSq;
        float h = 1/12f*mass*(3f*rSq + hSq);
        return dest.set(h, v, h);
    }
}
