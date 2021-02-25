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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static outskirts.client.render.isoalgorithm.dc.Octree.EDGE;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.logging.Log.LOGGER;

/**
 *  Adaptive Resolution of Varying Octree.
 *
 */
public final class DualContouring {


    // *math gen table.  depends on EDGE table.
    /**
     * adjacents for each axes. 2*3.  indexing VERT.
     */
    public static final int[][] EDGE_ADJACENT =
            {{0,1,2,3},{4,5,6,7},  // X
             {5,1,4,0},{7,3,6,2},  // Y
             {4,0,6,2},{5,1,7,3}}; // Z
    static {
        // Computation of EDGE_ADJACENT
        for (int i = 0;i < 6;i++) {
            for (int j = 0;j < 4;j++) {
                int v = EDGE[(i/2)*4+j][i%2];
                assert v == EDGE_ADJACENT[i][j];
            }
        }
    }

    // for. doFaceContour().  // *math gen table.  depends on EDGE table.
    public static final int[][] FACE_OTHO_ADJACENT = {
            {1,1,0,5,1,0,0,4, 1}, {1,3,0,7,1,2,0,6, 1}, {1,0,0,4,1,2,0,6, 2}, {1,1,0,5,1,3,0,7, 2},
            {0,2,0,3,1,0,1,1, 0}, {0,6,0,7,1,4,1,5, 0}, {0,6,0,2,1,4,1,0, 2}, {0,7,0,3,1,5,1,1, 2},
            {0,1,1,0,0,3,1,2, 0}, {0,5,1,4,0,7,1,6, 0}, {1,4,1,0,0,5,0,1, 1}, {1,6,1,2,0,7,0,3, 1}
    };
    static {
        // Computation of FACE_OTHO_ADJACENT.
        int ni = 0;
        for (int face_axis = 0;face_axis < 3;face_axis++) {
            for (int otho_axis = 0;otho_axis < 3;otho_axis++) {
                if (otho_axis == face_axis) continue;
                for (int otho_adjacent = 0;otho_adjacent < 2;otho_adjacent++) {  // othoaxis_adjacent_i.
                    int[] tb  = FACE_OTHO_ADJACENT[ni++]; assert tb[8] == otho_axis;
                    for (int adjacen_ei = 0;adjacen_ei < 4;adjacen_ei++) { // adjacent_element_i
                        int original_v = EDGE[otho_axis*4+adjacen_ei][otho_adjacent];
                        int should_v;
                        int sample_pair;
                        for (int j = 0;j < 4;j++) {  // search
                            int[] eg = EDGE[face_axis*4+j];
                            if (eg[0] == original_v || eg[1] == original_v) {
                                if (eg[1] == original_v) {
                                    sample_pair = 1;
                                    should_v = eg[0];
                                } else {
                                    sample_pair = 0;
                                    should_v = eg[1];
                                }
                                assert tb[adjacen_ei*2] == sample_pair && tb[adjacen_ei*2+1] == should_v;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // for. processEdgeVertex()  // *spec defined table. depends on EDGE table.
    /**
     * 2 triangles forms a quad. dependents EDGE table.  CCW winding.  normal pointing from min to max (if not flipped.)
     */
    private static final int[] ADJACENT_QUAD = {0, 2, 3, 0, 3, 1};


    public static VertexBuffer contouring(Octree node) {
        Objects.requireNonNull(node);
        VertexBuffer vbuf = new VertexBuffer();
        doCellContour(node, vbuf);
        return vbuf;
    }

    private static void doCellContour(Octree node, VertexBuffer vbuf) {
        if (node == null) return;
        if (node.isInternal()) {
            Octree.Internal internal = (Octree.Internal)node;

            // 8 Cell calls
            for (int i = 0;i < 8;i++) {
                doCellContour(internal.child(i), vbuf);
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
                for (int j = 0;j < 4;j++) {
                    eadjacent[j] = internal.child(EDGE_ADJACENT[i][j]);
                }
                doEdgeContour(eadjacent, i/2, vbuf);
            }
        }
    }

    /**
     * @param facepair order-required: [0]: min-side, [1] max-side. (relatively.)
     */
    public static void doFaceContour(Octree[] facepair, int axis, VertexBuffer vbuf) {
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
            Octree[] eadjacent = new Octree[4];
            for (int i = 0;i < 4;i++) {
                int[] tb = FACE_OTHO_ADJACENT[axis*4+i];
                for (int j = 0;j < 4;j++) {
                    int samplepair_i = tb[j*2];
                    int samplevert_i = tb[j*2+1];
                    eadjacent[j] = facepair[samplepair_i].isInternal() ? ((Octree.Internal)facepair[samplepair_i]).child(samplevert_i) : facepair[samplepair_i];
                }
                doEdgeContour(eadjacent, tb[ 8 ], vbuf);
            }
        }
    }

    public static void doEdgeContour(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
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
    }


    /*
     * Dont just use vempty(), vfull() to check is a leaf should be connect.  should use hasActuallyFeaturePoint().  else, in LOD may cause some holes.
     * LOD: because some LOD-leaf which vertex-sign sampled as vempty, may its originally contains a lots of littler leafs which should connect with 'external' leaves.
     * tho the LOD-leaf is rought-sampled as vempty, but its has LOD-fp, its may originally should connects with some 'external' leaves.
     * once the LOD-leaf been discord, the 'external' leaves which originally should beem connected with, just cant connect, cause some holes.
     */

    /**
     * @param eadjacent required leaves. required {EDGE at the axis} winding, facing either side is ok. (for vaild tris build and 'centric-edge' sample
     *                  these leafs may diff size, may 1 duplicated(the big one, same2times), may diff-parent. but must had a intersected face.
     */
    private static void processEdgeVertex(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        float minsz = Float.MAX_VALUE;
        boolean minsz_signchanged = false;
        boolean flip = false;

        for (int i = 0;i < 4;i++) {
            Octree.Leaf leaf = (Octree.Leaf)eadjacent[i];

            if (!leaf.lodObjDontUseHermiteDataFp)
                if (leaf.vfull() || leaf.vempty()) return;
            assert leaf.material != null;

            // "Diagonal EDGE in a Cell". for access centric-'shared'-edge on one axis.
            int[] centricedge = EDGE[axis*4 +(3-i)];
            int v0 = centricedge[0] ;
            int v1 = centricedge[1] ;

            if (leaf.size < minsz) {  // there might have diff size, diff sign-change cells/edges. just listen to smallest one.
                minsz = leaf.size;

                flip = leaf.sign(v1);
                minsz_signchanged = leaf.sign(v0) != leaf.sign(v1);
            }
        }
        if (minsz_signchanged) {  // put the QUAD. 2tri 6vts.
            for (int i = 0;i < 6;i++) {
                Octree.Leaf lf = (Octree.Leaf)eadjacent[ADJACENT_QUAD[flip ? 5-i : i]];
                // if eadjacent is a big one leaf (hold 2 place of 4) with 2 smaller leaf,
                // then should just produce one triangle instead of 2 (the another 'triangle' will been a 'line')
                if (i==0 || i==3) { // {0, 2, 3, 0, 3, 1};
                    if ((i==0&&!flip || i==3&&flip) ?
                            (eadjacent[0]==eadjacent[2] || eadjacent[0]==eadjacent[3] || eadjacent[2]==eadjacent[3]) :
                            (eadjacent[0]==eadjacent[3] || eadjacent[0]==eadjacent[1] || eadjacent[3]==eadjacent[1])) {
                        i+=2; continue; }
                }
                lf.computefp();
                vbuf.addpos(lf.featurepoint);
                vbuf.verttags.add((float)Material.REGISTRY.indexOf(lf.material.getRegistryID()));
            }
        }
    }
}
