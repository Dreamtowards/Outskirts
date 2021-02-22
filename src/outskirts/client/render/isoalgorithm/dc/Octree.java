package outskirts.client.render.isoalgorithm.dc;

import org.json.JSONArray;
import org.json.JSONObject;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvBFAVG;
import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvDCJAM3;
import outskirts.material.Material;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.physics.collision.shapes.Raycastable;
import outskirts.physics.collision.shapes.concave.BvhTriangleMeshShape;
import outskirts.util.*;
import outskirts.util.function.TriConsumer;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

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


    public static class Internal extends Octree {

        private Octree[] children = new Octree[8];

        public Octree child(int i) {
            return children[i];
        }
        public void child(int i, Octree node) {
            children[i] = node;
        }
        public int childidx(Octree target) {
            for (int i = 0;i < 8;i++) {
                if (children[i] == target) return i;
            }
            return -1;
        }

        @Override
        public byte type() { return TYPE_INTERNAL; }

        @Override
        public String toString() {
            return "Internal"+Arrays.toString(children);
        }
    }

    /**
     * Stores Hermite Data.  Rd. Material.
     *
     */
    public static class Leaf extends Octree {

        private static final byte SG_FULL = (byte)0xFF;
        private static final byte SG_EMPTY = (byte)0x00;

        /** Cell Vertices Signums.  NotNumVal. Jus. 8 bits.  bit0: Empty, bit1: Solid.  indices.{76543210} */
        public byte vsign;

        // is there has data-repeat.? some edge are shared.
        /** Hermite data for Edges.  only sign-changed edge are nonnull. else just null element. */
        public final HermiteData[] edges = new HermiteData[12];

        public final Vector3f featurepoint = new Vector3f();
        private int lascomp_fpdshash;

        public final Vector3f min = new Vector3f();
        public final float size;  // actually size.

        public Material material;

        public Leaf(Vector3f minVal, float size) {
            this.min.set(minVal);
            this.size = size;
        }

        public Leaf(Leaf src) {
            vsign = src.vsign;
            for (int i = 0;i < 12;i++) {
                HermiteData sh = src.edges[i];
                if (sh != null)
                    edges[i] = new HermiteData(sh);
            }
            min.set(src.min);
            size = src.size;
            material = src.material;
        }

        @Override
        public byte type() { return TYPE_LEAF; }

        /**
         * @return is "solid".
         */
        public boolean sign(int vi) { assert vi >= 0 && vi < 8;
            return ((vsign >>> vi) & 1) == 1;
        }
        public void sign(int vi, boolean solid) { assert vi >= 0 && vi < 8;
            if (solid) {
                vsign |= 1 << vi;
            } else {
                vsign &= ~(1 << vi);
            }
        }

        private boolean lodObjDontUseHermiteDataFp = false;
        private boolean hasfp() {
            return !vempty() && !vfull();
        }
        void computefp() {
            if (!hasfp()) throw new IllegalStateException("this Leaf dosent have featurepoint.");
            if (lodObjDontUseHermiteDataFp) return;
            int hash = Arrays.hashCode(edges);
            if (lascomp_fpdshash != hash) {
                lascomp_fpdshash = hash;

                Octree.computefeaturepoint(this);
            }
        }

        public boolean vfull() { return vsign==SG_FULL; }
        public boolean vempty() { return vsign==SG_EMPTY; }

        public void clearedges() { Arrays.fill(edges, null); }

        // size: 256. Cell Vertex-Signs -> Cell Sign-Changed Edges Number.  Jus. FastLookupTable.
        // for (int i = 0;i < 12;i++) if (lf.signchanged(i)) n++;
        private static final int[] _VSIGN_SIGNCHANGE_EDGES_NUMTB = {0, 3, 3, 4, 3, 4, 6, 5, 3, 6, 4, 5, 4, 5, 5, 4, 3, 4, 6, 5, 6, 5, 9, 6, 6, 7, 7, 6, 7, 6, 8, 5, 3, 6, 4, 5, 6, 7, 7, 6, 6, 9, 5, 6, 7, 8, 6, 5, 4, 5, 5, 4, 7, 6, 8, 5, 7, 8, 6, 5, 8, 7, 7, 4, 3, 6, 6, 7, 4, 5, 7, 6, 6, 9, 7, 8, 5, 6, 6, 5, 4, 5, 7, 6, 5, 4, 8, 5, 7, 8, 8, 7, 6, 5, 7, 4, 6, 9, 7, 8, 7, 8, 8, 7, 9, 12, 8, 9, 8, 9, 7, 6, 5, 6, 6, 5, 6, 5, 7, 4, 8, 9, 7, 6, 7, 6, 6, 3, 3, 6, 6, 7, 6, 7, 9, 8, 4, 7, 5, 6, 5, 6, 6, 5, 6, 7, 9, 8, 9, 8, 12, 9, 7, 8, 8, 7, 8, 7, 9, 6, 4, 7, 5, 6, 7, 8, 8, 7, 5, 8, 4, 5, 6, 7, 5, 4, 5, 6, 6, 5, 8, 7, 9, 6, 6, 7, 5, 4, 7, 6, 6, 3, 4, 7, 7, 8, 5, 6, 8, 7, 5, 8, 6, 7, 4, 5, 5, 4, 5, 6, 8, 7, 6, 5, 9, 6, 6, 7, 7, 6, 5, 4, 6, 3, 5, 8, 6, 7, 6, 7, 7, 6, 6, 9, 5, 6, 5, 6, 4, 3, 4, 5, 5, 4, 5, 4, 6, 3, 5, 6, 4, 3, 4, 3, 3, 0};
        public int sc_edges() {
            return _VSIGN_SIGNCHANGE_EDGES_NUMTB[vsign & 0xff];
        }

        public boolean signchange(int edgei) {
            int[] eg = EDGE[edgei];
            return sign(eg[0]) != sign(eg[1]);
        }

        /**
         * min+ t*EDGE_AXIS*size == intersection-point.  t: [0.0, 1.0]. dir-from-min-to-max
         */
        public float edgept(int i) {
            int axis = Octree.edgeaxis(i);
            float f = edges[i].point.get(axis);
            float minf = min.get(axis);
            float t = Maths.inverseLerp(f, minf, minf+size); assert t >= 0F && t <= 1F;
            return t;
        }

        public void cutEdgesByVSigns() {
            for (int i = 0;i < 12;i++) {
                int[] edge = EDGE[i];
                if (sign(edge[0]) == sign(edge[1])) {
                    edges[i] = null;
                }
            }
        }

        public void validate() {
            assert !vempty() || sc_edges() == 0;
            for (int i = 0;i < 12;i++) {
                HermiteData h = edges[i];
                if (h != null) {
                    assert Vector3f.isFinite(h.point) && Vector3f.isFinite(h.norm);
                    int axis = Octree.edgeaxis(i);
                    float f = h.point.get(axis) - min.get(axis);
                    assert f >= 0 && f <= size;
                }
            }
            for (int i = 0;i < 12;i++) {
                int[] eg = EDGE[i];
                if (sign(eg[0]) != sign(eg[1]))
                    assert edges[i] != null : "Not HermiteData on the sign-changed edge.";
            }
        }

        @Override
        public String toString() {
            return "Leaf{\n" +
                    "\tvsign=" + Integer.toBinaryString(vsign & 0xff) + " (SCES:"+ sc_edges()+") \n"+
                    "\tedges=" + Arrays.toString(edges) +"\n"+
                    "\tmin="+min+", sz=" + size +". featurepoint=" + featurepoint +". material=" + material  +"\n"+
                    '}';
        }
        public static String dbgtojson(Leaf lf) {  // debug.
            return new JSONObject(
                    CollectionUtils.asMap(
                    "sign", lf.vsign & 0xff,
                    "edge", Arrays.stream(lf.edges).map(h -> h==null?null:h.toString()).toArray(),
                    "min", lf.min.toString(),
                    "size", lf.size)
            ).toString(4);
        }
        public static Leaf dbgfromjson(String str) {
            JSONObject j = new JSONObject(str);
            Leaf lf = new Leaf(Vector3f.fromString(j.getString("min")), j.getFloat("size"));
            lf.vsign = (byte)j.getInt("sign");
            JSONArray ja = j.getJSONArray("edge");
            for (int i = 0;i < 12;i++) {
                if (!ja.isNull(i)) {
                    lf.edges[i] = HermiteData.fromString(ja.getString(i));
                }
            }
            return lf;
        }
    }


    private static void computefeaturepoint(Octree.Leaf cell) {
        List<Vector3f> ps = new ArrayList<>();
        List<Vector3f> ns = new ArrayList<>();
        for (int i = 0;i < 12;i++) {
            if (cell.signchange(i)) {
                HermiteData h = cell.edges[i];
                assert h != null : "No HermiteData on sc edge.";

                ps.add( h.point );
                ns.add( h.norm  );
            }
        }
        if (ps.size() != 0) {
//                    cell.featurepoint.set(QEFSolvDCJAM3.wCalcQEF(ps, ns));
//            cell.featurepoint.set(QEFSolvBFAVG.doAvg(ps, ns));
            cell.featurepoint.set(cell.min).add(cell.size/2f);
        }
        assert Vector3f.isFinite(cell.featurepoint) && cell.featurepoint.lengthSquared()!=0
                : "Illegal fp("+cell.featurepoint+") ps:"+ps+", ns:"+ns + " SG: "+Integer.toBinaryString(cell.vsign & 0xff) + "  SCES: "+cell.sc_edges();
    }


    ////////////////// I.O. //////////////////


    public static Octree readOctree(InputStream is, Vector3f min, float size) throws IOException {
        byte type = IOUtils.readByte(is);
        switch (type) {
            case TYPE_NULL:
                return null;
            case TYPE_INTERNAL: {
                Octree.Internal internal = new Octree.Internal();
                float subsz = size/2f;
                Vector3f submin = new Vector3f();
                for (int i = 0;i < 8;i++) {
                    submin.set(min).addScaled(subsz, VERT[i]);
                    internal.child(i, readOctree(is, submin, subsz));
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
                // METADATA byte[short]
                int idx = IOUtils.readInt(is);
                leaf.material = Material.REGISTRY.values().get(idx);
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

                    float t = leaf.edgept(i);
                    IOUtils.writeFloat(os, t);

                    IOUtils.writeFloat(os, h.norm.x);
                    IOUtils.writeFloat(os, h.norm.y);
                    IOUtils.writeFloat(os, h.norm.z);
                }
            }
            // METADATA byte[short]
            IOUtils.writeInt(os, Material.REGISTRY.values().indexOf(leaf.material));
        }
    }


    ////////////////// COMMON UTILITY //////////////////



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
            Octree.Internal intern = (Octree.Internal)node;
            for (int i = 0;i < 8;i++) {
                intern.child(i, collapse(intern.child(i)));
            }

            // MERGE Leaves. when children all leaves, same material, all full-signs.
            if (intern.child(0) instanceof Leaf) {
                Leaf first = (Leaf)intern.child(0);
                boolean merg = true;
                for (int i = 0;i < 8;i++) {
                    Octree c = intern.child(i);
                    if (! (c instanceof Leaf && ((Leaf)c).vfull() && ((Leaf)c).material==first.material) ) {
                        merg = false; break;
                    }
                }
                if (merg) {
                    Leaf lf = new Leaf(first.min, first.size*2);
                    lf.material = first.material;
                    lf.vsign = Leaf.SG_FULL;
                    return lf;
                }
            }
        }
        return _isNodeEmpty(node) ? null : node;
    }
    private static boolean _isNodeEmpty(Octree node) {
        return node.isLeaf() ? ((Leaf)node).vempty() : CollectionUtils.nonnulli(((Internal)node).children) == 0;
    }

    /**
     * @param rp rel-unit-pos in the InternalNode. xyz:[0-1).
     */
    public static int cellidx(Vector3f rp) {
        assert rp.x>=0&&rp.x<=1 && rp.y>=0&&rp.y<=1 && rp.z>=0&&rp.z<=1 : "Position Outbound. "+rp;
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
    public static Octree.Leaf findLeaf(Octree.Internal node, Vector3f rp, Ref<Octree.Internal> lp) {
        int idx = cellidx(rp);
        Octree sub = node.child(idx);
        if (sub == null) return null;
        if (sub.isLeaf()) {
            if (lp!=null) lp.value = node;
            return (Octree.Leaf)sub;
        }
        cellrpclip(idx, rp);
        return findLeaf((Internal)sub, rp, lp);
    }

    // traverse
    public static void forEach(Octree node, TriConsumer<Octree, Vector3f, Float> call, Vector3f min, float size) {
        if (node==null) return;
        call.accept(node, min, size);
        if (node.isInternal()) {
            Octree.Internal intern = (Octree.Internal)node;
            float subsz = size/2f;
            Vector3f submin = new Vector3f();
            for (int i = 0;i < 8;i++) {
                submin.set(min).addScaled(subsz, VERT[i]);
                forEach(intern.child(i), call, submin, subsz);
            }
        }
    }
    public static void forEach(Octree node, Consumer<Octree> call) {
        Octree.forEach(node, (n,m,s) -> call.accept(n), vec3(0), 0);
    }



    ////////////////// SAMPLING //////////////////



    /**
     * Sample from Signed Distance Function.  (field value: inner<0, outer>0.)
     * required: cell.min, cell.size.
     * samples:  cell.edges, cell.sign
     */
    public static void sampleSDF(Octree.Leaf cell, TrifFunc f) {
        float[] v = new float[8];
        Vector3f tmpp = new Vector3f();
        for (int i = 0;i < 8;i++) {
            v[i] = f.sample( tmpp.set(cell.min).addScaled(cell.size, VERT[i]) );
            cell.sign(i, v[i] < 0);
        }

        cell.clearedges();
        for (int i = 0;i < 12;i++) {
            int[] edge = EDGE[i];
            float val0 = v[edge[0]],val1 = v[edge[1]];
            if (val0<0 != val1<0) {
                HermiteData h = new HermiteData();

                float t = Maths.inverseLerp(0, val0, val1);
                edgelerp(cell, i, t, h.point);

                Maths.gradient(f, h.point, h.norm);
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




    private static Octree building(Vector3f min, float size, Consumer<Octree.Leaf> samp, int currdep, int depthcap) {
        if (currdep == depthcap) {  // do sample.
            Octree.Leaf lf = new Octree.Leaf(min, size);
            samp.accept(lf);
            return lf;
        } else if (currdep < depthcap) {  // recurise until reach the depthcap.
            Octree.Internal intern = new Octree.Internal();
            float subsize = size/2f;
            Vector3f submin = new Vector3f();
            for (int i = 0;i < 8;i++) {
                submin.set(min).addScaled(subsize, Octree.VERT[i]);
                intern.child(i, building(submin, subsize, samp, currdep+1, depthcap));
            }
            return intern;
        } else throw new IllegalStateException();
    }


    public static Octree fromSDF(Vector3f min, float size, TrifFunc f, int depthcap, Consumer<Leaf> fl) {
        return building(min, size, lf -> {
            Octree.sampleSDF(lf, f);
            if (!lf.vempty()) {
                fl.accept(lf);
            }
//            lf.computefp();
        }, 0, depthcap);
    }

    public static Octree fromMESH(Vector3f min, float size, Raycastable mesh, int depthcap) {
        return building(min, size, lf -> {
            Octree.sampleMESH(lf, mesh);
//            lf.computefp();
        }, 0, depthcap);
    }
    public static Octree fromMESH(VertexBuffer vbuf, int depthcap) {
        AABB aabb = AABB.bounding(vbuf.positions, null).grow(0.00001f);
        float sz = Maths.max(aabb.max.x-aabb.min.x, aabb.max.y-aabb.min.y, aabb.max.z-aabb.min.z);
        BvhTriangleMeshShape mesh = new BvhTriangleMeshShape(CollectionUtils.range(vbuf.positions.size()/3), vbuf.posarr());
        return fromMESH(aabb.min, sz, mesh, depthcap);
    }


    // usize: until size
    public static Octree doLOD(Octree.Internal intern, float usize, Vector3f min, float size) {
        float subsz = size/2f;
        Vector3f submin = new Vector3f();
        for (int i = 0;i < 8;i++) {
            Octree child = intern.child(i);
            if (child != null && child.isInternal()) {
                submin.set(min).addScaled(subsz, Octree.VERT[i]);
                intern.child(i, doLOD((Internal)child, usize, submin, subsz));
            }
        }
        assert CollectionUtils.nonnulli(intern.children) == 8;  // curr disabled collapse.
//        if (CollectionUtils.nonnulli(intern.children) == 0) {
//            LOGGER.info("NO CHIL");
//            return null;
//        }
        if (size <= usize) {
            Octree.Leaf lf = new Octree.Leaf(min, size);
            Vector3f avgfp = new Vector3f();
            int avgn = 0;
            List<Material> mtls = new ArrayList<>();
            for (int i = 0;i < 8;i++) {
                Octree.Leaf c = (Leaf)intern.child(i);
                // get signs.
                lf.sign(i, c != null && c.sign(i));
                // avg verts
                if (c != null && c.hasfp()) {
                    c.computefp();
                    avgfp.add(c.featurepoint);
                    avgn++;
                }
                if (c != null && !c.vempty()) {
                    mtls.add(c.material);
                    if (!c.sign(i))
                        mtls.add(c.material); // add weight. for 'surface' children.
                }
            }
            if (!lf.vempty()) {
                lf.lodObjDontUseHermiteDataFp = true;
                lf.featurepoint.set(avgfp.scale(1f / avgn));

                lf.material = CollectionUtils.mostDuplicated(mtls);
            }
            return lf;
        } else {

            return intern;
        }
    }





    //  DEBUG.

    public static void dbgprint(Octree node, int dep, String pf) {
        System.out.printf("L%s|%s%s", dep, StringUtils.repeat(" ", dep*2), pf);
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
    private static boolean DBG_AABB_LEAF_VT = true;
    private static boolean DBG_AABB_INTERN = true;
    private static boolean DBG_AABB_HERMIT = true;
    private static void dbgaabbobjmode(boolean leaf, boolean intern, boolean hermit, boolean lfvt) {
        DBG_AABB_LEAF=leaf; DBG_AABB_INTERN=intern; DBG_AABB_HERMIT=hermit; DBG_AABB_LEAF_VT=lfvt;
    }
    public static void dbgaabbC(Octree node, Vector3f min, float size, String outfileprefix) {
        dbgaabbobjmode(true, false, false, false);
        Octree.dbgaabbobj(node, outfileprefix+"_l.obj", min, size);
        dbgaabbobjmode(false, true, false, false);
        Octree.dbgaabbobj(node, outfileprefix+"_i.obj", min, size);
        dbgaabbobjmode(false, false, true, false);
        Octree.dbgaabbobj(node, outfileprefix+"_h.obj", min, size);
        dbgaabbobjmode(false, false, false, true);
        Octree.dbgaabbobj(node, outfileprefix+"_lv.obj", min, size);
    }
    private static void dbgaabbobj(StringBuilder sb, Octree node, Vector3f min, float size, Val vi) {
        if (node == null) return;
        if ((DBG_AABB_LEAF && node.isLeaf()) || (DBG_AABB_INTERN && node.isInternal()))
            dbgAppendAABB(sb, min, size, vi);
        if (DBG_AABB_LEAF_VT && node.isLeaf()) {
            Leaf lf = (Leaf)node;
            for (int i = 0;i<8;i++) {
                if (lf.sign(i)) {
                    Vector3f v = vec3(lf.min).addScaled(lf.size, VERT[i]);
                    dbgAppendLine(sb, v, v, vi);
                }
            }
        }
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
