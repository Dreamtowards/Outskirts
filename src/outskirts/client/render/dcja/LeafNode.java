package outskirts.client.render.dcja;

import outskirts.util.vector.Vector3f;

import java.util.Arrays;

public class LeafNode extends OctreeNode {

    private int signs;

    public int depth;

    public Vector3f vert = new Vector3f();

    public float[] ata = new float[6];
    public float[] atb = new float[3];
    public float btb;

    public LeafNode(int lv, int sg, Vector3f vert) {
        this.depth = lv;
        this.signs = sg;

        this.vert.set(vert);
//        System.out.println((LeafNode.i++)+" LEAF p "+vert.toString().substring(8));
    }
//    static int i=0;

    @Override
    public int type() {
        return TYPE_LEAF;
    }

    int sign ( int index )
    {
        return (( signs >> index ) & 1 );
    };

    @Override
    public String toString() {
        return "LeafNode{" +
                "signs=" + signs +
                ", depth=" + depth +
                ", vert=" + vert +
                '}';
    }
}


// OTS [InternalNode{child=[null, null, null, null, null, null, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 4.5]}, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 5.5]}]}, InternalNode{child=[null, null, null,                                                           null,                                                           null,                                                           null,                                                           LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 6.5]}, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 7.5]}]}, InternalNode{child=[null, null, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 4.5]}, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 5.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 4.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 5.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 4.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 5.5]}]}, InternalNode{child=[null, null, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 6.5]}, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 7.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 6.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 7.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 6.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 7.5]}]}]
// DCJ [InternalNode{child=[null, null, null, null, null, null, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 4.5]}, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 5.5]}]}, InternalNode{child=[null, null, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 4.5]}, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 5.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 4.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 5.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 4.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 5.5]}]}, InternalNode{child=[null, null, null, null, null, null, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 6.5]}, LeafNode{signs=192, depth=0, vert=[29.747606, 27.586409, 7.5]}]}, InternalNode{child=[null, null, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 6.5]}, LeafNode{signs=204, depth=0, vert=[30.637722, 27.110706, 7.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 6.5]}, LeafNode{signs=192, depth=0, vert=[31.603611, 26.817682, 7.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 6.5]}, LeafNode{signs=252, depth=0, vert=[31.00132, 27.000402, 7.5]}]}]