package outskirts.client.render.dcja;

import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.isoalgorithm.dc.qefsv.QEFSolvDCJAM3;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.logging.Log;
import outskirts.util.mx.VertexUtil;
import outskirts.util.obj.OBJLoader;
import outskirts.util.vector.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static outskirts.client.render.dcja.OctreeNode.TYPE_INTERNAL;
import static outskirts.client.render.dcja.OctreeNode.TYPE_LEAF;

public final class OctreeOp {

    private static int readCInt(InputStream is) throws IOException {
        return (IOUtils.readByte(is) & 0xff)|
                ((IOUtils.readByte(is) & 0xff) << 8) |
                ((IOUtils.readByte(is) & 0xff) << 16) |
                (IOUtils.readByte(is) << 24);
    }
    private static short readCShort(InputStream is) throws IOException {
        return (short)((IOUtils.readByte(is) & 0xff)|
                ((IOUtils.readByte(is) & 0xff) << 8));
    }
    private static float readFloat(InputStream is) throws IOException {
        return Float.intBitsToFloat(readCInt(is));
    }





    private static InternalNode root;

    public static InternalNode readDCF(DataInputStream is) throws IOException {

        is.skip(9+1);  // header[9] and a \0

        is.skip(8);

        int rtTreeSize = readCInt(is);  // length

        int rtTreeDepth = 0 ;
        int temp = 1 << rtTreeSize;
        while ( temp < rtTreeSize) {
            rtTreeDepth++ ;
            temp <<= 1 ;
        }

        System.out.printf("Dimensions: %d Depth: %d\n", rtTreeSize, rtTreeDepth) ;

        return (InternalNode) doReadDCF( is, new Vector3f(0,0,0), rtTreeSize ) ;
    }

    private static final Vector3f[] Vert = CollectionUtils.filli(new Vector3f[8], i ->
                new Vector3f(Math.signum(i & 4), Math.signum(i & 2), Math.signum(i & 1)));
    // 000, 001, 010, 011, 100, 101, 110, 111


    private static void printdebug(String s) {
        Log.LOGGER.info(s);
    }
    private static OctreeNode doReadDCF(DataInputStream is, Vector3f relpos, int treesz) throws IOException {
        switch (readCInt(is)) {
            case 1: {  // Empty node.
                printdebug("WRITE::NULL");
                is.skip(2);
                return null;
            }
            case 0: {  // InternalNode
                printdebug("WRITE::INTERNAL");
                InternalNode node = new InternalNode();

                int halfsz = treesz/2;
                for (int i=0; i<8; i++) {
                    Vector3f childrelpos = new Vector3f(relpos).addScaled(halfsz, Vert[i]);
                    node.child[i] = doReadDCF(is, childrelpos, halfsz );
                }
                return node;
            }
            case 2: {  // LeafNode
                printdebug("WRITE::LEAF");
                short[] signs = new short[8];
                for (int i = 0;i < 8;i++) {
                    signs[i] = readCShort(is);
                }
                int sign = 0;
                for (int i = 0;i < 8;i++) {
                    if (signs[i] != 0)
                        sign |= (1 << i);
                }
                printdebug(" -VSIGN: "+Integer.toBinaryString(sign & 0xff));

                printdebug(" -EgSZ: n");
                LeafNode leaf = new LeafNode(sign, Vector3f.ZERO);
                List<Vector3f> verts = new ArrayList<>();
                List<Vector3f> norms = new ArrayList<>();
                for (int i = 0;i < 12;i++) {
                    int has = readCInt(is);
                    if (has != 0) {
                        printdebug(" -EgI: "+i);
                        float off = readFloat(is);
                        printdebug(" -Eg.t: "+off);

                        norms.add(leaf.ns[i]=new Vector3f(readFloat(is), readFloat(is), readFloat(is)));

                        int axis = i / 4;
                        int base = EDGE[i][0];
                        Vector3f vert = new Vector3f(relpos).addScaled(treesz, Vert[base]);
                        vert.addv(axis, off);
                        verts.add(vert);
                        leaf.ps[i] = vert;
                        printdebug(" -Eg.p: "+vert);
                        printdebug(" -Eg.n: "+norms.get(norms.size()-1));
                    }
                }
                leaf.min.set(relpos);
                leaf.size = treesz;
                leaf.vert.set( QEFSolvDCJAM3.wCalcQEF(verts, norms) );
                printdebug(" -Eg.FP::: : "+leaf.vert);
                if (verts.size() > 0)
                    return leaf;
                else return null;
            }
            default: throw new RuntimeException("Wrong type.");
        }
    }

    /**
     * the result octree is non pos,size. needs be reconstruct to init pos, size.
     */
    public static Octree convertToDCAR(OctreeNode src) {
        if (src == null) {
            return null;
        } else if (src.type() == TYPE_INTERNAL) {
            InternalNode intern = (InternalNode)src;
            Octree.InternalNode out = new Octree.InternalNode();
            for (int i=0;i<8;i++) {
                out.child(i, convertToDCAR(intern.child[i]));
            }
            return out;
        } else {
            LeafNode leaf = (LeafNode)src;
            Octree.LeafNode out = new Octree.LeafNode(new Vector3f(leaf.min), leaf.size);
            for (int i=0;i<12;i++) {
                if (leaf.ns[i] != null) {
                    HermiteData h = new HermiteData();
                    h.point.set(leaf.ps[i]);
                    h.norm.set(leaf.ns[i]);
                    out.edges[i] = h;
                }
            }
            for (int i=0;i<8;i++) {
                out.sign(i, leaf.sign(i));
            }
            return out;
        }
    }

    private static int tris = 0;
    public static void buildMesh(InternalNode node) {
        VertexBuffer vbuf = new VertexBuffer();

        doCellContour(node, vbuf);
        System.out.println("contour done. tris: "+tris);

        for (int i = 0;i < vbuf.positions.size()/3;i++) {
            vbuf.adduv(0,0);
            vbuf.addnorm(0,1,0);
        }
        VertexUtil.hardnorm(vbuf);

        try {
            IOUtils.write(OBJLoader.saveOBJ(vbuf), new File("dcja.obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int[][] EDGEADJACENT = {
            {0,1,2,3},{4,5,6,7},  // X
            {0,4,1,5},{2,6,3,7},  // Y
            {0,2,4,6},{1,3,5,7}}; // Z

    private static void doCellContour(OctreeNode node, VertexBuffer vbuf) {
        if (node == null) return;
        if (node.type() == TYPE_INTERNAL) {
            InternalNode internal = (InternalNode) node;

            // 8 Cell calls
            for (int i = 0; i < 8; i++) {
                doCellContour(internal.child[i], vbuf);
            }

            // 12 Face calls.  which 8 inter-cubes inner-pair-face. in 3 axies.
            OctreeNode[] facepair = new OctreeNode[2];  // face pair.
            for (int i = 0; i < 12; i++) {
                facepair[0] = internal.child[EDGE[i][0]];
                facepair[1] = internal.child[EDGE[i][1]];

                doFaceContour(facepair, i / 4, vbuf);
            }

            // 6 Edge calls  which 6 edges mid of 8 cubes.
            OctreeNode[] edgeadjacent = new OctreeNode[4];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 4; j++) {
                    edgeadjacent[j] = internal.child[EDGEADJACENT[i][j]];
                }
                doEdgeContour(edgeadjacent, i / 2, vbuf);
            }
        }
    }

    public static final int[][][] faceProcEdgeMask = {
            {{1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2}},
            {{0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2}},
            {{1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}}
    };
    public static final int[][][] faceProcFaceMask = {
            {{4,0,0},{5,1,0},{6,2,0},{7,3,0}},
            {{2,0,1},{6,4,1},{3,1,1},{7,5,1}},
            {{1,0,2},{3,2,2},{5,4,2},{7,6,2}}
    } ;
    private static void doFaceContour(OctreeNode[] facepair, int axis, VertexBuffer vbuf) {
        if (facepair[0] == null || facepair[1] == null) return;
        if (facepair[0].type() == TYPE_INTERNAL || facepair[1].type() == TYPE_INTERNAL) {

            // 4 Face calls.  in one axis.
            OctreeNode[] interFacepair = new OctreeNode[2];
            for (int i = 0; i < 4; i++) {
                // may the indexing had problem.
//                interFacepair[0] = facepair[0].type() == TYPE_INTERNAL ? ((InternalNode)facepair[0]).child[EDGE[axis*4 +i][0]] : facepair[0];
//                interFacepair[1] = facepair[1].type() == TYPE_INTERNAL ? ((InternalNode)facepair[1]).child[EDGE[axis*4 +i][1]] : facepair[1];

                for ( int j = 0 ; j < 2 ; j ++ ) {
                    interFacepair[j] = facepair[j].type()==TYPE_INTERNAL ? ((InternalNode)facepair[j]).child[ faceProcFaceMask[ axis ][ i ][j] ] : facepair[j] ;
                }
                doFaceContour(interFacepair, axis, vbuf);  // axis
            }

            // 4 Edge calls.
            int[][] orders = {{ 0, 0, 1, 1 }, { 0, 1, 0, 1 }} ;
            OctreeNode[] interEdgeadjacent = new OctreeNode[4] ;
            for (int i = 0;i < 4;i++) {
                int order = faceProcEdgeMask[ axis ][ i ][ 0 ];
                for (int j = 0;j < 4;j++) {
                    interEdgeadjacent[j] =
                            facepair[orders[order][j]].type()==TYPE_INTERNAL ? ((InternalNode)facepair[orders[order][j]]).child[ faceProcEdgeMask[axis][i][1+j] ] :
                            facepair[orders[order][i]];
                }
                doEdgeContour(interEdgeadjacent, faceProcEdgeMask[ axis ][ i ][ 5 ], vbuf);
            }
        }
    }

    public static final int[][][] edgeProcEdgeMask = {
            {{3,2,1,0,0},{7,6,5,4,0}},
            {{5,1,4,0,1},{7,3,6,2,1}},
            {{6,4,2,0,2},{7,5,3,1,2}},
    };
    private static void doEdgeContour(OctreeNode[] edgeadjacent, int axis, VertexBuffer vbuf) {
        if (edgeadjacent[0]==null || edgeadjacent[1]==null || edgeadjacent[2]==null || edgeadjacent[3]==null) return;

        if (edgeadjacent[0].type() == TYPE_LEAF && edgeadjacent[1].type() == TYPE_LEAF &&
            edgeadjacent[2].type() == TYPE_LEAF && edgeadjacent[3].type() == TYPE_LEAF) {
            processEdgeVertex(edgeadjacent, axis, vbuf);
        } else {
            // 2 Edge calls.
            OctreeNode[] interEdgeadjacent = new OctreeNode[4];
            for (int i = 0;i < 2;i++) {

                for (int j = 0;j < 4;j++) {
                    interEdgeadjacent[j] = edgeadjacent[j].type()==TYPE_INTERNAL ? ((InternalNode)edgeadjacent[j]).child[edgeProcEdgeMask[axis][i][j]] : edgeadjacent[j];
                }

                doEdgeContour(interEdgeadjacent, edgeProcEdgeMask[ axis ][ i ][ 4 ], vbuf);
            }
        }
    }

    public static final int[][] EDGE = {{0,4},{1,5},{2,6},{3,7},  // X
                                        {0,2},{1,3},{4,6},{5,7},  // Y
                                        {0,1},{2,3},{4,5},{6,7}}; // Z

    public static final int[][] processEdgeMask = {{3,2,1,0},{7,5,6,4},{11,10,9,8}} ;
    private static void processEdgeVertex(OctreeNode[] edgeadjacent, int axis, VertexBuffer vbuf) {

        float minsz = Float.MAX_VALUE; // dcja: depth: bigger means big-size, smaller means less-size.
        int minI = -1;
        boolean[] signs = new boolean[4];

        boolean flip = false;
        for (int i = 0; i < 4; i++) {
            if (edgeadjacent[i].type() == TYPE_LEAF) {
                LeafNode leaf = (LeafNode)edgeadjacent[i];

//                int edgev1 = EDGE[axis*4+i][0];
//                int edgev2 = EDGE[axis*4+i][1];
                int edgeIdx = processEdgeMask[axis][i] ;
                int v1 = EDGE[edgeIdx][0] ;
                int v2 = EDGE[edgeIdx][1] ;

                if (leaf.size < minsz) {
                    minsz = leaf.size;
                    minI = i;

                    flip = leaf.sign(v1) > 0;
                }

                signs[i] = leaf.sign(v1) != leaf.sign(v2);
            }
        }
//        String dpf = String.format("#%5s |"+ StringUtils.repeat(" ", dbgLV), dbgI++);
//        System.out.println(dpf+" --TRI");
        if (signs[minI]) {//System.out.println(dpf+" --TRI!YS");
            tris+=2;
            if (!flip) {
                vbuf.addpos(((LeafNode)edgeadjacent[0]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[1]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[3]).vert);

                vbuf.addpos(((LeafNode)edgeadjacent[0]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[3]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[2]).vert);
//                PLYWriter.writeFace(fout, 3, new int[] { ind[0], ind[1], ind[3] });
//                PLYWriter.writeFace(fout, 3, new int[] { ind[0], ind[3], ind[2] });
            } else {
                vbuf.addpos(((LeafNode)edgeadjacent[0]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[3]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[1]).vert);

                vbuf.addpos(((LeafNode)edgeadjacent[0]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[2]).vert);
                vbuf.addpos(((LeafNode)edgeadjacent[3]).vert);
//                PLYWriter.writeFace(fout, 3, new int[] { ind[0], ind[3], ind[1] });
//                PLYWriter.writeFace(fout, 3, new int[] { ind[0], ind[2], ind[3] });
            }
        }

    }
}
