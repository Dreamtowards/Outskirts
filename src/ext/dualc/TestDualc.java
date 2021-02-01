package ext.dualc;

import org.junit.Test;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.csg.CSG;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.HermiteData;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.init.Materials;
import outskirts.util.function.TrifFunc;
import outskirts.util.obj.OBJLoader;
import outskirts.util.vector.Vector3f;
import outskirts.world.gen.NoiseGeneratorPerlin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class TestDualc {

    @Test
    public void samplMesh() throws FileNotFoundException {

        Octree node = Octree.fromMESH(OBJLoader.loadOBJ(new FileInputStream("terr2.obj")), 6);


        buildAndWrite(node);
    }

    @Test
    public void samplSDF() {

        NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin(234099);
        float s = 1/12f;
        TrifFunc FUNC = (x,y,z) -> {
            float hei = noise.fbm(x/18, z/18f, 5);
            float f = noise.fbm(x*s, y*s, z*s, 2);// -DistFunctions.boundingbox(vec3(x,y,z), vec3(3,4,3), .2f);
            if (f < -0f) {
                return f;
            }
            return (8+hei*18f) - y;
        };

        Octree node = Octree.fromSDF(vec3(0), 30, FUNC, 5, Materials.STONE);

        ((Octree.Internal) node).child(0, Octree.fromSDF(vec3(0), 15, FUNC, 3, Materials.STONE));

        buildAndWrite(node);

        LOGGER.info("Write aabbs.");
        Octree.dbgaabb3vs(node, vec3(0), 30, "aabb");
    }

    private void buildAndWrite(Octree node) {

        Octree.collapse(node);

        LOGGER.info("CONTOURING.");
        VertexBuffer vbuf = DualContouring.contouring(node);

        LOGGER.info("Write Output.");
        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("ms.obj");
    }


    @Test
    public void testVertIndex() {



//       for (int i = 0;i < 256;i++) {
//            Octree.Leaf lf = new Octree.Leaf(vec3(0), 0);
//            lf.vsign = (byte) i;
//            System.out.print(lf.validedges()+", ");
//        }

//        Octree.Leaf o1 = new Octree.Leaf(vec3(0), 0);o1.vsign =51;
//        Octree.Leaf o2 = new Octree.Leaf(vec3(0), 0);o2.vsign = (byte) 250;

//        LOGGER.info(o2);
//        for (int i = 0;i < 8;i++)
//            LOGGER.info(o2.sign(i));
//        System.exit(0);

//        LOGGER.info(
//                CSGOp.opSet(o1, o2)  // 02367  1100 1101
//        );

        Octree.Leaf lf = new Octree.Leaf(vec3(0), 16);
        lf.sign(0, true);
        lf.sign(1, true);
        lf.sign(4, true);
        lf.sign(5, true);

        Vector3f stdhn = vec3(0, 1, 0);
        lf.edges[4] = new HermiteData(vec3(0,  4, 0), stdhn);
        lf.edges[5] = new HermiteData(vec3(0,  4, 16), stdhn);
        lf.edges[6] = new HermiteData(vec3(16, 4, 0), stdhn);
        lf.edges[7] = new HermiteData(vec3(16, 4, 16), stdhn);

        // 0,1,4,5  p(y:4,16/8), n(up)
        LOGGER.info(
                CSG.expand(lf)
        );

    }

    @Test
    public void maintest() throws IOException {


        Octree node = Octree.readOctree(new FileInputStream("conv.octree"), new Vector3f(0, 0, 0), 16);

        VertexBuffer vbuf = DualContouring.contouring(node);

        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("conv.obj");

    }

}
