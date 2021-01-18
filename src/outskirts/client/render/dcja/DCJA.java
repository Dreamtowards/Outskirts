package outskirts.client.render.dcja;

import outskirts.client.render.isoalgorithm.dc.Octree;

import java.io.*;

public class DCJA {

    public static void main(String[] args) throws IOException {

        InternalNode rootnode = OctreeOp.readDCF(new DataInputStream(new FileInputStream("mechanic.dcf")));

        System.out.printf("Done reading.\n") ;

        Octree out = OctreeOp.convertToDCAR(rootnode);
        Octree.writeOctree(new FileOutputStream("conv.octree"), out);

        OctreeOp.buildMesh(rootnode);




    }

}
