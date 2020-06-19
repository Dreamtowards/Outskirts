package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;

// Y-Up
public class ConeShape extends ConvexShape {

    private float radius;
    private float halfHeight;

    private float sinAngle;

    public ConeShape(float radius, float halfHeight) {
        setRadius(radius);
        setHalfHeight(halfHeight);
    }

    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
        recalcSinAngle();
    }

    public float getHalfHeight() {
        return halfHeight;
    }
    public void setHalfHeight(float halfHeight) {
        this.halfHeight = halfHeight;
        recalcSinAngle();
    }

    private void recalcSinAngle() {
        sinAngle = radius / Maths.sqrt(radius*radius + (halfHeight*2f)*(halfHeight*2f));
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        if (d.y >= sinAngle)
            return dest.set(0, halfHeight, 0);
        if (d.x==0 && d.z==0)
            return dest.set(radius, -halfHeight, 0);
        return dest
                .set(d.x, 0, d.z)
                .normalize()
                .scale(radius)
                .setY(-halfHeight);
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(radius, halfHeight, radius);
    }

    //todo: tests uses Box-InertiaTensor.?
    /**
     * Right Circular Cone. Bottom.
     * I_xz = 1/10mh^2 + 3/20mr^2
     * I_y  = 3/10mr^2
     */
    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        float hSq = (halfHeight*2f)*(halfHeight*2f);
        float rSq = radius*radius;
        float v = 3/10f*mass*rSq;
        float h = 1/10f*mass*hSq + 3/20f*mass*rSq;
        return dest.set(h, v, h);
    }
}
