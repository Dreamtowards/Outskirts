package ext.dualc;

import org.junit.Test;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DCOctreeSampler;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
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

        Octree node = DCOctreeSampler.fromMESH(OBJLoader.loadOBJ(new FileInputStream("terr2.obj")), 6);


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

        Octree node = DCOctreeSampler.fromSDF(vec3(0), 30, FUNC, 5);

        ((Octree.Internal) node).child(0, DCOctreeSampler.fromSDF(vec3(0), 15, FUNC, 3));

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

        LOGGER.info(
                -18%3
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
