package outskirts.client.render.isoalgorithm.csg;

import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;

import static outskirts.util.logging.Log.LOGGER;

public class CSGOp {

    /**
     *
     * @param o1 been oper.
     * @param o2 the  oper.
     */
    public static Octree opSet(Octree o1, Octree o2) {
//        if (o1==null && o2==null) return;
        if (o1.isInternal() && o2.isInternal()) {
            Octree.Internal out = new Octree.Internal();
            for (int i = 0; i < 8; i++) {
                out.child(i, opSet(((Octree.Internal)o1).child(i), ((Octree.Internal)o2).child(i)) );
            }
            return out;
        } else if (o1.isLeaf() && o2.isLeaf()) {
            Octree.Leaf out = new Octree.Leaf((Octree.Leaf)o1);
            Octree.Leaf opr = (Octree.Leaf)o2;
            if (!opr.vempty()) {
                for (int i = 0; i < 12; i++) { HermiteData sh = opr.edges[i]; int[] eg = Octree.EDGE[i];
                    if (opr.sign(eg[0]) != opr.sign(eg[1])) {
                        assert sh != null;
                        out.edges[i] = new HermiteData(sh);
//                        LOGGER.info("  "+i);
//                        int[] edge = Octree.EDGE[i];
//                        if (opr.sign(edge[0])) {out.sign(edge[0], true); LOGGER.info("SET SG E0 ("+edge[0]); }
//                        if (opr.sign(edge[1])) {out.sign(edge[1], true); LOGGER.info("SET SG E1 ("+edge[1]); }
//                        LOGGER.info("  "+out.toString());
                    }
                }
                out.material = opr.material;

                for (int i = 0;i < 8;i++) {
                    if (opr.sign(i))
                        out.sign(i, true);
                }
            }
//            out.computefp();
            out.cutEdgesByVSigns();
            try {
                out.validate();
            } catch (Throwable ex) {
                ex.printStackTrace();
                LOGGER.info("O1: "+o1);
                LOGGER.info("O2: "+o2);
                LOGGER.info("OUT: "+out);

                //[15:15:46][CSGOp.java:45][main/INFO]: O1: Leaf{
                //	vsign=110011 (SCES:4)
                //	edges=[null, null, null, null, HermiteData{p=[7.5, 4.3053303, 5.5],n=[0.21415876, 0.95008, -0.22690125]}, HermiteData{p=[7.5, 4.482548, 6.0],n=[0.3623084, 0.8913457, -0.2724619]}, HermiteData{p=[8.0, 4.192053, 5.5],n=[0.26691222, 0.9334417, -0.23967572]}, HermiteData{p=[8.0, 4.3161063, 6.0],n=[0.3514817, 0.9304809, -0.103275836]}, null, null, null, null]
                //	min=[7.5, 4.0, 5.5], sz=0.5
                //	featurepoint=[7.75, 4.3240094, 5.75]
                //	material=outskirts.material.MaterialStone@6273c5a4
                //}
                //[15:15:46][CSGOp.java:46][main/INFO]: O2: Leaf{
                //	vsign=11111010 (SCES:4)
                //	edges=[HermiteData{p=[7.6073537, 4.0, 5.5],n=[-0.29925784, -0.12232612, -0.94629866]}, null, HermiteData{p=[7.5626974, 4.5, 5.5],n=[-0.3168325, 0.07819493, -0.9452528]}, null, null, null, null, null, HermiteData{p=[7.5, 4.0, 5.5286136],n=[-0.34124422, -0.12185591, -0.9320427]}, HermiteData{p=[7.5, 4.5, 5.516699],n=[-0.34121633, 0.078091085, -0.9367354]}, null, null]
                //	min=[7.5, 4.0, 5.5], sz=0.5
                //	featurepoint=[0.0, 0.0, 0.0]
                //	material=outskirts.material.MaterialDirt@5d465e4b
                //}
                //[15:15:46][CSGOp.java:47][main/INFO]: OUT: Leaf{
                //	vsign=1111011 (SCES:6)
                //	edges=[null, null, HermiteData{p=[7.5626974, 4.5, 5.5],n=[-0.3168325, 0.07819493, -0.9452528]}, null, HermiteData{p=[7.5, 4.3053303, 5.5],n=[0.21415876, 0.95008, -0.22690125]}, null, null, HermiteData{p=[8.0, 4.3161063, 6.0],n=[0.3514817, 0.9304809, -0.103275836]}, null, HermiteData{p=[7.5, 4.5, 5.516699],n=[-0.34121633, 0.078091085, -0.9367354]}, null, null]
                //	min=[7.5, 4.0, 5.5], sz=0.5
                //	featurepoint=[0.0, 0.0, 0.0]
                //	material=outskirts.material.MaterialDirt@5d465e4b
                //}
            }
            return out;
        } else
            throw new IllegalStateException();
    }

}
