package outskirts.client.animation.animated;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.animation.Animation;
import outskirts.client.animation.dae.DaeLoader;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.util.Identifier;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;

import java.util.Map;

public class AnimatedModel {

    /**
     * vec3[] positions
     * vec2[] textureCoords
     * vec3[] normals
     * vec3i[] jointIds
     * vec3[] jointWeights
     * ?tangents
     */
    public Model model;
    public Texture texture;

    public Joint[] joints;
    public Animator animator; // animators

    public AnimatedModel(Model model, Joint[] joints) {
        this.model = model;
        this.texture = Loader.loadTexture(new Identifier("materials/transres/diffuse.png").getInputStream());
        this.joints = joints;
        this.animator = new Animator();
    }

    public void update(float delta) {

        animator.update(delta, joints);
    }

    /**
     * Delta-Transforms
     * @return array element are internals objects. should unmodifiable.
     */
    public Matrix4f[] getJointTransforms(Matrix4f[] dest) {
        for (int i = 0;i < joints.length;i++) {
            Matrix4f.mul(joints[i].currentTransform, joints[i].invBindTransform, dest[i]);
        }
        return dest;
    }



    public static AnimatedModel loadModel(DaeLoader.DaeData daedata) {
        DaeLoader.DaeData.MeshData dMeshs = daedata.meshs;
        Model model = Loader.loadModel(dMeshs.indices, 3,dMeshs.positions, 2,dMeshs.textureCoords, 3,dMeshs.normals, 3,dMeshs.jointsIDs, 3,dMeshs.jointsWeights);

        Joint[] joints = new Joint[daedata.joints.length];
        for (int i = 0;i < joints.length;i++) {
            DaeLoader.DaeData.JointInfo jinf = daedata.joints[i];
            joints[i] = new Joint(jinf.parentIdx, jinf.name, jinf.bindTransform);
        }

        return new AnimatedModel(model, joints);
    }

    public static Animation loadAnim(DaeLoader.DaeData daedat) {
        Animation anim = new Animation();
        for (String jname : daedat.anim1.keySet()) {
            DaeLoader.DaeData.JointKeyframeData[] data_jkeyframes = daedat.anim1.get(jname);
            Animation.JKeyFrame[] jkframes = new Animation.JKeyFrame[data_jkeyframes.length];
            for (int i = 0;i < jkframes.length;i++) {
                jkframes[i] = new Animation.JKeyFrame(data_jkeyframes[i].timestamp, data_jkeyframes[i].translation, data_jkeyframes[i].orientation);
            }
            anim.getKeyFrames().put(jname, jkframes);
        }
        return anim;
    }
}
