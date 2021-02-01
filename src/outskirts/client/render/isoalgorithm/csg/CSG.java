package outskirts.client.render.isoalgorithm.csg;

import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.Maths;
import outskirts.util.Val;
import outskirts.util.vector.Vector3f;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;

public class CSG {

    /**
     *
     * @param o1 been oper.
     * @param o2 the  oper.
     */
    public static Octree union(Octree o1, Octree o2) {
        if (o1==null && o2==null) return null;
        if (o1==null) return o2;
        if (o2==null) return o1;
        if (o1.isInternal() && o2.isInternal()) {
            Octree.Internal out = new Octree.Internal();
            for (int i = 0; i < 8; i++) {
                out.child(i, union(((Octree.Internal)o1).child(i), ((Octree.Internal)o2).child(i)) );
            }
            return out;
        }
        if (o1.isLeaf() && o2.isLeaf()) {
            Octree.Leaf out = new Octree.Leaf((Octree.Leaf)o1);
            Octree.Leaf opr = (Octree.Leaf)o2;
//            if (opr.vempty()) return out;
            for (int i = 0; i < 12; i++) {
                if (opr.signchange(i))
                    out.edges[i] = new HermiteData(opr.edges[i]);
            }
            for (int i = 0;i < 8;i++) {
                if (opr.sign(i))
                    out.sign(i, true);
            }
            if (opr.material != null)  // shouldn't be null.
                out.material = opr.material;

            out.cutEdgesByVSigns();
            out.validate();
            return out;
        }
        if (o1.isLeaf() && o2.isInternal()) {
            Octree.Internal intern = expand((Octree.Leaf)o1);
            return union(intern, o2);
        }
        if (o1.isInternal() && o2.isLeaf()) {
            Octree.Internal intern = expand((Octree.Leaf)o2);
            return union(o1, intern);
        }
        throw new IllegalStateException();
    }

    /**
     * "Expand" a Leaf into 8 leaf children of a InternalNode.
     */
    public static Octree.Internal expand(Octree.Leaf leaf) {
        Octree.Internal expan = new Octree.Internal();

        float hfsz = leaf.size/2f;
        Val t = Val.zero();

        for (int i = 0;i < 8;i++) {
            Octree.Leaf sub = new Octree.Leaf(vec3(leaf.min).addScaled(hfsz, Octree.VERT[i]), hfsz);
            sub.material = leaf.material;
            for (int j = 0;j < 12;j++) {  // edge of subleaf.
                Vector3f rpos = Octree.edgelerp(sub, j, 0, null);
                Vector3f rdir = Vector3f.AXES[j/4];
                for (int k = 0;k < 12;k++) {  // edge of mainLeaf.
                    if (leaf.signchange(k)) {
                        HermiteData h = leaf.edges[k];
                        if (Maths.intersectRayPlane(rpos, rdir, h.point, h.norm, t)) {
                            if (t.val >= 0 && t.val < hfsz) {
                                HermiteData se = new HermiteData();
                                se.point.set(rpos).addScaled(t.val, rdir);
                                se.norm.set(h.norm);
                                sub.edges[j] = se;
                                boolean v0s = Vector3f.dot(se.norm, rdir) > 0;
                                int[] eg = Octree.EDGE[j];
                                sub.sign(eg[0], v0s);
                                sub.sign(eg[1], !v0s);
                            }
                        }
                    }
                }
            }
            if (!sub.vempty())
                expan.child(i, sub);
        }

        return expan;

//        HermiteData[] centedge = new HermiteData[6];  // 2 edge * 3 axes.
//        for (int i = 0;i < 12;i++) {
//            for (int j = 0;j < 3;j++) {  // axes
//                if (leaf.signchange(i)) {
//                    HermiteData h = leaf.edges[i];
//                    float hfsz = leaf.size/2f;
//                    Val t = Val.zero();
//                    Vector3f rpos = vec3(leaf.min).add(vec3(hfsz).setv(j, 0));
//                    Vector3f rdir = Vector3f.AXES[j];
//                    if (Maths.intersectRayPlane(rpos, rdir, h.point, h.norm, t)) {
//                        if (t.val >= 0 && t.val < leaf.size) {
//                            int mnmx = t.val < hfsz ? 0 : 1;
//                            int idx = j*2+mnmx;
//                            HermiteData ce = centedge[idx];
//                            if (ce == null)
//                                ce = centedge[idx] = new HermiteData();
//                            ce.norm.set(h.norm);
//                            ce.point.set(vec3(rpos).addScaled(t.val-mnmx*hfsz, rdir));
//                            // AVG.?
//                        }
//                    }
//                }
//            }
//        }
    }

}
