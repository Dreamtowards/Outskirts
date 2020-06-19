package outskirts.physics.collision.shapes;

import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * ConcaveShape is Static-Shape. no Transform. Offset/Rotation.
 *
 * all triangles data, are worldspace coordinates. not localspace.
 *
 * // may Concave should had a offset/position.? (far terrain vertex coordinate...
 */
public abstract class ConcaveShape extends CollisionShape {

    /**
     * @param onProcessTriangle (triangle3vts: vec3[] , triangleIndex: int)
     * @param aabb worldspace.?
     */
    public abstract void processAllTriangles(BiConsumer<Vector3f[], Integer> onProcessTriangle, AABB aabb);

    @Override
    public final Vector3f calculateLocalInertia(float mass, Vector3f dest) {
         throw new UnsupportedOperationException();  // return dest.set(0, 0, 0);
    }
}
