package outskirts.physics.collision.shapes;

import outskirts.physics.collision.broadphase.bounding.AABB;
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
public abstract class ConcaveShape extends CollisionShape {

    /**
     * for each Triangles in the AABB.
     * @param onProcessTriangle (triangle3vts: vec3[] , triangleIndex: int)
     * @param aabb in Concave-Space. in concave-localspace aabb.
     */
    public abstract void processAllTriangles(BiConsumer<Vector3f[], Integer> onProcessTriangle, AABB aabb);

    @Override
    public final Vector3f calculateLocalInertia(float mass, Vector3f dest) {
         return dest.set(0, 0, 0);  // throw new UnsupportedOperationException();
    }
}
