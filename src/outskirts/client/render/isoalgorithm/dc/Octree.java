package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvBFAVG;
import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvDCJAM3;
import outskirts.init.Materials;
import outskirts.material.Material;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.Raycastable;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.Val;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.function.Consumer;

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

    // from Min to Max in each Edge.
    // orders as X, Y, Z.
    // Diagonal Edge in Cell is in-axis-flip-index edge.  i.e. diag of edge[axis*4 +i] is edge[axis*4 +(3-i)]
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

        public final Vector3f min = new Vector3f();
        public final float size;  // actually size.

        public Material material = Materials.STONE;

        public Leaf(Vector3f minVal, float size) {
            this.min.set(minVal);
            this.size = size;
        }

        @Override
        public byte type() { return TYPE_LEAF; }

        public boolean solid(int vi) { assert vi >= 0 && vi < 8;
            return ((vsign >>> vi) & 1) == 1;
        }
        public void sign(int vi, boolean solid) { assert vi >= 0 && vi < 8;
            vsign |= (solid ? 1 : 0) << vi;
        }

        // when..? not just after change, but just when update-needed. when read-fp.
        public void computefp() {
            Octree.computefeaturepoint(this);
        }

        @Override
        public boolean collapsed() {
            return vsign == SG_FULL || vsign == SG_EMPTY;
        }

        public void clearedges() {
            Arrays.fill(edges, null);
        }
        public int validedges() {
            return CollectionUtils.nonnulli(edges);
        }


        public void validate() {
            assert !collapsed() || validedges() == 0;
            for (int i = 0;i < 12;i++) {
                HermiteData h = edges[i];
                if (h != null) {
                    assert Vector3f.isFinite(h.point) && Vector3f.isFinite(h.norm);
                    int axis = Octree.edgeaxis(i);
                    float f = h.point.get(axis) - min.get(axis);
                    assert f >= 0 && f <= size;
                }
            }
        }
    }


    private static void computefeaturepoint(Octree.Leaf cell) {
        int sz = cell.validedges();
        Vector3f[] ps = new Vector3f[sz];
        Vector3f[] ns = new Vector3f[sz];
        int j = 0;
        for (HermiteData h : cell.edges) {
            if (h != null) {
                ps[j] = h.point;
                ns[j] = h.norm;  j++;
            }
        }
        if (ps.length != 0) {
//            cell.featurepoint.set(QEFSolvDCJAM3.wCalcQEF(Arrays.asList(ps), Arrays.asList(ns)));
            cell.featurepoint.set(QEFSolvBFAVG.doAvg(Arrays.asList(ps), Arrays.asList(ns)));
        }
        assert Vector3f.isFinite(cell.featurepoint) : "Illegal fp("+cell.featurepoint+") ps:"+Arrays.toString(ps)+", ns:"+Arrays.toString(ns);
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

                    float t = IOUtils.readFloat(is);
                    edgelerp(leaf, idx, t, h.point);

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

                    int axis = edgeaxis(i);
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


    /**
     * Edge Lerp of Actually Cell.
     */
    public static Vector3f edgelerp(Octree.Leaf cell, int edgei, float t, Vector3f dest) {
        if (dest==null)dest=new Vector3f();
        assert t >= 0F && t <= 1F;
        int axis = edgeaxis(edgei);
        dest.set(cell.min)
                .addScaled(cell.size, VERT[EDGE[edgei][0]])  // offset to edge-base-vert.
                .addv(axis, cell.size*t);
        return dest;
    }
    private static int edgeaxis(int edgei) {
        return edgei/4;
    }


    /**
     * Collapse Empty nodes.
     */
    public static Octree collapse(Octree node) {
        if (node==null) return null;
        if (node.isInternal()) {
            Octree.Internal internal = (Octree.Internal)node;
            for (int i = 0;i < 8;i++) {
                internal.child(i, collapse(internal.child(i)));
            }
        }
        return node.collapsed() ? null : node;
    }

    /**
     * @param rp rel-unit-pos in the InternalNode. xyz:[0-1).
     */
    public static int cellidx(Vector3f rp) {
        assert rp.x>=0&&rp.x<1 && rp.y>=0&&rp.y<1 && rp.z>=0&&rp.z<1 : "Position Outbound. "+rp;
        int x = Math.round(rp.x);
        int y = Math.round(rp.y);
        int z = Math.round(rp.z);
        return (x << 2) | (y << 1) | z;
    }
    public static Vector3f cellrpclip(int idx, Vector3f dest) { // [0.62, 0.42, 0.023], idx: 4, edit: [2.1273065, 0.4278543, 0.02373588]
        Vector3f v = VERT[idx];
        return dest.addScaled(-0.5f, v).scale(2f);
    }

    /**
     * @param rp Modifiable
     */
    public static Octree.Leaf findOctree(Octree node, Vector3f rp) {
        if (node.isInternal()) {
            int idx = cellidx(rp);
            Octree sub = ((Octree.Internal)node).child(idx);
            if (sub == null) return null;
            cellrpclip(idx, rp);
            return findOctree(sub, rp);
        } else {
            return (Octree.Leaf)node;
        }
    }

    public static void forEach(Octree node, Consumer<Octree> call) {
        if (node==null) return;
        call.accept(node);
        if (node.isInternal()) {
            Octree.Internal intern = (Octree.Internal)node;
            for (int i = 0;i < 8;i++) {
                forEach(intern.child(i), call);
            }
        }
    }



    /**
     * Sample from SignedDensityFunction.
     * required: cell.min, cell.size.
     * samples:  cell.edges, cell.sign
     */
    public static void sampleSDF(Octree.Leaf cell, TrifFunc f) {
        float[] v = new float[8];
        Vector3f tmpp = new Vector3f();
        for (int i = 0;i < 8;i++) {
            v[i] = f.sample( tmpp.set(cell.min).addScaled(cell.size, VERT[i]) );
            cell.sign(i, v[i] > 0);
        }

        cell.clearedges();
        for (int i = 0;i < 12;i++) {
            int[] edge = EDGE[i];
            float val0 = v[edge[0]],val1 = v[edge[1]];
            if (val0>0 != val1>0) {
                HermiteData h = new HermiteData();

                float t = Maths.inverseLerp(0, val0, val1);
                edgelerp(cell, i, t, h.point);

                Maths.grad(f, h.point, h.norm).negate();  // neg(): for positive-solid field gradient, the actuall normal is its neg.
                cell.edges[i] = h;
            }
        }
    }

    /**
     * Sample from TriangleMesh. CCW winding.
     */
    public static void sampleMESH(Octree.Leaf cell, Raycastable mesh) {
        cell.clearedges();

        Vector3f edgebase = new Vector3f();
        Val t = Val.zero();  // ray actually length. not 0-1.
        Vector3f n = new Vector3f();
        for (int i = 0;i < 12;i++) {
            int[] edge = EDGE[i];
            edgelerp(cell, i, 0f, edgebase);
            Vector3f AXIS = Vector3f.AXES[edgeaxis(i)];
            if (mesh.raycast(edgebase, AXIS, t, n) && t.val <= cell.size) {  assert t.val >= 0 && t.val <= cell.size;
                HermiteData h = new HermiteData();

                h.point.set(edgebase).addScaled(t.val, AXIS);
                h.norm.set(n);

                cell.edges[i] = h;
                boolean vbasesolid = Vector3f.dot(AXIS, n) > 0;  // edge-base-vert 'behind' the norm. solid.
                cell.sign(edge[0], vbasesolid);  // there are not appear duplicated-setting. not all edge has intersects.
                cell.sign(edge[1], !vbasesolid);
            }
        }
        cell.validate();
    }








    //  DEBUG.

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
        dbgaabbobj(sb, node, min, sz, Val.of(1));
        try {
            IOUtils.write(sb.toString(), new File(outobj));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static boolean DBG_AABB_LEAF = true;
    private static boolean DBG_AABB_INTERN = true;
    private static boolean DBG_AABB_HERMIT = true;
    private static void dbgaabbobjmode(boolean leaf, boolean intern, boolean hermit) {
        DBG_AABB_LEAF=leaf; DBG_AABB_INTERN=intern; DBG_AABB_HERMIT=hermit;
    }
    public static void dbgaabb3vs(Octree node, Vector3f min, float size, String outprefix) {
        dbgaabbobjmode(true, false, false);
        Octree.dbgaabbobj(node, outprefix+"_l.obj", min, size);
        dbgaabbobjmode(false, true, false);
        Octree.dbgaabbobj(node, outprefix+"_i.obj", min, size);
        dbgaabbobjmode(false, false, true);
        Octree.dbgaabbobj(node, outprefix+"_h.obj", min, size);
    }
    private static void dbgaabbobj(StringBuilder sb, Octree node, Vector3f min, float size, Val vi) {
        if (node == null) return;
        if ((DBG_AABB_LEAF && node.isLeaf()) || (DBG_AABB_INTERN && node.isInternal()))
            dbgAppendAABB(sb, min, size, vi);
        if (DBG_AABB_HERMIT && node.isLeaf()) {
            Leaf lf = (Leaf)node;
            for (int i = 0;i < 12;i++) {
                HermiteData h = lf.edges[i];
                if (h != null) {
                    dbgAppendLine(sb, h.point, new Vector3f(h.point).addScaled(lf.size*.8f, h.norm), vi);
                }
            }
        }
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
            obj.append(String.format("l %s %s\n",(int)vi.val+EDGE[i][0], (int)vi.val+EDGE[i][1]));
        }
        vi.val += 8;
    }
    private static void dbgAppendLine(StringBuilder obj, Vector3f v1, Vector3f v2, Val vi) {
        obj.append(String.format("v %s %s %s\n",v1.x,v1.y,v1.z));
        obj.append(String.format("v %s %s %s\n",v2.x,v2.y,v2.z));
        obj.append(String.format("l %s %s\n",(int)vi.val, (int)vi.val+1));
        vi.val += 2;
    }
}
