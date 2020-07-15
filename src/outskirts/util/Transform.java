package outskirts.util;

import outskirts.storage.Savable;
import outskirts.storage.DataMap;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import java.util.Map;

/**
 * Affine Transformation
 * T(x) = Rx + c
 * not use position/rotation term because they are more-high-level and customized concept.
 */
public final class Transform {

    // use for set(IDENTITY)
    public static final Transform IDENTITY = new Transform();


    public final Vector3f origin = new Vector3f(); // Position

    public final Matrix3f basis = new Matrix3f(); // Rotation

    public Transform() {}

    public Transform(Transform src) {
        set(src);
    }

    public Transform set(Transform trans) {
        origin.set(trans.origin);
        basis.set(trans.basis);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof Transform &&
                ((Transform)obj).origin.equals(this.origin) &&
                ((Transform)obj).basis.equals(this.basis);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 31 * hash + origin.hashCode();
        hash = 31 * hash + basis.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Transform{origin="+origin+", basis="+basis+'}';
    }

    public static Vector3f transform(Transform transform, Vector3f dest) {
        return Matrix3f.transform(transform.basis, dest).add(transform.origin);
    }

    public static Vector3f inverseTranform(Transform transform, Vector3f dest) {
        dest.sub(transform.origin);

        Matrix3f.transform(transform.basis.transpose(), dest);
        transform.basis.transpose(); // invert back.
        return dest;
    }



    // tmp
    // not dest/src trans, just dest as src. cuz you can set dest value to src before perform the operation
    public static void integrate(Transform trans, Vector3f linvel, Vector3f angvel, float delta) {
        trans.origin.addScaled(delta, linvel);

        // rot = angvel_deltaRot * rot;
        if (!Maths.fuzzyZero(angvel.lengthSquared())) { // for avoid normalize() zero length err
            Matrix3f.mul(Matrix3f.rotate(angvel.length() * delta, new Vector3f(angvel).normalize(), null), trans.basis, trans.basis);
        }
    }
}
