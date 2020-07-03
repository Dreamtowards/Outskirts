package outskirts.client.animation;

import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class Animation {

    private float duration;  // in seconds.  // or calls 'length'.?
    private KeyFrame[] keyFrames;

    public Animation(float duration, KeyFrame[] keyFrames) {
        this.duration = duration;
        this.keyFrames = keyFrames;
    }

    public float getDuration() {
        return duration;
    }

    public KeyFrame[] getKeyFrames() {
        return keyFrames;
    }


    public static class KeyFrame { // JetyKourrei

        private float timestamp;  // from anim-start. to curr-frame. in seconds.
        private Map<String, JointTransform> jointTransforms = new HashMap<>();  // transform in in-parent-bone-space.
        // should Array and member.jointIndex.?

        public KeyFrame(float timestamp, Map<String, JointTransform> jointTransforms) {
            this.timestamp = timestamp;
            this.jointTransforms = jointTransforms;
        }

        public float getTimestamp() {
            return timestamp;
        }

        public Map<String, JointTransform> getJointTransforms() {
            return jointTransforms;
        }


        // bone space.
        public static class JointTransform {

            private Vector3f position = new Vector3f(); // translation
            private Quaternion rotation = new Quaternion(); // orientation

            public JointTransform() {}

            public JointTransform(Vector3f position, Quaternion rotation) {
                this.position.set(position);
                this.rotation.set(rotation);
            }

            public static Matrix4f interpolateAndToTransMat(float t, JointTransform start, JointTransform end) {
                JointTransform dest = new JointTransform();
                Vector3f.lerp(t, start.position, end.position, dest.position);
                Quaternion.nlerpsf(t, start.rotation, end.rotation, dest.rotation);
                return toTransMat(dest, new Matrix4f()); // opt required..
            }

            private static Matrix4f toTransMat(JointTransform src, Matrix4f dest) {
                Matrix4f pos = Matrix4f.translate(src.position, new Matrix4f());  // req opt.
                Matrix4f rot = Quaternion.toMatrix(src.rotation, new Matrix4f());
                return Matrix4f.mul(pos, rot, dest);
            }

        }
    }
}
