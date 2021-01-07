package outskirts.client.render.ducj;

import outskirts.client.render.VertexBuffer;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Val;
import outskirts.util.Validate;
import outskirts.util.vector.Vector3f;

import java.io.*;
import java.util.Objects;

public final class OctreeOp {

    private static int readCInt(InputStream is) throws IOException {
        return (IOUtils.readByte(is) & 0xff)|
                ((IOUtils.readByte(is) & 0xff) << 8) |
                ((IOUtils.readByte(is) & 0xff) << 16) |
                (IOUtils.readByte(is) << 24);
    }
    private static float readFloat(InputStream is) throws IOException {
        return Float.intBitsToFloat(readCInt(is));
    }





    private static int rtTreesz;
    private static int rtTreelv;

    private static InternalNode root;

    public static InternalNode readSOG(DataInputStream is) throws IOException {

        is.skip(14+1);  // header[14] and a \0

        Vector3f origin = new Vector3f(readFloat(is), readFloat(is), readFloat(is));
        float range = readFloat(is);

        int nlen = 128 - 4 * 4 - 14 - 1 ;
        is.skip(nlen);

        rtTreesz = readCInt(is);  // length

        rtTreelv = 0 ;
        int temp = 1 << rtTreesz;
        while ( temp < rtTreesz) {
            rtTreelv++ ;
            temp <<= 1 ;
        }

        System.out.printf("Dimensions: %d Depth: %d\n", rtTreesz, rtTreelv) ;

        return (InternalNode) doReadSOG( is, new Vector3f(0,0,0), rtTreesz, rtTreelv, origin, range ) ;
    }

    private static final Vector3f[] OCTREE_RELPOS = CollectionUtils.filli(new Vector3f[8], i ->
                new Vector3f(Math.signum(i & 4), Math.signum(i & 2), Math.signum(i & 1)));
    // 000, 001, 010, 011, 100, 101, 110, 111


    private static OctreeNode doReadSOG(DataInputStream is, Vector3f relpos, int treesz, int treelv, Vector3f origin, float range) throws IOException {
        switch (is.readByte()) {
            case 0: {  // InternalNode
                InternalNode node = new InternalNode();

                int halfsz = treesz/2;
                for (int i=0; i<8; i++) {
                    Vector3f childrelpos = new Vector3f(relpos).addScaled(halfsz, OCTREE_RELPOS[i]);
                    node.child[i] = doReadSOG(is, childrelpos, halfsz, treelv-1, origin, range);
                }
                return node;
            }
            case 1: {  // Empty node.
                is.readByte();
                return null;
            }
            case 2: {  // LeafNode
                byte sg = (byte)~is.readByte();
                Vector3f coord = new Vector3f(readFloat(is), readFloat(is), readFloat(is));

                for (int i=0; i<3; i++) {
                    Vector3f.set(coord, i,
                            (coord.get(i) - origin.get(i)) * (rtTreesz / range));
                }
                return new LeafNode(treelv, sg, coord);
            }
            default: throw new RuntimeException("Wrong type.");
        }
    }

    public static void buildMesh(InternalNode node, OutputStream os) {



    }

    // [[pair1_octIdx, pair2_octIdx, axis]]
    public static final int[][] OCTREE_FACEPAIR_AXIS = {{0,4,0},{1,5,0},{2,6,0},{3,7,0},{0,2,1},{1,3,1},{4,6,1},{5,7,1},{0,1,2},{2,3,2},{4,5,2},{6,7,2}} ;

    public static final int[][] OCTREE_EDGEADJACENT_AXIS = {{0,1,2,3,0},{4,5,6,7,0},{0,1,4,5,1},{2,3,6,7,1},{0,2,4,6,2},{1,3,5,7,2}};

    private static void doCellContour(OctreeNode node, VertexBuffer vbuf) {
        if (node == null) return;
        if (node.type() != 0) return;

        InternalNode inode = (InternalNode)node;

        // 8 Cell calls
        for (int i=0; i<8; i++) {
            doCellContour(inode.child[i], vbuf);
        }

        // 12 Face calls.  which 8 cubes inner-pair-face.
        OctreeNode[] fcpair = new OctreeNode[2];  // face pair.
        for (int i=0; i<12; i++) {
            fcpair[0] = inode.child[OCTREE_FACEPAIR_AXIS[i][0]];
            fcpair[1] = inode.child[OCTREE_FACEPAIR_AXIS[i][1]];

            doFaceContour(fcpair, OCTREE_FACEPAIR_AXIS[i][2], vbuf);
        }

        // 6 Edge calls  which 6 edges mid of 8 cubes.
        OctreeNode[] egadjacent = new OctreeNode[4];
        for (int i=0; i<6; i++) {
            for (int j=0; j<4; j++) {
                egadjacent[j] = inode.child[OCTREE_EDGEADJACENT_AXIS[i][j]];
            }
            doEdgeContour(egadjacent, OCTREE_EDGEADJACENT_AXIS[i][4], vbuf);
        }
    }

    private static void doFaceContour(OctreeNode[] facepair, int axis, VertexBuffer vbuf) {
        if (facepair[0] == null || facepair[1] == null)
            throw new NullPointerException();
        if (facepair[0].type() != 0 || facepair[1].type() != 0)
            return;

        OctreeNode[] infcpair = new OctreeNode[2];
        for (int i=0; i<4; i++) {
            for (int j=0; j<2; j++) {
                infcpair[j] = facepair[j].type() == 0 ?
                        ((InternalNode)facepair[j]).child[OCTREE_FACEPAIR_AXIS[axis*4+i][j]] :  // may the indexing had problem.
                        facepair[j];  // LeafNode.
            }
            doFaceContour(infcpair, axis, vbuf);
        }


    }

    private static void doEdgeContour(OctreeNode[] edgeadjacent, int axis, VertexBuffer vbuf) {

    }
}
