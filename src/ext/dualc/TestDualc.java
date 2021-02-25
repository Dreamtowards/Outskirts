package ext.dualc;

import org.junit.Test;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.csg.CSG;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.isoalgorithm.sdf.SDF;
import outskirts.init.Materials;
import outskirts.util.function.TrifFunc;
import outskirts.util.obj.OBJLoader;
import outskirts.util.vector.Vector3f;
import outskirts.world.gen.NoiseGeneratorPerlin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class TestDualc {

    @Test
    public void samplMesh() throws FileNotFoundException {

        Octree node = Octree.fromMESH(OBJLoader.loadOBJ(new FileInputStream("terr2.obj")), 6);


        buildAndWrite(node);
    }

    @Test
    public void samplSDF() {

//        NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin(234099);
//        float s = 1/12f;
//        TrifFunc FUNC = (x,y,z) -> {
//            float hei = noise.fbm(x/18, z/18f, 5);
//            float f = noise.fbm(x*s, y*s, z*s, 2);// -DistFunctions.boundingbox(vec3(x,y,z), vec3(3,4,3), .2f);
//            if (f < -0f) {
//                return f;
//            }
//            return (8+hei*18f) - y;
//        };
//
//        Octree node = Octree.fromSDF(vec3(0), 30, FUNC, 5, Materials.STONE);
//
//        ((Octree.Internal) node).child(0, Octree.fromSDF(vec3(0), 15, FUNC, 3, Materials.STONE));
//
//        buildAndWrite(node);
//
//        LOGGER.info("Write aabbs.");
//        Octree.dbgaabbC(node, vec3(0), 30, "aabb");
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

        Octree.Leaf lf = new Octree.Leaf(vec3(0), 1);
//        lf.sign(0, true);
//        lf.sign(1, true);
//        lf.sign(4, true);
//        lf.sign(5, true);
//
//        Vector3f stdhn = vec3(0, 1, 0);
//        lf.edges[4] = new HermiteData(vec3(0,  4, 0), stdhn);
//        lf.edges[5] = new HermiteData(vec3(0,  4, 16), stdhn);
//        lf.edges[6] = new HermiteData(vec3(16, 4, 0), stdhn);
//        lf.edges[7] = new HermiteData(vec3(16, 4, 16), stdhn);
        //[10:26:17][Outskirts.java:170][main/INFO]: {
        //    "sign": 19,
        //    "edge": [
        //        null,
        //        "HermiteData{p=[11.153389, 4.5, 15.5],n=[0.23886028, 0.8570922, 0.45644143]}",
        //        null,
        //        null,
        //        "HermiteData{p=[11.0, 4.8827744, 15.0],n=[0.15478061, 0.8152269, 0.5580752]}",
        //        "HermiteData{p=[11.0, 4.555294, 15.5],n=[0.20978887, 0.8759129, 0.43447128]}",
        //        "HermiteData{p=[11.5, 4.767741, 15.0],n=[0.2738576, 0.7745159, 0.570199]}",
        //        null,
        //        null,
        //        null,
        //        "HermiteData{p=[11.5, 4.5, 15.340908],n=[0.31965375, 0.7311851, 0.6026524]}",
        //        null
        //    ],
        //    "min": "[11.0, 4.5, 15.0]",
        //    "size": 0.5
        //}


        NoiseGeneratorPerlin n = new NoiseGeneratorPerlin(123);
        TrifFunc FUNC = (x,y,z) -> {
            return n.fbm((x),(z), 5)/2+(y-.5f);
        };
        Octree.sampleSDF(lf, FUNC);
        lf = Octree.Leaf.dbgfromjson("{\n" +
                "    \"sign\": 19,\n" +
                "    \"edge\": [\n" +
                "        null,\n" +
                "        \"HermiteData{p=[11.153389, 4.5, 15.5],n=[0.23886028, 0.8570922, 0.45644143]}\",\n" +
                "        null,\n" +
                "        null,\n" +
                "        \"HermiteData{p=[11.0, 4.8827744, 15.0],n=[0.15478061, 0.8152269, 0.5580752]}\",\n" +
                "        \"HermiteData{p=[11.0, 4.555294, 15.5],n=[0.20978887, 0.8759129, 0.43447128]}\",\n" +
                "        \"HermiteData{p=[11.5, 4.767741, 15.0],n=[0.2738576, 0.7745159, 0.570199]}\",\n" +
                "        null,\n" +
                "        null,\n" +
                "        null,\n" +
                "        \"HermiteData{p=[11.5, 4.5, 15.340908],n=[0.31965375, 0.7311851, 0.6026524]}\",\n" +
                "        null\n" +
                "    ],\n" +
                "    \"min\": \"[11.0, 4.5, 15.0]\",\n" +
                "    \"size\": 0.5\n" +
                "}\n" +
                "[10:26:37][ClientSettings.java:213][main/INFO]: Saved ClientSettings options. (options.dat)\n" +
                "\n" +
                "Process finished with exit code 0\n");

        Octree.dbgaabbC(lf, vec3(lf.min), lf.size, "test_expan/ori");


        Octree.Internal expan = CSG.expand(lf);

        Octree.dbgaabbC(expan, vec3(lf.min), lf.size, "test_expan/expan");


    }

    @Test
    public void maintest() throws IOException {


        Octree node = Octree.readOctree(new FileInputStream("conv.octree"), new Vector3f(0, 0, 0), 16);

        VertexBuffer vbuf = DualContouring.contouring(node);

        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("conv.obj");

    }

    @Test
    public void signTest() {

        Octree.Leaf lf = new Octree.Leaf(vec3(0), 1);

        for (int i = 0;i < 8;i++) {
            lf.sign(i, true);
        }
        assert lf.vfull();

        LOGGER.info(Integer.toBinaryString(lf.vsign&0xff));
        for (int i = 0;i < 8;i++) {
            lf.sign(i, false);
            LOGGER.info(i+" "+Integer.toBinaryString(lf.vsign&0xff));
        }
        assert lf.vempty();
    }

    @Test
    public void testExpand() {

        Octree.Leaf lf = new Octree.Leaf(vec3(0), 4);

        lf.sign(0, true);
        lf.sign(1, true);
        lf.sign(4, true);
        lf.sign(5, true);


    }


    public static Octree rootNode;
    @Test
    public void adaptiveTest() {

        TrifFunc FUN = (x,y,z) -> {
            return SDF.box(vec3(x,y,z).sub(8), vec3(7.5f));  // a box, margin 0.5f to size16, size15
//            return SDF.sphere(vec3(x,y,z).sub(8), 7.5f);
        };

        Octree nd = Octree.fromSDF(vec3(0), 16, FUN, 2, lf -> {
            lf.material = Materials.DIRT;
        });

        ((Octree.Internal)nd).child(0,
                Octree.fromSDF(vec3(0), 8, FUN, 0, lf -> {
                    lf.material = Materials.DIRT;
                })
        );

        rootNode = nd;  // 579 11

        VertexBuffer vbuf = DualContouring.contouring(nd);
        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("testAdaptive.obj");

        Octree.dbgaabbC(nd, vec3(0), 16, "dbg/testadaptive");

    }
}
