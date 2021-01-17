package outskirts.client.render.isoalgorithm.dc;

public abstract class Octree {

    // the 0 is been NULL.
    public static final byte TYPE_INTERNAL = 1;
    public static final byte TYPE_LEAF = 2;



    public abstract byte type();



    public static class InternalNode extends Octree {

        private Octree[] children = new Octree[8];

        @Override
        public byte type() {
            return Octree.TYPE_INTERNAL;
        }
    }


    /**
     * Stores Hermite Data.  Rd. Material.
     *
     */
    public static class LeafNode extends Octree {

        private HermiteData[] edgehd = new HermiteData[12];

        private int materialIdx;

        // static Vector3f computeFeaturePoint(Octree.LeafNode cell);

        @Override
        public byte type() {
            return Octree.TYPE_LEAF;
        }
    }

}
