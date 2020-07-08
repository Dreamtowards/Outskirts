package outskirts.client.animation.animated;

import outskirts.client.animation.Animation;
import outskirts.client.animation.JointAnimation;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import sun.security.provider.MD4;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// quastion: why src-data keeps 'inparent-bone-space'.?   then even not all in model-localspace.?
public class Animator {

    private float currentTime;  // current time.
    private JointAnimation animation;  // current animation.

    // material, mat[] deltaTransArray, jointsCount, rootJoints..

    public final void doAnimation(JointAnimation animation) {
        this.currentTime = 0;
        this.animation = animation;
    }


    public void update(float delta, Joint[] joints) {
        if (animation==null) return;
        currentTime += delta;
        currentTime %= 4; // loop.?

        // for each joint, interpolate out current transform from curr/next keyframes.
        for (String jname : animation.getKeyFrames().keySet()) {
            JointAnimation.JKeyFrame[] jkframes = animation.getKeyFrames().get(jname);
            JointAnimation.JKeyFrame currFrame=jkframes[0], nextFrame=jkframes[0];
            for (int i = 0;i < jkframes.length;i++) {
                if (currentTime > jkframes[i].timestamp) {
                    currFrame=jkframes[i];
                    nextFrame=jkframes[Math.min(i+1, jkframes.length-1)];
                } else break;
            }
            float t = Maths.inverseLerp(currentTime, currFrame.timestamp, nextFrame.timestamp); // NaN when currFrame==nextFrame.

            Matrix4f currInparentTrans = JointAnimation.JKeyFrame.toMatrix(
                    currFrame==nextFrame?currFrame: JointAnimation.JKeyFrame.lerp(t, currFrame, nextFrame, new JointAnimation.JKeyFrame()),
                    new Matrix4f());

            Joint j = getJ(jname, joints);

            if (j.parentIdx == -1)
                j.currentTransform.set(currInparentTrans);
            else
                Matrix4f.mul(joints[j.parentIdx].currentTransform, currInparentTrans, j.currentTransform);
        }

//        Map<String, Matrix4f> currtrans = calculateCurrentAnimationPose();
//        applyTransToJoints(currtrans, joints); // opt IDENTITYmat   but why not mat4[] transArray -> to Joint[] jointArray setting.? yes not.. cuz needs treeStruc-set.
    }

    public static Joint getJ(String jname, Joint[] joints) {
        for (Joint j : joints) {
            if (j.name.equals(jname))
                return j;
        }
        throw new NoSuchElementException();
    }

//    private Map<String, Matrix4f> calculateCurrentAnimationPose() {  // why not mat4[] trans array.?
//        // getting currFrame, nextFrame.
//        Animation.KeyFrame[] kfs = animation.getKeyFrames();
//        Animation.KeyFrame currFrame=null, nextFrame=null;
//        for (int i = 1;i < kfs.length;i++) {
//            if (currentTime < kfs[i].getTimestamp()) {
//                nextFrame=kfs[i];
//                currFrame=kfs[i-1];
//                break;
//            }
//        }
//        // getting 'current-progress'
//
//        // interpolate/generate bone-space transform.
//        Map<String, Matrix4f> currentJointTrans = new HashMap<>();  // inparent-bone-space -trans.
//        for (String jointname : currFrame.getJointTransforms().keySet()) {
//            Animation.KeyFrame.JointTransform currTrans = currFrame.getJointTransforms().get(jointname);
//            Animation.KeyFrame.JointTransform nextTrans = nextFrame.getJointTransforms().get(jointname);
//            Matrix4f intpTrans = Animation.KeyFrame.JointTransform.interpolateAndToTransMat(t, currTrans, nextTrans); // cache obj.?
//            currentJointTrans.put(jointname, intpTrans);
//        }
//        return currentJointTrans;
//    }
//
//    private void applyTransToJoints(Map<String, Matrix4f> trans, Joint[] joints) {
//        for (int i = 0;i < joints.length;i++) {
//            Joint j = joints[i];
//            Matrix4f.mul(
//                    i==0? Matrix4f.IDENTITY:joints[j.parentIdx].currentTransform, trans.get(j.name),
//                    j.currentTransform);
//        }
//    }

}
