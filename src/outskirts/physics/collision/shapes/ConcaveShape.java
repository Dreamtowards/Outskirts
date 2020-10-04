package outskirts.physics.collision.shapes;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Maths;
import outskirts.util.Ref;
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
    public boolean raycast(Vector3f raypos, Vector3f raydir, Ref<Float> rst) {
        rst.value = Float.MAX_VALUE;
        Ref<Float> tmp = new Ref<>();
        collideTriangles(AABB.INFINITY, (i, tri) -> {
            if (Maths.intersectRayTriangle(raypos, raydir, tri[0], tri[1], tri[2], tmp)) {
                rst.value = Math.min(rst.value, tmp.value);
            }
        });
        return rst.value != Float.MAX_VALUE;
    }
}
