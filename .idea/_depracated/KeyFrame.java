package outskirts.client.animation;

import java.util.HashMap;
import java.util.Map;

private final class KeyFrame {

    private float timestamp;  // from anim-start. to curr-frame. in seconds.
    private Map<String, JointTransform> jointTransformMap = new HashMap<>();  // transform in in-parent-bone-space.


    public float getTimestamp() {
        return timestamp;
    }

    public Map<String, JointTransform> getJointTransforms() {
        return jointTransformMap;
    }
}
