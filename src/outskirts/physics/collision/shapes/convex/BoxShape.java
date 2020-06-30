package outskirts.physics.collision.shapes.convex;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.util.vector.Vector3f;

public class BoxShape extends ConvexShape {

    // needs a more clear name..?
    private Vector3f halfExtent = new Vector3f();

    // can be simpleimize, this constructor may is not necessary
    public BoxShape(float hx, float hy, float hz) {
        getHalfExtent().set(hx, hy, hz);
    }

    public Vector3f getHalfExtent() {
        return halfExtent;
    }

    @Override
    public AABB getAABB(AABB dest) {
        return dest
                .set(Vector3f.ZERO, Vector3f.ZERO)
                .grow(getHalfExtent());
    }

    @Override
    public Vector3f getFarthestPoint(Vector3f d, Vector3f dest) {
        Vector3f h = getHalfExtent();
        return dest.set(
                d.x >= 0f ? h.x : -h.x,
                d.y >= 0f ? h.y : -h.y,
                d.z >= 0f ? h.z : -h.z
        );
    }

    @Override
    public Vector3f calculateLocalInertia(float mass, Vector3f dest) {
        return BoxShape.calculateBoxLocalInertia(mass, getHalfExtent(), dest);
    }

    /**
     * I_h = (1/12)m(w^2+d^2) :y
     * I_w = (1/12)m(d^2+h^2) :x
     * I_d = (1/12)m(w^2+h^2) :z
     * where h=y, w=x, d=z
     * @param hx,hy,hz Box Half-Extent
     */
    static Vector3f calculateBoxLocalInertia(float mass, float hx, float hy, float hz, Vector3f dest) {
        float xSq = (hx*2f) * (hx*2f);
        float ySq = (hy*2f) * (hy*2f);
        float zSq = (hz*2f) * (hz*2f);
        return dest.set(
                mass/12f * (zSq + ySq),
                mass/12f * (xSq + zSq),
                mass/12f * (xSq + ySq)
        );
    }
    static Vector3f calculateBoxLocalInertia(float mass, Vector3f h, Vector3f dest) {
        return calculateBoxLocalInertia(mass, h.x, h.y, h.z, dest);
    }
}
