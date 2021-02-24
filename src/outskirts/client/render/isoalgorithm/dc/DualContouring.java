package outskirts.client.render.isoalgorithm.dc;

import ext.dualc.TestDualc;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkMeshGen;
import outskirts.material.Material;
import outskirts.util.CollectionUtils;
import outskirts.util.Colors;
import outskirts.util.StringUtils;
import outskirts.util.Val;
import outskirts.util.logging.Log;
import outskirts.util.mx.VertexUtil;
import outskirts.util.vector.Vector3f;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

import static outskirts.client.render.isoalgorithm.dc.Octree.EDGE;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.logging.Log.LOGGER;

/**
 *  Adaptive Resolution of Varying Octree.
 *
 */
public final class DualContouring {


    /**
     * 4 adjacent cells of 6 inter-centric-edges.  indexing to VERT.
     */
    public static final int[][] EDGE_ADJACENT =
            {{0,1,2,3},{4,5,6,7},  // X
             {0,4,1,5},{2,6,3,7},  // Y
             {0,2,4,6},{1,3,5,7}}; // Z


    public static VertexBuffer contouring(Octree node) {
        Objects.requireNonNull(node);
        VertexBuffer vbuf = new VertexBuffer();
        doCellContour(node, vbuf);
        return vbuf;
    }
    static int space = 0;
    static boolean startRootDoFace = false;

    private static void doCellContour(Octree node, VertexBuffer vbuf) {
        LOGGER.info(StringUtils.repeat(" ", space)+"doCell()"); space+=2;
        if (node == null) return;
        if (node.isInternal()) {
            Octree.Internal internal = (Octree.Internal)node;

            // 8 Cell calls
            for (int i = 0;i < 8;i++) {
                doCellContour(internal.child(i), vbuf);
            }
            if (TestDualc.rootNode == node) {
                LOGGER.info(" ============== End Root doCell. start Root doFace, doEdge.");
                startRootDoFace=true;
            }

            // 12 Face calls. 4 * 3 axes.  per between 2 inter-cells.
            Octree[] facepair = new Octree[2];
            for (int i = 0;i < 12;i++) {
                facepair[0] = internal.child(EDGE[i][0]);
                facepair[1] = internal.child(EDGE[i][1]);
                doFaceContour(facepair, i/4, vbuf);
            }

            // 6 Edge calls. 2 * 3 axes.   per among 4 inter-cells.
            Octree[] eadjacent = new Octree[4];
            for (int i = 0;i < 6;i++) {
                eadjacent[0] = internal.child(EDGE_ADJACENT[i][0]);
                eadjacent[1] = internal.child(EDGE_ADJACENT[i][1]);
                eadjacent[2] = internal.child(EDGE_ADJACENT[i][2]);
                eadjacent[3] = internal.child(EDGE_ADJACENT[i][3]);
                doEdgeContour(eadjacent, i/2, vbuf);
            }
        }
        space -= 2;
    }

    public static final int[][] axisFace4PrepEdgeTb = {
            {1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2},  // 0,5,4,1,0
            {0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2},
            {1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}
    };

    /**
     * @param facepair order-required: [0]: min-side, [1] max-side. (relatively.)
     */
    public static void doFaceContour(Octree[] facepair, int axis, VertexBuffer vbuf) {
        LOGGER.info(StringUtils.repeat(" ", space)+"doFace()"); space+=2;
        if (facepair[0]==null || facepair[1]==null) return;
        if (facepair[0].isInternal() || facepair[1].isInternal()) {

            // 4 Face calls. 4 subpair on one axis.
            Octree[] subpair = new Octree[2];
            for (int i = 0;i < 4;i++) {
                int[] subpairvi = EDGE[axis*4+i]; // edge.  filpped to use.
                subpair[0] = facepair[0].isInternal() ? ((Octree.Internal)facepair[0]).child(subpairvi[1]) : facepair[0];
                subpair[1] = facepair[1].isInternal() ? ((Octree.Internal)facepair[1]).child(subpairvi[0]) : facepair[1];
                doFaceContour(subpair, axis, vbuf);
            }

            // 4 Edge calls. 4 interedges between 2 facepair. 2 * 2 face-prep-axes
            final int[][] pairside = {{ 0, 0, 1, 1 }, { 0, 1, 0, 1 }} ;  // 2type. indexing to src-pair.
            Octree[] eadjacent = new Octree[4];
            for (int i = 0;i < 4;i++) {
                int[] tb = axisFace4PrepEdgeTb[axis*4+i];
                for (int j = 0;j < 4;j++) {
                    int pair_i = pairside[ tb[0] ][j];  // index of src-pair.
                    eadjacent[j] = facepair[pair_i].isInternal() ? ((Octree.Internal)facepair[pair_i]).child(tb[1+j]) : facepair[pair_i];
                }
                LOGGER.info("-FaceEAdjacent["+i+"]: tb: " + Arrays.toString(tb) + "   ; leafs: "+leafsposes(eadjacent));
                doEdgeContour(eadjacent, tb[ 5 ], vbuf);
            }
        }
        space -=2;
    }
    private static String leafsposes(Octree[] leafs) {
        return "["+CollectionUtils.toString(Arrays.asList(leafs), ", ", e -> ((Octree.Leaf)e).min.toString())+"]";
    }

    public static void doEdgeContour(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        LOGGER.info(StringUtils.repeat(" ", space)+"doEdge()"); space+=2;
        if (eadjacent[0]==null || eadjacent[1]==null || eadjacent[2]==null || eadjacent[3]==null) return;

        if (eadjacent[0].isLeaf() && eadjacent[1].isLeaf() && eadjacent[2].isLeaf() && eadjacent[3].isLeaf()) {
            processEdgeVertex(eadjacent, axis, vbuf);
        } else {
            Octree[] subadjacent = new Octree[4];
            for (int i = 0;i < 2;i++) {
                for (int j = 0;j < 4;j++) {
                    int egdiagcell = EDGE_ADJACENT[axis*2+i][3-j];  // Diagonal Cell of the Edge.
                    subadjacent[j] = eadjacent[j].isInternal() ? ((Octree.Internal)eadjacent[j]).child(egdiagcell) : eadjacent[j];
                }
                doEdgeContour(subadjacent, axis, vbuf);
            }
        }
        space-=2;
    }

    /**
     * 2 Triangles forms a Quad. indexing to 4 edge-adjacents [0,3].  CCW winding.
     * QUAD winding: |3  2|  i.e. processEdgeVertex()'s eadjacent required winding.
     *               |1  0|  default (not-flip) the normal is pointing axis-positive-dir.
     */
    private static int[] ADJACENT_QUADTRIV = {0, 2, 3, 0, 3, 1};

    /**
     * @param eadjacent required leaves. required {EDGE at the axis} winding, facing either side is ok. (for vaild tris build and 'centric-edge' sample
     *                  these leafs may diff size, may 1 duplicated(the big one, same2times), may diff-parent. but must had a intersected face.
     */
    private static void processEdgeVertex(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        float minsz = Float.MAX_VALUE;  // min depth means smallest one.?
        boolean minsz_signchanged = false;
        boolean flip = false; int min_i=-1;
        int[][] processEdgeMask = {{3,2,1,0},{7,5,6,4},{11,10,9,8}} ;  // note: Y :: 4 6 5 7!!!!

        for (int i = 0;i < 4;i++) {
            Octree.Leaf leaf = (Octree.Leaf)eadjacent[i];

            if (leaf.vfull() || leaf.vempty()) return;
//            assert leaf.material != null;  // debugging disabled

            // "Diagonal EDGE in a Cell". for access centric-'shared'-edge on one axis.
            int[] centricedge = EDGE[processEdgeMask[axis][i]];//EDGE[axis*4 +(3-i)];
            int v0 = centricedge[0] ;
            int v1 = centricedge[1] ;

            if (leaf.size < minsz) {  // there might have diff size, diff sign-change cells/edges. just listen to smallest one.
                minsz = leaf.size; min_i=i;

                flip = leaf.sign(v1);
                minsz_signchanged = leaf.sign(v0) != leaf.sign(v1);
            }
        }
        if (startRootDoFace) {
            LOGGER.info("  DO VERTEX INFO: min_sc: {}, , min_sz: {}, axis: {}, min_i: {}, min_leaf: {}", minsz_signchanged, minsz, axis, min_i, eadjacent[min_i]);
        }
        if (minsz_signchanged) {  // put the QUAD. 2tri 6vts.
            boolean finalFlip = flip;
            Octree.Leaf[] lfs = CollectionUtils.filli(new Octree.Leaf[6], i -> (Octree.Leaf)eadjacent[ADJACENT_QUADTRIV[finalFlip ? 5-i : i]]);
            for (int i = 0;i < 6;i++) {
//                int idx = ADJACENT_QUADTRIV[flip ? 5-i : i];
                Octree.Leaf lf = lfs[i];//(Octree.Leaf)eadjacent[idx];
                // if eadjacent is a big one leaf (hold 2 place(of 4)) with 2 littler leaf,
                // then should just produce one triangle instead of 2 (the another 'triangle' will been a 'line')
                if (i==0 || i==3) {
                    boolean dup = false;
                    if (lfs[i] == lfs[i+1]) dup=true;
                    else if (lfs[i] == lfs[i+2]) dup=true;
                    else if (lfs[i+1] == lfs[i+2]) dup=true;
                    if (dup) {
//                        i+=2;
//                        continue;
                    }

                    LOGGER.info(StringUtils.repeat(" ", space)+"processEdgeVertex()");
                }
                // todo: may 1 duplicated triangle: when 2 sub-size vs 1 big-size (used 2 places.)
                lf.computefp();
                vbuf.addpos(lf.featurepoint);
//                if (vbuf.verttags != null)  // put cell/vertex material.
//                    vbuf.verttags.add((float)Material.REGISTRY.indexOf(lf.material.getRegistryID()));
                if (i==2 || i==5) {
                    int si = vbuf.positions.size()-9;
                    Vector3f v1=vec3(vbuf.positions,si), v2=vec3(vbuf.positions,si+3), v3=vec3(vbuf.positions,si+6);
                    try {
                        Vector3f.trinorm(v1,v2,v3, null);
                    } catch (ArithmeticException ex) {
                        Val v = Val.zero();
                        CollectionUtils.mostDuplicated(Arrays.asList(lfs).subList(i-2, i+1), v);
                        LOGGER.info("MX Dup: "+ v);
//                        ex.printStackTrace();
//                        System.err.println(i+"; flip: "+flip+" "+Arrays.toString(eadjacent));
//                        Log.LOGGER.info(i+" : "+ex.getMessage());
//                        GuiVert3D.addTri("v1", vec3(vbuf.positions, si).add(ChunkMeshGen.rsp), vec3(vbuf.positions, si+3).add(ChunkMeshGen.rsp), vec3(vbuf.positions, si+6).add(ChunkMeshGen.rsp), Colors.BLUE);
                    }
                }
            }
        }
    }
}
