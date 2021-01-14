package outskirts.client.render.dcja;

public abstract class OctreeNode {

    public static final byte TYPE_INTERNAL = 1;
    public static final byte TYPE_LEAF = 2;

    public abstract int type();

}
