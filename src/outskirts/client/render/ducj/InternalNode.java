package outskirts.client.render.ducj;

public class InternalNode extends OctreeNode {

    public OctreeNode[] child = new LeafNode[8];

    @Override
    public int type() {
        return 0;
    }
}
