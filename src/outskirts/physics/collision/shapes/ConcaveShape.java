package outskirts.physics.collision.shapes;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Maths;
import outskirts.util.Ref;
import outskirts.util.Val;
import outskirts.util.vector.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * ConcaveShape
 *
 * all triangles data, localspace.
 *
 * ConcaveShape dosen't supports Rotations.
 */
public abstract class ConcaveShape extends CollisionShape implements Raycastable {

    // ? processAllTriangles() or collideTriangles()
    /**
     * for each Triangles intersects with the AABB.
     * @param aabb in Concave-Space. in concave-localspace aabb.
     * @param oncollide (triangleIndex: int, triangle3vertices: [vec3, vec3, vec3] readonly. )
     */
    public abstract void collideTriangles(AABB aabb, BiConsumer<Integer, Vector3f[]> oncollide);

    @Override
    public final Vector3f calculateLocalInertia(float mass, Vector3f dest) {
         return dest.set(0, 0, 0);  // throw new UnsupportedOperationException();
    }

    // linear. fullwalk. slow.
    @Override
    public boolean raycast(Vector3f raypos, Vector3f raydir, Val t, Vector3f ndest) {
        t.val = Float.MAX_VALUE;
        Val tmp = Val.zero(); // artibary.
        collideTriangles(AABB.INFINITY, (i, tri) -> {
            intersectsRayTriangle(raypos, raydir, tri, tmp, t, ndest);
        });
        return t.val != Float.MAX_VALUE;
    }

    protected static void intersectsRayTriangle(Vector3f raypos, Vector3f raydir, Vector3f[] tri, Val tmp, Val t, Vector3f ndest) {
        if (Maths.intersectRayTriangle(raypos, raydir, tri[0], tri[1], tri[2], tmp) && tmp.val >= 0) {
            if (tmp.val < t.val) {
                t.val = tmp.val;
                if (ndest != null)
                    Vector3f.trinorm(tri[0], tri[1], tri[2], ndest);
            }
        }
    }
}
