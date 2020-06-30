package outskirts.client.animation.animated;

import outskirts.client.animation.Animation;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix4f;

import java.util.HashMap;
import java.util.Map;

// quastion: why src-data keeps 'inparent-bone-space'.?   then even not all in model-localspace.?
public class Animator {

    private float currentTime;  // current time.
    private Animation animation;  // current animation.

    // material, mat[] deltaTransArray, jointsCount, rootJoints..

    public final void doAnimation(Animation animation) {
        this.currentTime = 0;
        this.animation = animation;
    }


    public void update(float delta, Joint rootJoint) {
        if (animation==null) return;
        currentTime += delta;
        currentTime %= animation.getDuration(); // loop.?

        Map<String, Matrix4f> currtrans = calculateCurrentAnimationPose();
        applyTransToJoints(currtrans, rootJoint, new Matrix4f()); // opt IDENTITYmat   but why not mat4[] transArray -> to Joint[] jointArray setting.? yes not.. cuz needs treeStruc-set.
    }

    private Map<String, Matrix4f> calculateCurrentAnimationPose() {  // why not mat4[] trans array.?
        // getting currFrame, nextFrame.
        Animation.KeyFrame[] kfs = animation.getKeyFrames();
        Animation.KeyFrame currFrame=null, nextFrame=null;
        for (int i = 1;i < kfs.length;i++) {
            if (currentTime < kfs[i].getTimestamp()) {
                nextFrame=kfs[i];
                currFrame=kfs[i-1];
                break;
            }
        }
        // getting 'current-progress'
        float t = Maths.inverseLerp(currentTime, currFrame.getTimestamp(), nextFrame.getTimestamp());  // interpolate v.

        // interpolate/generate bone-space transform.
        Map<String, Matrix4f> currentJointTrans = new HashMap<>();  // inparent-bone-space -trans.
        for (String jointname : currFrame.getJointTransforms().keySet()) {
            Animation.KeyFrame.JointTransform currTrans = currFrame.getJointTransforms().get(jointname);
            Animation.KeyFrame.JointTransform nextTrans = nextFrame.getJointTransforms().get(jointname);
            Matrix4f intpTrans = Animation.KeyFrame.JointTransform.interpolateAndToTransMat(t, currTrans, nextTrans); // cache obj.?
            currentJointTrans.put(jointname, intpTrans);
        }
        return currentJointTrans;
    }

    private void applyTransToJoints(Map<String, Matrix4f> trans, Joint joint, Matrix4f parentTrans) {
        Matrix4f modelspTrans = Matrix4f.mul(parentTrans, trans.get(joint.name), new Matrix4f());
        for (Joint child : joint.children) {
            applyTransToJoints(trans, child, modelspTrans);
        }
        Matrix4f.mul(modelspTrans, joint.invBindTransform, joint.currentTransform); // the exactly delta-transform
    }

}
