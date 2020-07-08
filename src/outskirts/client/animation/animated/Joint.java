package outskirts.client.animation.animated;

import outskirts.util.vector.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Joint {

    public int parentIdx;  // for shaders Indexing.
    public String name;

    public List<Joint> _children = new ArrayList<>();

    public Matrix4f currentTransform = new Matrix4f(); // modelspace. 'joint-current-transform', not the delta-transform.

    public final Matrix4f _bindTransform = new Matrix4f();
    public final Matrix4f invBindTransform = new Matrix4f(); // modelspace.

    public Joint(int parentIdx, String name, Matrix4f bindTransform) {
        this.parentIdx = parentIdx;
        this.name = name;
        this.currentTransform.set(bindTransform); // tmp init.
        this.invBindTransform.set(bindTransform).invert();
        _bindTransform.set(bindTransform);
    }

}
