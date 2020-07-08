package outskirts.client.animation.animated;

import outskirts.client.animation.Animation;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix4f;

import java.util.NoSuchElementException;

// quastion: why src-data keeps 'inparent-bone-space'.?   then even not all in model-localspace.?
public class Animator {

    private float currentTime;  // current time.
    private Animation animation;  // current animation.

    // material, mat[] deltaTransArray, jointsCount, rootJoints..

    public final void doAnimation(Animation animation) {
        this.currentTime = 0;
        this.animation = animation;
    }


    public void update(float delta, Joint[] joints) {
        if (animation==null) return;
        currentTime += delta;
        currentTime %= 4; // loop.?

        // for each joint, interpolate out current transform from curr/next keyframes.
        for (String jname : animation.getKeyFrames().keySet()) {
            Animation.JKeyFrame[] jkframes = animation.getKeyFrames().get(jname);
            Animation.JKeyFrame currFrame=jkframes[0], nextFrame=jkframes[0];
            for (int i = 0;i < jkframes.length;i++) {
                if (currentTime > jkframes[i].timestamp) {
                    currFrame=jkframes[i];
                    nextFrame=jkframes[Math.min(i+1, jkframes.length-1)];
                } else break;
            }
            float t = Maths.inverseLerp(currentTime, currFrame.timestamp, nextFrame.timestamp); // NaN when currFrame==nextFrame.

            Matrix4f currInparentTrans = Animation.JKeyFrame.toMatrix(
                    currFrame==nextFrame?currFrame: Animation.JKeyFrame.lerp(t, currFrame, nextFrame, new Animation.JKeyFrame()),
                    new Matrix4f());

            Joint j = getJ(jname, joints);

            Matrix4f.mul(j.parentIdx==-1?Matrix4f.IDENTITY: joints[j.parentIdx].currentTransform, currInparentTrans, j.currentTransform);
        }
    }

    public static Joint getJ(String jname, Joint[] joints) {
        for (Joint j : joints) {
            if (j.name.equals(jname))
                return j;
        }
        throw new NoSuchElementException();
    }

}
