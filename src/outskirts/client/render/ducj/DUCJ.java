package outskirts.client.render.ducj;

import java.io.*;

public class DUCJ {

    public static void main(String[] args) throws IOException {

        InternalNode rootnode = OctreeOp.readSOG(new DataInputStream(new FileInputStream("mechanic.sog")));

        System.out.printf("Done reading.\n") ;

        OctreeOp.buildMesh(rootnode, new FileOutputStream("out.ply"));

    }

}
