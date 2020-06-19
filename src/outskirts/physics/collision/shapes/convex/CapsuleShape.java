package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.vector.Vector3f;

// Y-Up
public class CapsuleShape extends ConvexShape {

    private float radius;
    private float halfHeight; // the actually total height is 2*halfHeight+2*radius.

    public CapsuleShape(float radius, float halfHeight) {
        this.radius = radius;
        this.halfHeight = halfHeight;
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        return dest
                .set(d).scale(radius)
                .add(0, d.y>=0?halfHeight: -halfHeight, 0);
    }

    @Override
    protected AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(radius, halfHeight+radius, radius);
    }

    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        return BoxShape.calculateBoxLocalInertia(mass, radius, halfHeight+radius, radius, dest);
    }
}
