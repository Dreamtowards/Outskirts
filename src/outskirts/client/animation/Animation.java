package outskirts.client.animation;

import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Animation {

    private Map<String, JKeyFrame[]> keyframes = new HashMap<>();  // Joint Keyframes.

    public Map<String, JKeyFrame[]> getKeyFrames() {
        return keyframes;
    }

    public static class JKeyFrame { // JointKeyframe
        public float timestamp;
        public Vector3f   translation = new Vector3f();
        public Quaternion orientation = new Quaternion();

        public static JKeyFrame lerp(float t, JKeyFrame start, JKeyFrame end, JKeyFrame dest) {
            Vector3f.lerp(t, start.translation, end.translation, dest.translation);
            Quaternion.nlerpsf(t, start.orientation, end.orientation, dest.orientation);
            return dest;
        }
        public static Matrix4f toMatrix(JKeyFrame keyframe, Matrix4f dest) {
            dest.setIdentity();
            Matrix4f.translate(keyframe.translation, dest);
            Matrix4f.set(dest, Quaternion.toMatrix(keyframe.orientation, new Matrix3f()));
            return dest;
        }
    }

}
