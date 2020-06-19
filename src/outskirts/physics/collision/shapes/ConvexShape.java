package outskirts.physics.collision.shapes;

import outskirts.util.Transform;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

public abstract class ConvexShape extends CollisionShape {

    /**
     * getFarthestPoint() in the shape along the direction (localspace). mainly for supports GJK SupportFunction.
     * for supports Transformations, just use inv-rot-mat apply to d, and apply rot to the result point
     * @param d the direction. requires non-zero. normalized unit-vector.
     * @param dest this is non-null. (so not needs check null in impl
     */
    public abstract Vector3f getFarthestPoint(Vector3f d, Vector3f dest);

    /**
     * A tool method for, getFarthestPoint() in the Transform(ed) Shape along the direction (worldspace).
     */
    public final Vector3f getFarthestPoint(Vector3f d, Vector3f dest, Transform transform) {
        Vector3f localspacedir = Matrix3f.transform(transform.basis.transpose(), new Vector3f(d)); // rot.invert() invert rotmat, let dir recovery to non-rotation localspace

        getFarthestPoint(localspacedir, dest);

        Matrix3f.transform(transform.basis.transpose(), dest); // rot.invert() back. and apply rotation to the point
        dest.add(transform.origin); // apply position.

        return dest;
    }

}
