package outskirts.client.animation.animated;

import outskirts.client.Loader;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.init.Textures;
import outskirts.util.Identifier;
import outskirts.util.vector.Matrix4f;

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

    private Joint rootJoint;
    private int countJoints;

    public Animator animator;

    public AnimatedModel(Model model, Joint rootJoint, int countJoints) {
        this.model = model;
        this.texture = Loader.loadTexture(new Identifier("materials/transres/diffuse.png").getInputStream());
        this.rootJoint = rootJoint;
        this.countJoints = countJoints;
        this.animator = new Animator();
        rootJoint.calcInvBindTransform(new Matrix4f());
    }

    public void update(float delta) {

        animator.update(delta, rootJoint);
    }

    /**
     * models
     * @return array element are internals objects. should unmodifiable.
     */
    public Matrix4f[] getJointTransforms() {
        Matrix4f[] trans = new Matrix4f[countJoints];
        fillJointTransf(rootJoint, trans);
        return trans;
    }

    private void fillJointTransf(Joint joint, Matrix4f[] dest) {
        dest[joint.idx] = joint.currentTransform;
        for (Joint child : joint.children) {
            fillJointTransf(child, dest);
        }
    }
}
