package outskirts.client.animation;

import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

private final class JointTransform {

    private Vector3f position = new Vector3f();
    private Quaternion rotation = new Quaternion();

    public static JointTransform interpolate(float t, JointTransform start, JointTransform end, JointTransform dest) {
        if (dest == null)
            dest = new JointTransform();
        Vector3f.lerp(t, start.position, end.position, dest.position);
        Quaternion.nlerpsf(t, start.rotation, end.rotation, dest.rotation);
        return dest;
    }

    public static Matrix4f toTransMat(JointTransform src, Matrix4f dest) {
        Matrix4f pos = Matrix4f.translate(src.position, new Matrix4f());  // req opt.
        Matrix4f rot = Quaternion.toMatrix(src.rotation, new Matrix4f());
        return Matrix4f.mul(pos, rot, dest);
    }

}
