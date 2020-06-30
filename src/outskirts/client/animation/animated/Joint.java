package outskirts.client.animation.animated;

import outskirts.util.vector.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Joint {

    public int idx;  // for shaders Indexing.
    public String name;
    public List<Joint> children = new ArrayList<>();

    public Matrix4f currentTransform = new Matrix4f(); // delta-transform

    private final Matrix4f localBindTransform = new Matrix4f(); // bone-space. /inparentbone-space.     SHARED.
    public final Matrix4f invBindTransform = new Matrix4f(); // modelspace. inv

    public Joint(int idx, String name, Matrix4f localBindTransform) {
        this.idx = idx;
        this.name = name;
        this.localBindTransform.set(localBindTransform);
    }

    /**
     * @param parentBindTransform model-space.
     */
    public void calcInvBindTransform(Matrix4f parentBindTransform) {
        Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, localBindTransform, new Matrix4f());  // model-space
        for (Joint child : children) {
            child.calcInvBindTransform(bindTransform);
        }
        invBindTransform.set(bindTransform).invert();
    }

}
