package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvDCJAM3;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.Val;
import outskirts.util.vector.Vector3f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class Octree {

    private static final byte TYPE_NULL = 0;       // storage only.
    private static final byte TYPE_INTERNAL = 1;
    private static final byte TYPE_LEAF = 2;

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

    /**       -          |          /     AXIS XYZ.
     *     +--2--+    +-----+    +-----+
     *    /|    /|   /4    /6   9|    11
     *   +--3--+ |  +-----+ |  +-----+ |
     *   | +--0|-+  5 +---7-+  | +---|-+
     *   |/    |/   |/    |/   |8    |10
     *   +--1--+    +-----+    +-----+    */
    public static final int[][] EDGE =
           {{0,4},{1,5},{2,6},{3,7},  // X
            {0,2},{1,3},{4,6},{5,7},  // Y
            {0,1},{2,3},{4,5},{6,7}}; // Z


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

    /**
     * Is 'Empty'. from view of tree-structure.  is the node can be clear/collaspe.
     */
    public abstract boolean collapsed();


    public static class Internal extends Octree {

        private Octree[] children = new Octree[8];

        public Octree child(int i) {
            return children[i];
        }
        public void child(int i, Octree node) {
            children[i] = node;
        }

        @Override
        public byte type() { return TYPE_INTERNAL; }

        @Override
        public boolean collapsed() {
            return CollectionUtils.nonnulli(children) == 0;
        }
    }


    /**
     * Stores Hermite Data.  Rd. Material.
     *
     */
    public static class Leaf extends Octree {

        private static final byte SG_FULL = (byte)0xFF;
        private static final byte SG_EMPTY = (byte)0x00;

        /** Cell Vertices Signums.  NotNumVal. Jus. 8 bits.  0: Negative(-1)(Empty), 1: Positive(+1)(Solid).  indices.{76543210} */
        private byte vsign;

        // is there has data-repeat.? some edge are shared.
        /** Hermite data for Edges.  only sign-changed edge are nonnull. else just null element. */
        public final HermiteData[] edges = new HermiteData[12];

        public final Vector3f featurepoint = new Vector3f();

        // needs really aabb.?   does Internal needs min and aabb.?
        public final Vector3f min = new Vector3f();
        public final float size;  // actually size.

        private int materialIdx;

        public Leaf(Vector3f minVal, float size) {
            this.min.set(minVal);
            this.size = size;
        }

        @Override
        public byte type() { return TYPE_LEAF; }

        public int sign(int vi) { assert vi >= 0 && vi < 8;
            return ((vsign >>> vi) & 1) == 1 ? 1 : -1;
        }
        public void sign(int vi, boolean solid) { assert vi >= 0 && vi < 8;
            vsign |= (solid ? 1 : 0) << vi;
        }

        public void computefp() {
            Octree.computefeaturepoint(this);
        }

        @Override
        public boolean collapsed() {
            return vsign == SG_FULL || vsign == SG_EMPTY;
        }

        public AABB aabb(AABB dest) {
            return dest.set(min.x, min.y, min.z, min.x+size, min.y+size, min.z+size);
        }
    }


    private static void computefeaturepoint(Octree.Leaf cell) {
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
        if (ps.length != 0)
        cell.featurepoint.set(QEFSolvDCJAM3.wCalcQEF(Arrays.asList(ps), Arrays.asList(ns)));
    }




    public static Octree readOctree(InputStream is, Vector3f min, float size) throws IOException {
        byte type = IOUtils.readByte(is);
        switch (type) {
            case TYPE_NULL:
                return null;
            case TYPE_INTERNAL: {
                Octree.Internal internal = new Octree.Internal();
                float subsize = size/2f;
                Vector3f submin = new Vector3f();
                for (int i = 0;i < 8;i++) {
                    submin.set(min).addScaled(subsize, VERT[i]);
                    internal.child(i, readOctree(is, submin, subsize));
                }
                return internal; }
            case TYPE_LEAF: {
                Octree.Leaf leaf = new Octree.Leaf(min, size);  // leaf.min, size. depth
                leaf.vsign = IOUtils.readByte(is);

                int sz = IOUtils.readByte(is);
                for (int i = 0;i < sz;i++) {
                    int idx = IOUtils.readByte(is);
                    HermiteData h = new HermiteData();

                    int axis = idx/4;
                    float t = IOUtils.readFloat(is); assert t >= 0F && t <= 1F;
                    h.point.set(leaf.min)
                           .addScaled(size, VERT[EDGE[idx][0]])  // offset to edge-base-vert.
                           .addv(axis, leaf.size*t);

                    h.norm.x = IOUtils.readFloat(is);
                    h.norm.y = IOUtils.readFloat(is);
                    h.norm.z = IOUtils.readFloat(is);

                    leaf.edges[idx] = h;
                }
                leaf.computefp();

                // METADATA byte[short]
                return leaf; }
            default:
                throw new IllegalStateException("Unknown type.");
        }
    }

    public static void writeOctree(OutputStream os, Octree node) throws IOException {   // this is not warpped. so outstream in 1st param. means 'continious stream'.
        if (node == null) {
            IOUtils.writeByte(os, TYPE_NULL);
            return;
        }
        if (node.isInternal()) {
            IOUtils.writeByte(os, TYPE_INTERNAL);
            for (int i = 0;i < 8;i++) {
                writeOctree(os, ((Octree.Internal)node).child(i));
            }
        } else {
            Octree.Leaf leaf = (Octree.Leaf)node;
            IOUtils.writeByte(os, TYPE_LEAF);

            IOUtils.writeByte(os, leaf.vsign);

            int sz = CollectionUtils.nonnulli(leaf.edges);
            IOUtils.writeByte(os, (byte)sz);

            for (int i = 0;i < 12;i++) {
                HermiteData h = leaf.edges[i];
                if (h != null) {
                    IOUtils.writeByte(os, (byte)i);

                    int axis = i/4;
                    float minf = leaf.min.get(axis);
                    float t = Maths.inverseLerp(h.point.get(axis), minf, minf+leaf.size);  assert t >= 0F && t <= 1F;
                    IOUtils.writeFloat(os, t);

                    IOUtils.writeFloat(os, h.norm.x);
                    IOUtils.writeFloat(os, h.norm.y);
                    IOUtils.writeFloat(os, h.norm.z);
                }
            }
            // METADATA byte[short]
        }
    }




    public static Octree collapse(Octree node) {
        if (node.isInternal()) {
            Octree.Internal internal = (Octree.Internal)node;
            for (int i = 0;i < 8;i++) {
                internal.child(i, collapse(internal.child(i)));
            }
        }
        return node.collapsed() ? null : node;
    }

    public static void dbgprint(Octree node, int dep, String pf) {
        System.out.printf("L%s|%s%s", dep, " ".repeat(dep*2), pf);
        if (node == null) {
            System.out.println("[NULL]");
        } else if (node.isInternal()) {
            System.out.println("[INTERNAL]");
            Internal intern = (Internal)node;
            for (int i = 0;i < 8;i++) {
                dbgprint(intern.child(i), dep+1, "@"+i);
            }
        } else {
            Leaf lf = (Leaf)node;
            System.out.println(String.format("[LEAF] fp:%-38s  |min:%-22s sz:%-8s|  esz:%s", lf.featurepoint, lf.min,lf.size, CollectionUtils.nonnulli(lf.edges)));
        }
    }

    public static void dbgaabbobj(Octree node, String outobj, Vector3f min, float sz) {
        StringBuilder sb = new StringBuilder();
        dbgaabbobj(sb, node, min, sz, Val.zero());
        try {
            IOUtils.write(sb.toString(), new File(outobj));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static void dbgaabbobj(StringBuilder sb, Octree node, Vector3f min, float size, Val vi) {
        if (node == null) return;
        dbgAppendAABB(sb, min, size, vi);
        if (node.isInternal()) {
            Vector3f tmp = new Vector3f();
            for (int i = 0;i < 8;i++) {
                dbgaabbobj(sb, ((Internal)node).child(i), tmp.set(min).addScaled(size/2f,VERT[i]),size/2f,vi);
            }
        }
    }
    private static void dbgAppendAABB(StringBuilder obj, Vector3f min, float sz, Val vi) {
        Vector3f p = new Vector3f();
        for (int i = 0;i < 8;i++) {
            p.set(min).addScaled(sz, VERT[i]);
            obj.append(String.format("v %s %s %s\n",p.x,p.y,p.z));
        }
        for (int i = 0;i < 12;i++) {
            obj.append(String.format("l %s %s\n",(int)vi.val+1+EDGE[i][0], (int)vi.val+1+EDGE[i][1]));
        }
        vi.val += 8;
    }
}
