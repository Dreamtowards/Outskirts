package outskirts.client.render.isoalgorithm.csg;

import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.Val;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class CSG {

    public static void difference(Octree ben, TrifFunc opr) {
        if (ben==null) return;
        if (ben.isInternal()) {
            Octree.Internal intern = (Octree.Internal)ben;
            for (int i = 0;i < 8;i++) {
                difference(intern.child(i), opr);
            }
        } else {
            Octree.Leaf leaf = (Octree.Leaf)ben;

            float[] v = new float[8];
            Vector3f tmp = vec3(0);
            for (int i = 0;i < 8;i++) {
                float f = opr.sample( tmp.set(leaf.min).addScaled(leaf.size, Octree.VERT[i]) );
                v[i] = f;
                if (f < 0)
                    leaf.sign(i, false);
            }

            for (int i = 0;i < 12;i++) {
                int[] eg = Octree.EDGE[i];
                float f0 = v[eg[0]];
                float f1 = v[eg[1]];
                // if the edge original intersection point has inside the OPR, update point and norm by the OPR.
                if (f0<0 || f1<0) {  // edge vert inside. need forther test
                    if (f0<0 != f1<0) {  // sign-change.
                        float opr_t = Maths.inverseLerp(0, f0, f1);  assert opr_t >= 0f && opr_t < 1f;
                        if (leaf.edges[i] != null) {  // already had hermitedata intersection-point. if the point outside the OPR, just jump over, do not need substract.
                            float ben_t = leaf.edgept(i);
                            if (f0 < 0 ? (ben_t > opr_t) : (ben_t < opr_t))  // the original intersection-point outside of the OPR.
                                continue;
                        }
                        HermiteData h = new HermiteData();
                        Octree.edgelerp(leaf, i, opr_t, h.point);
                        Maths.gradient(opr, h.point, h.norm).negate();
                        leaf.edges[i] = h;
                    }
                }
            }
        }
    }

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
            return union(expand((Octree.Leaf)o1), o2);
        }
        if (o1.isInternal() && o2.isLeaf()) {
            return union(o1, expand((Octree.Leaf)o2));
        }
        throw new IllegalStateException();
    }


    private static void autoVerts(int[] vs) {

        while (CollectionUtils.contains(vs,-1)) {
            for (int i = 0;i < 12;i++) {
                int[] eg = Octree.EDGE[i];
                int s0 = vs[eg[0]];
                int s1 = vs[eg[1]];
                if (s0 != -1 && s1==-1) {  // s0 valid, s1 invalid, set s1 as s0.
                    vs[eg[1]] = s0;
                } else if (s1 != -1 && s0==-1) {  // s1 valid, s0 invalod, set s0 as s1.
                    vs[eg[0]] = s1;
                }
            }
        }
    }

    /**
     * "Expand" a Leaf into 8 leaf children of a InternalNode.
     */
    public static Octree.Internal expand(Octree.Leaf leaf) {
        Octree.Internal expan = new Octree.Internal();

        float subsz = leaf.size/2f;
        Val t = Val.zero();

        for (int i = 0;i < 8;i++) {
            int[] vs = CollectionUtils.fill(new int[8], -1);
            Octree.Leaf sub = new Octree.Leaf(vec3(leaf.min).addScaled(subsz, Octree.VERT[i]), subsz);
            sub.material = leaf.material;
            for (int j = 0;j < 12;j++) {  // edge of subleaf.
                Vector3f rpos = Octree.edgelerp(sub, j, 0, null);
                Vector3f rdir = Vector3f.AXES[j/4];
                float minDistan = Float.MAX_VALUE;
                for (int k = 0;k < 12;k++) {  // edge of mainLeaf.
                    if (leaf.signchange(k)) {
                        HermiteData h = leaf.edges[k];
                        if (Maths.intersectRayPlane(rpos, rdir, h.point, h.norm, t) && t.val >= 0 && t.val < subsz) {
                            Vector3f p = vec3(rpos).addScaled(t.val, rdir);
                            float distan = vec3(p).sub(h.point).length();
                            if (distan < minDistan) {
                                minDistan = distan;
                                HermiteData se = new HermiteData();
                                se.point.set(rpos).addScaled(t.val, rdir);
                                se.norm.set(h.norm);
                                sub.edges[j] = se;
                                boolean v0s = Vector3f.dot(se.norm, rdir) > 0;
                                int[] eg = Octree.EDGE[j];
                                vs[eg[0]] = v0s ? 1 : 0;
                                vs[eg[1]] = v0s ? 0 : 1;
                            }
                        }
                    }
                }
            }
            if (vs[i] != -1) {
                if ( leaf.sign(i) ? vs[i]==1 : vs[i]==0 ) {}
                else LOGGER.warn("Corner VSign error.");
            } else {
                vs[i] = leaf.sign(i) ? 1 : 0;
            }
            autoVerts(vs);
            for (int j=0;j<8;j++) {
                sub.sign(j, vs[j]==1);
            }
            sub.validate();
            if (!sub.vempty())
                expan.child(i, sub);
        }

        return expan;
    }

}
