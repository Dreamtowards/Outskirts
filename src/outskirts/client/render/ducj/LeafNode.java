package outskirts.client.render.ducj;

import outskirts.util.vector.Vector3f;

public class LeafNode extends OctreeNode {

    private int signs;

    public int depth;

    public Vector3f p = new Vector3f();
    public int idx = -1;

    public float[] ata = new float[6];
    public float[] atb = new float[3];
    public float btb;

    public LeafNode(int lv, int sg, Vector3f coord) {
        this.depth = lv;
        this.signs = sg;

        p.set(coord);
    }

    @Override
    public int type() {
        return 1;
    }

    int sign ( int index )
    {
        return (( signs >> index ) & 1 );
    };
}
