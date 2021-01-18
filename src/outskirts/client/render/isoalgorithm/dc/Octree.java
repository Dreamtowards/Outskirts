package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvDCJAM3;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static outskirts.util.logging.Log.LOGGER;

public abstract class Octree {

    public static final byte TYPE_NULL = 0;       // storage only.
    public static final byte TYPE_INTERNAL = 1;
    public static final byte TYPE_LEAF = 2;

    /**   2+-----+6
     *    /|    /|
     *  3+-----+7|
     *   |0+---|-+4
     *   |/    |/
     *  1+-----+5   RH */  // 000,001,010,011,100,101,110,111
    public static final Vector3f[] VERT = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 1, 0),
            new Vector3f(0, 1, 1),
            new Vector3f(1, 0, 0),
            new Vector3f(1, 0, 1),
            new Vector3f(1, 1, 0),
            new Vector3f(1, 1, 1)
    };

    public static final int[][] EDGE =
           {{0,4},{1,5},{2,6},{3,7},  // X
            {0,2},{1,3},{4,6},{5,7},  // Y
            {0,1},{2,3},{4,5},{6,7}}; // Z

//    public int depth;  // neseccary.? or just use 'size' better.?

    /**
     * Type is required for storage.
     */
    protected abstract byte type();

    /**
     * There only 2 types. either Internal or Leaf.
     * the Empty or Psudo just be null.
     */
    public final boolean isInternal() { return type() == TYPE_INTERNAL; }
    public final boolean isLeaf() { return type() == TYPE_LEAF; }




    public static class InternalNode extends Octree {

        private Octree[] children = new Octree[8];

        public Octree child(int i) {
            return children[i];
        }
        public void child(int i, Octree node) {
            children[i] = node;
        }

        @Override
        public byte type() { return TYPE_INTERNAL; }
    }


    /**
     * Stores Hermite Data.  Rd. Material.
     *
     */
    public static class LeafNode extends Octree {

        /** Cell Vertices Signums.  NotNumVal. Jus. 8 bits.  0: Negative(-1), 1: Positive(+1).  indices.{76543210} */
        private byte vsign;

        /** Hermite data for Edges.  only sign-changed edge are nonnull. else just null element. */
        public final HermiteData[] edges = new HermiteData[12];

        private int materialIdx;

        public final Vector3f featurepoint = new Vector3f();

        // needs really aabb.?   does Internal needs min and aabb.?
        public final Vector3f min = new Vector3f();
        public final float size;  // actually size.

        public LeafNode(Vector3f minVal, float size) {
            this.min.set(minVal);
            this.size = size;
        }

        @Override
        public byte type() { return TYPE_LEAF; }

        public int sign(int vi) { assert vi >= 0 && vi < 8;
            return ((vsign >>> vi) & 1) == 1 ? 1 : -1;
        }
        public void sign(int vi, int sign) { assert sign==-1 || sign==1; assert vi >= 0 && vi < 8;
            vsign |= (sign==1 ? 1 : 0) << vi;
        }

        public AABB aabb(AABB dest) {
            return dest.set(min.x, min.y, min.z, min.x+size, min.y+size, min.z+size);
        }
    }


    private static void computefeaturepoint(Octree.LeafNode cell) {

        int sz = CollectionUtils.nonnulli(cell.edges);
        Vector3f[] ps = new Vector3f[sz];
        Vector3f[] ns = new Vector3f[sz];
        int i = 0;
        for (HermiteData h : cell.edges) {
            if (h != null) {
                ps[i] = h.point;
                ns[i] = h.norm;
                i++;
            }
        }
        cell.featurepoint.set(QEFSolvDCJAM3.wCalcQEF(Arrays.asList(ps), Arrays.asList(ns)));

    }



    public static Octree readOctree(InputStream is, Vector3f min, float size) throws IOException {
        byte type = IOUtils.readByte(is);
        switch (type) {
            case TYPE_NULL:
                printdebug("WRITE::NULL");
                return null;
            case TYPE_INTERNAL: {
                printdebug("WRITE::INTERNAL");
                InternalNode internal = new InternalNode();
                float subsize = size/2f;
                Vector3f submin = new Vector3f();
                for (int i = 0;i < 8;i++) {
                    submin.set(min).addScaled(subsize, VERT[i]);
                    internal.child(i, readOctree(is, submin, subsize));
                }
                return internal; }
            case TYPE_LEAF: {
                printdebug("WRITE::LEAF");
                LeafNode leaf = new LeafNode(min, size);  // leaf.min, size. depth
                leaf.vsign = IOUtils.readByte(is);
                printdebug(" -VSIGN: "+Integer.toBinaryString(leaf.vsign & 0xff));

                int sz = IOUtils.readByte(is);
                printdebug(" -EgSZ: "+sz);
                for (int i = 0;i < sz;i++) {
                    int idx = IOUtils.readByte(is);
                    printdebug(" -EgI: "+idx);
                    HermiteData h = new HermiteData();

                    int axis = idx/4;
                    float t = IOUtils.readFloat(is); assert t >= 0F && t <= 1F;
                    printdebug(" -Eg.t: "+t);
                    h.point.set(leaf.min)
                           .addScaled(size, VERT[EDGE[idx][0]])  // offset to edge-base-vert.
                           .addv(axis, leaf.size*t);
                    printdebug(" -Eg.p: "+h.point);

                    h.norm.x = IOUtils.readFloat(is);
                    h.norm.y = IOUtils.readFloat(is);
                    h.norm.z = IOUtils.readFloat(is);
                    printdebug(" -Eg.n: "+h.norm);

                    leaf.edges[idx] = h;
                }

                computefeaturepoint(leaf);
                printdebug(" -Eg.FP::: : "+leaf.featurepoint);

                // METADATA byte[short]
                return leaf; }
            default:
                throw new IllegalStateException("Unknown type.");
        }
    }

    public static void writeOctree(OutputStream os, Octree node) throws IOException {   // this is not warpped. so outstream in 1st param. means 'continious stream'.
        if (node == null) {
            printdebug("WRITE::NULL");
            IOUtils.writeByte(os, TYPE_NULL);
            return;
        }
        if (node.isInternal()) {
            printdebug("WRITE::INTERNAL");
            IOUtils.writeByte(os, TYPE_INTERNAL);
            for (int i = 0;i < 8;i++) {
                writeOctree(os, ((InternalNode)node).child(i));
            }
        } else {
            LeafNode leaf = (LeafNode)node;
            printdebug("WRITE::LEAF");
            IOUtils.writeByte(os, TYPE_LEAF);

            printdebug(" -VSIGN: "+Integer.toBinaryString(leaf.vsign & 0xff));
            IOUtils.writeByte(os, leaf.vsign);

            int sz = CollectionUtils.nonnulli(leaf.edges);
            printdebug(" -EgSZ: "+sz);
            IOUtils.writeByte(os, (byte)sz);

            for (int i = 0;i < 12;i++) {
                HermiteData h = leaf.edges[i];
                if (h != null) {
                    printdebug(" -EgI: "+i);
                    IOUtils.writeByte(os, (byte)i);

                    int axis = i/4;
                    float minf = leaf.min.get(axis);
                    float t = Maths.inverseLerp(h.point.get(axis), minf, minf+leaf.size);  assert t >= 0F && t <= 1F;
                    printdebug(" -Eg.t: "+t);
                    LOGGER.info("Axis: {}, p:{} min:{}", axis, h.point, leaf.min);
                    System.exit(0);
                    IOUtils.writeFloat(os, t);

                    printdebug(" -Eg.n: "+h.norm);
                    IOUtils.writeFloat(os, h.norm.x);
                    IOUtils.writeFloat(os, h.norm.y);
                    IOUtils.writeFloat(os, h.norm.z);
                }
            }
            // METADATA byte[short]
        }
    }

    private static void printdebug(String s) {
//        LOGGER.info(s);
    }
}
