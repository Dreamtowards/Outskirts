package outskirts.world.chunk;

import outskirts.block.state.BlockState;
import outskirts.client.Outskirts;
import outskirts.event.Events;
import outskirts.event.world.block.BlockChangedEvent;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.Validate;
import outskirts.util.vector.Vector3f;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

//does this instance data final?
public final class Octree {

    public static final int LENGTH = 8;     //children count

    // bits of x|y|z
    public static final Vector3f[] CHILDREN_POS = {
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.0f, 0.0f, 0.5f),
            new Vector3f(0.0f, 0.5f, 0.0f),
            new Vector3f(0.0f, 0.5f, 0.5f),
            new Vector3f(0.5f, 0.0f, 0.0f),
            new Vector3f(0.5f, 0.0f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.0f),
            new Vector3f(0.5f, 0.5f, 0.5f)
    };

    //dont change this value.
    private final Vector3f position;

    private final Octree parent;

    private final int depth;
    private final float size;

    private Octree[] children;

    private BlockState body; //self? or blockState ...

    Octree(Octree parent, Vector3f position) {
        this.parent = parent;
        this.depth = Octree.calculateDepth(this);
        this.size = Octree.calculateSize(depth);

        this.position = position; //usually is not directly set Vector3f's reference.
    }

    public float size() {
        return size;
    }

    public int depth() {
        return depth;
    }

    public Octree parent() {
        return parent;
    }

    public Octree child(int index) {
        return children[index];
    }

    public boolean hasChildren() {
        return children != null;
    }

    public boolean hasBody() {
        return body != null;
    }

    // blockPos() / position()
    public Vector3f blockPos() {
        return position;
    }

    private void allocChildren() {
        Validate.isTrue(!hasChildren(), "Already had children array.");
        Validate.isTrue(!hasBody(), "Unable to allocChildren, body is existed.");

        children = new Octree[LENGTH];
        for (int i = 0;i < LENGTH;i++) {
            children[i] = new Octree(this, new Vector3f(CHILDREN_POS[i]).scale(size()).add(position));
        }
    }

    public AABB getAABB(AABB dest) {
        dest.min.set(position);
        dest.max.set(position).add(size, size, size);
        return dest;
    }

    public BlockState body() {
        return body;
    }

    public void body(BlockState blockState) {
        Validate.validState(!hasChildren(), "Unable to set Body. Children is existed.");

        this.body = blockState;

        Events.EVENT_BUS.post(new BlockChangedEvent(Outskirts.getWorld(), this));
    }

    private void read(DataInput in, List<BlockState> stateTable) throws IOException {
        byte type = in.readByte();
        switch (type) {
            case 0: // no body AND no children. empty octree
                break;
            case 1: // Body octree
                short id = in.readShort();
                body(stateTable.get(id));
                break;
            case 2: // Children octree
                allocChildren();
                for (int i = 0;i < Octree.LENGTH;i++) {
                    children[i].read(in, stateTable);
                }
                break;
            default:
                throw new IllegalStateException(); // illegal type.
        }
    }

    private void write(DataOutput out, List<BlockState> stateTable) throws IOException {
        if (hasChildren()) {
            out.writeByte(2);
            for (int i = 0;i < Octree.LENGTH;i++) {
                children[i].write(out, stateTable);
            }
        } else if (hasBody()) {
            out.writeByte(1);
            int id = stateTable.indexOf(body());
            if (id <= Short.MAX_VALUE) {
                out.writeShort(id);
            } else {
                throw new IllegalStateException("BlockState id oversize. ("+id+")");
            }
        } else {
            out.write(0);
        }
    }

    public static void readOctree(Octree octree, InputStream inputStream, List<BlockState> stateTable) {
        try {
            octree.read(new DataInputStream(inputStream), stateTable);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read octree.", ex);
        }
    }

    public static void writeOctree(Octree octree, OutputStream outputStream, List<BlockState> stateTable) {
        try {
            octree.write(new DataOutputStream(outputStream), stateTable);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write octree.", ex);
        }
    }

    /**
     * root = 0
     * d    = 1
     */
    private static int calculateDepth(Octree octree) {
        if (octree.parent == null)
            return 0;
        return calculateDepth(octree.parent) + 1;
    }

    // public: sometimes external needs calculate size by fixed depth,
    // like RayPicker calc blockPos unit by GameSettings.PICKER_DEPTH
    /**
     * root = 1 * 16 = 16
     * dep1 = .5 * 16 = 8
     * dep2 = .25 * 16 = 4
     * dep3 = .125 * 16 = 2
     * dep4 = .0625 * 16 = 1
     */
    public static float calculateSize(int depth) {
        return (float)Math.pow(0.5f, depth) * Chunk.SIZE;
    }

    /**
     * x, y, z between 0-1
     */
    public static int indexOf(float x, float y, float z) {
        return (x >= 0.5f ? 4 : 0) | (y >= 0.5f ? 2 : 0) | (z >= 0.5f ? 1 : 0);
    }

    //does DIRECT means must hasBody ? now is't, think how about we need walk a getOctree()'s children?
    /**
     * @param x,y,z 0-16 as coordinates as octree.size()
     * @param creating [false=DIRECT, true=CREATING].
     *                 DIRECT: getting octree at specified position, if not find that will return null
     *                 CREATING: definitely return specified-position's octree and never return null. if not find, just creating octrees until reached the specified position
     */
    static Octree findChild(float x, float y, float z, int depth, Octree root, boolean creating) {
        Octree octree = root;
        for (int i = 0;i < depth;i++) {
            float size = octree.size();
            if (!octree.hasChildren()) {
                if (creating) {
                    octree.allocChildren();
                } else {
                    return null;
                }
            }
            octree = octree.child(indexOf(
                    (x % size) / size, // MOD(xyz, size) / size
                    (y % size) / size,
                    (z % size) / size
            ));
        }
//        if (!creating && octree != null && !octree.hasBody() && !octree.hasChildren())
//            return null;
        return octree;
    }

    public static void forChildren(Octree octree, Consumer<Octree> accumulator) {

        accumulator.accept(octree);

        if (octree.hasChildren()) {
            for (int i = 0; i < LENGTH; i++) {
                forChildren(octree.child(i), accumulator);
            }
        }
    }
}
