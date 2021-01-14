package outskirts.client.render.dcja;

import java.util.Arrays;

public class InternalNode extends OctreeNode {
//    {
//        System.out.println((LeafNode.i++)+" INTERNAL ");
//    }

    public OctreeNode[] child = new OctreeNode[8];

    @Override
    public int type() {
        return TYPE_INTERNAL;
    }

    @Override
    public String toString() {
        return "InternalNode{" +
                "child=" + Arrays.toString(child) +
                '}';
    }
}
