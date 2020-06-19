package outskirts.client.render.chunk;

import outskirts.client.material.Model;
import outskirts.util.Maths;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector3i;
import outskirts.util.vector.Vector4f;

public class RenderSection {

    private Model model;

    /**
     * The position is world-coordinate and just like Section(16*16*16),
     * x y z must be 16's multiple (like 0, 16, 32, 48, 64...)
     * the final no actually working.. but maybe is a mean sign: dont change it after Constructor!
     */
    private final Vector3f position;

    private boolean needsUpdate;

    private boolean empty;

    public RenderSection(Vector3f position) {
        this.position = position;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void markNeedsUpdate() {
        this.needsUpdate = true;
    }

    public void clearNeedsUpdate() {
        this.needsUpdate = false;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return empty;
    }


    public static Vector3i getPosition(Vector3f blockPos) {
        return new Vector3i(
                (int)Maths.unit(blockPos.x, 16),
                (int)Maths.unit(blockPos.y, 16),
                (int)Maths.unit(blockPos.z, 16)
        );
    }
}
