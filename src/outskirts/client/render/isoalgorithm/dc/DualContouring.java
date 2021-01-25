package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.VertexBuffer;
import outskirts.material.Material;
import outskirts.physics.collision.shapes.Raycastable;
import outskirts.util.Maths;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.Objects;

import static outskirts.client.render.isoalgorithm.dc.Octree.EDGE;
import static outskirts.client.render.isoalgorithm.dc.Octree.VERT;

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
                eadjacent[0] = internal.child(EDGE_ADJACENT[i][0]);
                eadjacent[1] = internal.child(EDGE_ADJACENT[i][1]);
                eadjacent[2] = internal.child(EDGE_ADJACENT[i][2]);
                eadjacent[3] = internal.child(EDGE_ADJACENT[i][3]);
                doEdgeContour(eadjacent, i/2, vbuf);
            }
        }
    }

    public static final int[][] axisFace4PrepEdgeTb = {
            {1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2},
            {0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2},
            {1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}
    };
    public static void doFaceContour(Octree[] facepair, int axis, VertexBuffer vbuf) {
        if (facepair[0]==null || facepair[1]==null) return;
        if (facepair[0].isInternal() || facepair[1].isInternal()) {

            // 4 Face calls. 4 subfaces in one axis.
            Octree[] interpair = new Octree[2];
            for (int i = 0;i < 4;i++) {
                int[] subpairvi = EDGE[axis*4+i]; // filpped to use
                interpair[0] = facepair[0].isInternal() ? ((Octree.Internal)facepair[0]).child(subpairvi[1]) : facepair[0];
                interpair[1] = facepair[1].isInternal() ? ((Octree.Internal)facepair[1]).child(subpairvi[0]) : facepair[1];
                doFaceContour(interpair, axis, vbuf);
            }

            // 4 Edge calls. 4 interedges between 2 facepair. 2 * 2 face-prep-axes
            int[][] pairside = {{ 0, 0, 1, 1 }, { 0, 1, 0, 1 }} ;  // 2type. indexing to src-pair.
            Octree[] eadjacent = new Octree[4];
            for (int i = 0;i < 4;i++) {
                int[] tb = axisFace4PrepEdgeTb[axis*4+i];
                for (int j = 0;j < 4;j++) {
                    int pIdx = pairside[ tb[0] ][j];  // index of src-pair.
                    eadjacent[j] = facepair[pIdx].isInternal() ? ((Octree.Internal)facepair[pIdx]).child(tb[1+j]) : facepair[pIdx];
                }
                doEdgeContour(eadjacent, tb[ 5 ], vbuf);
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

    /**
     * 2 Triangles forms a Quad. indexing to 4 edge-adjacents [0,3].  CCW winding.
     * QUAD: |3  2|
     *       |1  0|
     */
    private static int[] ADJACENT_QUADTRIV = {0, 2, 3, 0, 3, 1};

    /**
     * @param eadjacent same size inter-cells. 4 on one same parent.  ??
     */
    private static void processEdgeVertex(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        float minsz = Float.MAX_VALUE;  // min depth means smallest one.?
        boolean minsz_signchanged = false;
        boolean flip = false;

        for (int i = 0;i < 4;i++) {
            Octree.Leaf leaf = (Octree.Leaf)eadjacent[i];

            // "Diagonal EDGE in a Cell". for access centric-'shared'-edge on one axis.
            int[] centricedge = EDGE[axis*4 +(3-i)];
            int v0 = centricedge[0] ;
            int v1 = centricedge[1] ;

            if (leaf.size < minsz) {  // there might have diff size, diff sign-change cells/edges. just listen to smallest one.
                minsz = leaf.size;

                flip = leaf.solid(v1); // ?? is this sign right.?

                minsz_signchanged = leaf.solid(v0) != leaf.solid(v1);
            }
        }

        if (minsz_signchanged) {  // put the QUAD. 2tri 6vts.
            for (int i = 0;i < 6;i++) {
                int idx = ADJACENT_QUADTRIV[flip ? 5-i : i];
                Octree.Leaf lf = (Octree.Leaf)eadjacent[idx];
                vbuf.addpos(lf.featurepoint);
                vbuf.verttags.add((float)Material.REGISTRY.indexOf(lf.material.getRegistryID()));
            }
        }
    }
}
