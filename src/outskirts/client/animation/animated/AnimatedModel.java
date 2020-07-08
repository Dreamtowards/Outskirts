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

    public Animator animator;

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



    public static AnimatedModel newFromDAE(DaeLoader.DaeData daedata) {
        DaeLoader.DaeData.MeshData dMeshs = daedata.meshs;
        Model model = Loader.loadModel(dMeshs.indices, 3,dMeshs.positions, 2,dMeshs.textureCoords, 3,dMeshs.normals, 3,dMeshs.jointsIDs, 3,dMeshs.jointsWeights);

        Joint[] joints = new Joint[daedata.joints.length];
        for (int i = 0;i < joints.length;i++) {
            DaeLoader.DaeData.JointInfo jinf = daedata.joints[i];
            joints[i] = new Joint(jinf.parentIdx, jinf.name, jinf.bindTransform);
            if (i!=0) {
                joints[joints[i].parentIdx]._children.add(joints[i]);
            }
//            Log.LOGGER.info("joint{}: parent:{}, bindTrans:{}", joints[i].name, joints[i].parentIdx==-1?"NO":joints[joints[i].parentIdx].name, joints[i]._bindTransform);
        }

        return new AnimatedModel(model, joints);
    }

    public static Animation loadAfromDae(DaeLoader.DaeData daedat) {
        Animation ja = new Animation();

        for (Map.Entry<String, DaeLoader.DaeData.JointKeyframeData[]> entry : daedat.anim1.entrySet()) {
            Animation.JKeyFrame[] kfs = new Animation.JKeyFrame[entry.getValue().length];
            for (int i = 0;i < entry.getValue().length;i++) {
                kfs[i] = new Animation.JKeyFrame();
                kfs[i].timestamp = entry.getValue()[i].timestamp;
                kfs[i].translation.set(entry.getValue()[i].translation);
                kfs[i].orientation.set(entry.getValue()[i].orientation);
            }
            ja.getKeyFrames().put(entry.getKey(), kfs);
        }

        return ja;
    }
}
