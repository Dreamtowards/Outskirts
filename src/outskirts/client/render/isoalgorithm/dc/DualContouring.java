package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.VertexBuffer;
import outskirts.physics.collision.shapes.Raycastable;
import outskirts.util.Maths;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;

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

    /**
     * Reversed EDGE_ADJACENT. for getting recorrespounding slot. in EdgeContour phase.
     */
    public static final int[][] EDGE_ADJACENT_REV =
            {{3,2,1,0},{7,6,5,4},
             {5,1,4,0},{7,3,6,2},
             {6,4,2,0},{7,5,3,1}};

    /**
     * Diagonal of EDGE in a cell. indexing to EDGE.
     * use for get inter-centric-edge of 4 inter-cells on one axis.  in processEdgeVertex phase.
     */
    public static final int[] EDGE_DIAG =
            {3, 2, 1, 0,    // X
             7, 5, 6, 4,    // Y
             11,10,9, 8};   // Z


    public static VertexBuffer contouring(Octree node) {
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

    public static final int[][] FacepInterEdgeTb = {
            {1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2},
            {0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2},
            {1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}
    };
    private static void doFaceContour(Octree[] facepair, int axis, VertexBuffer vbuf) {
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
            int[][] pairside = {{ 0, 0, 1, 1 }, { 0, 1, 0, 1 }} ;  // 2type. indexing to source-pair.
            Octree[] eadjacent = new Octree[4];
            for (int i = 0;i < 4;i++) {
                int[] tb = FacepInterEdgeTb[axis*4+i];
                for (int j = 0;j < 4;j++) {
                    int pIdx = pairside[ tb[0] ][j];  // index of src-pair.
                    eadjacent[j] = facepair[pIdx].isInternal() ? ((Octree.Internal)facepair[pIdx]).child(tb[1+j]) : facepair[pIdx];
                }
                doEdgeContour(eadjacent, tb[ 5 ], vbuf);
            }
        }
    }

    private static void doEdgeContour(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        if (eadjacent[0]==null || eadjacent[1]==null || eadjacent[2]==null || eadjacent[3]==null) return;

        if (eadjacent[0].isLeaf() && eadjacent[1].isLeaf() && eadjacent[2].isLeaf() && eadjacent[3].isLeaf()) {
            processEdgeVertex(eadjacent, axis, vbuf);
        } else {
            Octree[] subadjacent = new Octree[4];
            for (int i = 0;i < 2;i++) {
                int[] subidx = EDGE_ADJACENT_REV[axis*2+i];
                for (int j = 0;j < 4;j++) {
                    subadjacent[j] = eadjacent[j].isInternal() ? ((Octree.Internal)eadjacent[j]).child(subidx[j]) : eadjacent[j];
                }
                doEdgeContour(subadjacent, axis, vbuf);
            }
        }
    }

    /**
     * @param eadjacent same size inter-cells. 4 on one same parent.  ??
     */
    private static void processEdgeVertex(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        float minsz = Float.MAX_VALUE;  // min depth means smallest one.?
        int minidx = -1;
        boolean[] signchanged = new boolean[4];
        boolean flip = false;

        for (int i = 0;i < 4;i++) {
            Octree.Leaf leaf = (Octree.Leaf)eadjacent[i];

            int edge = EDGE_DIAG[axis*4 + i];
            int v1 = EDGE[ edge ][0] ;
            int v2 = EDGE[ edge ][1] ;

            if (leaf.size < minsz) {  // have different depth in there together.?
                minsz = leaf.size;
                minidx = i;

                flip = leaf.solid(v1); // ?? is this sign right.?
            }
            assert leaf.size == ((Octree.Leaf)eadjacent[0]).size : "Not Same Size.?";

            signchanged[i] = leaf.solid(v1) != leaf.solid(v2);
//            assert signchanged[i] == signchanged[0] : "Diff Sign?!";
        }

        if (signchanged[minidx]) {
            vbuf.addpos(((Octree.Leaf)eadjacent[0]).featurepoint);
            vbuf.addpos(((Octree.Leaf)eadjacent[flip ? 3 : 1]).featurepoint);
            vbuf.addpos(((Octree.Leaf)eadjacent[flip ? 1 : 3]).featurepoint);

            vbuf.addpos(((Octree.Leaf)eadjacent[0]).featurepoint);
            vbuf.addpos(((Octree.Leaf)eadjacent[flip ? 2 : 3]).featurepoint);
            vbuf.addpos(((Octree.Leaf)eadjacent[flip ? 3 : 2]).featurepoint);
        }
    }




    public static void main(String[] args) throws IOException {

        Octree node = Octree.readOctree(new FileInputStream("conv.octree"), new Vector3f(0, 0, 0), 16);

        VertexBuffer vbuf = DualContouring.contouring(node);

        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("conv.obj");
    }



}
