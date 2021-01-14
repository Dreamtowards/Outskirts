package outskirts.client.render.dcja;

import java.io.*;

public class DCJA {

    public static void main(String[] args) throws IOException {

        InternalNode rootnode = OctreeOp.readDCF(new DataInputStream(new FileInputStream("mechanic.dcf")));

        System.out.printf("Done reading.\n") ;

        OctreeOp.buildMesh(rootnode);

    }

}
