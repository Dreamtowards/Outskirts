package outskirts.physics.collision.shapes;

import outskirts.physics.collision.shapes.convex.SphereShape;
import outskirts.util.CollectionUtils;
import outskirts.util.Transform;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import java.util.Arrays;

/**
 * the Shape just only handles shape itself's features/responsibility
 * Shape even can SingleInstance for one state or applies FlightWeight Pattern,
 * Its totally decouple from owner CollisionObject.
 */
public abstract class CollisionShape {

    /**
     * get shape local-aabb (not transformation)
     * note that required return dest's reference back.
     */
    protected abstract AABB getAABB(AABB dest);

    private static final Vector3f[] TMP_VERTS = CollectionUtils.fill(new Vector3f[8], Vector3f::new);
    /**
     * get transformed-shape's AABB
     */
    public final AABB getAABB(AABB dest, Transform transform) {
        getAABB(dest);
        if (transform.equals(Transform.IDENTITY))
            return dest;
        if (this instanceof ConcaveShape)
            throw new IllegalArgumentException("Concave is not supports Transforms."); // may shouldn't validate in there..
        if (this instanceof SphereShape)
            return dest.translate(transform.origin);

        // apply rotation
        AABB.vertices(dest, TMP_VERTS);
        for (Vector3f vert : TMP_VERTS)
            Matrix3f.transform(transform.basis, vert);
        AABB.bounding(TMP_VERTS, dest);

        // apply position
        dest.translate(transform.origin);

        return dest;
    }

    /**
     * its yield a Moment_of_Inertia-Tensor, by exports the inertia:vec3 xyz as the tensor:mat3's m00, m11, m22
     * | x 0 0 |
     * | 0 y 0 |
     * | 0 0 z |
     * https://en.wikipedia.org/wiki/List_of_moments_of_inertia#List_of_3D_inertia_tensors
     * @param mass bodyobject mass
     * @param dest export dest. nonnull
     */
    public abstract Vector3f calculateLocalInertia(float mass, Vector3f dest);

}
