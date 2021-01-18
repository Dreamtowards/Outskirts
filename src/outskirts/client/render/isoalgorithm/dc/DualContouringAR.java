package outskirts.client.render.isoalgorithm.dc;

import outskirts.client.render.VertexBuffer;
import outskirts.util.vector.Vector3f;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static outskirts.client.render.isoalgorithm.dc.Octree.EDGE;

/**
 *  Adaptive Resolution of Varying Octree.
 *
 */
public final class DualContouringAR {


    public static final int[][] EDGEADJACENT =
            {{0,1,2,3},{4,5,6,7},  // X
             {0,4,1,5},{2,6,3,7},  // Y
             {0,2,4,6},{1,3,5,7}}; // Z

    public static VertexBuffer contouring(Octree node) {
        VertexBuffer vbuf = new VertexBuffer();
        doCellContour(node, vbuf);
        return vbuf;
    }

    private static void doCellContour(Octree node, VertexBuffer vbuf) {
        if (node == null) return;
        if (node.isInternal()) {
            Octree.InternalNode internal = (Octree.InternalNode)node;

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
                eadjacent[0] = internal.child(EDGEADJACENT[i][0]);
                eadjacent[1] = internal.child(EDGEADJACENT[i][1]);
                eadjacent[2] = internal.child(EDGEADJACENT[i][2]);
                eadjacent[3] = internal.child(EDGEADJACENT[i][3]);
                doEdgeContour(eadjacent, i/2, vbuf);
            }
        }
    }

    public static final int[][][] faceProcFaceMask = {
            {{4,0,0},{5,1,0},{6,2,0},{7,3,0}},
            {{2,0,1},{6,4,1},{3,1,1},{7,5,1}},
            {{1,0,2},{3,2,2},{5,4,2},{7,6,2}}
    } ;
    public static final int[][][] faceProcEdgeMask = {
            {{1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2}},
            {{0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2}},
            {{1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}}
    };
    private static void doFaceContour(Octree[] facepair, int axis, VertexBuffer vbuf) {
        if (facepair[0]==null || facepair[1]==null) return;
        if (facepair[0].isInternal() || facepair[1].isInternal()) {

            // 4 Face calls. in one axis.
            Octree[] interpair = new Octree[2];
            for (int i = 0;i < 4;i++) {
                interpair[0] = facepair[0].isInternal() ? ((Octree.InternalNode)facepair[0]).child(faceProcFaceMask[axis][i][0]) : facepair[0];
                interpair[1] = facepair[1].isInternal() ? ((Octree.InternalNode)facepair[1]).child(faceProcFaceMask[axis][i][1]) : facepair[0];
                doFaceContour(interpair, axis, vbuf);
            }

            // 4 Edge calls.
            int[][] orders = {{ 0, 0, 1, 1 }, { 0, 1, 0, 1 }} ;
            Octree[] eadjacent = new Octree[4];
            for (int i = 0;i < 4;i++) {
                for (int j = 0;j < 4;j++) {
                    int idx = orders[ faceProcEdgeMask[axis][i][0] ][j];
                    eadjacent[j] = facepair[idx].isInternal() ? ((Octree.InternalNode)facepair[idx]).child(faceProcEdgeMask[axis][i][1+j]) : facepair[idx];
                }
                doEdgeContour(eadjacent, faceProcEdgeMask[ axis ][ i ][ 5 ], vbuf);
            }
        }
    }

    public static final int[][][] edgeProcEdgeMask = {
            {{3,2,1,0,0},{7,6,5,4,0}},
            {{5,1,4,0,1},{7,3,6,2,1}},
            {{6,4,2,0,2},{7,5,3,1,2}},
    };
    private static void doEdgeContour(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        if (eadjacent[0]==null || eadjacent[1]==null || eadjacent[2]==null || eadjacent[3]==null) return;

        if (eadjacent[0].isLeaf() && eadjacent[1].isLeaf() && eadjacent[2].isLeaf() && eadjacent[3].isLeaf()) {
            processEdgeVertex(eadjacent, axis, vbuf);
        } else {
            Octree[] interadjacent = new Octree[4];
            for (int i = 0;i < 2;i++) {
                for (int j = 0;j < 4;j++) {
                    interadjacent[j] = eadjacent[j].isInternal() ? ((Octree.InternalNode)eadjacent[j]).child(edgeProcEdgeMask[axis][i][j]) : eadjacent[j];
                }
                doEdgeContour(interadjacent, axis, vbuf);
            }
        }
    }

    public static final int[][] processEdgeMask = {{3,2,1,0},{7,5,6,4},{11,10,9,8}} ;
    private static void processEdgeVertex(Octree[] eadjacent, int axis, VertexBuffer vbuf) {
        float minDepth = Float.MAX_VALUE;  // min depth means smallest one.?
        int minI = -1;
        boolean[] signchanged = new boolean[4];
        boolean flip = false;

        for (int i = 0;i < 4;i++) { assert eadjacent[i].isLeaf();
            Octree.LeafNode leaf = (Octree.LeafNode)eadjacent[i];

            int v1 = EDGE[processEdgeMask[axis][i]][0] ;
            int v2 = EDGE[processEdgeMask[axis][i]][1] ;

            if (leaf.size < minDepth) {  // have different depth in there together.?
                minDepth = leaf.size;
                minI = i;

                flip = leaf.sign(v1) > 0;
            }

            signchanged[i] = leaf.sign(v1) != leaf.sign(v2);
        }

        if (signchanged[minI]) {
            vbuf.addpos(((Octree.LeafNode)eadjacent[0]).featurepoint);
            vbuf.addpos(((Octree.LeafNode)eadjacent[flip ? 3 : 1]).featurepoint);
            vbuf.addpos(((Octree.LeafNode)eadjacent[flip ? 1 : 3]).featurepoint);

            vbuf.addpos(((Octree.LeafNode)eadjacent[0]).featurepoint);
            vbuf.addpos(((Octree.LeafNode)eadjacent[flip ? 2 : 3]).featurepoint);
            vbuf.addpos(((Octree.LeafNode)eadjacent[flip ? 3 : 2]).featurepoint);
        }
    }




    public static void main(String[] args) throws IOException {

        Octree node = Octree.readOctree(new FileInputStream("conv.octree"), new Vector3f(0, 0, 0), 64);

        VertexBuffer vbuf = DualContouringAR.contouring(node);

        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("conv.obj");
    }

}
