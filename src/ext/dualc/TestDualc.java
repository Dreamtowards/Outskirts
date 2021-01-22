package ext.dualc;

import org.junit.Test;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DCOctreeSampler;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.isoalgorithm.distfunc.DistFunctions;
import outskirts.util.function.TrifFunc;
import outskirts.util.obj.OBJLoader;
import outskirts.world.gen.NoiseGeneratorPerlin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static outskirts.client.render.isoalgorithm.distfunc.DistFunctions.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class TestDualc {

    @Test
    public void samplMesh() throws FileNotFoundException {

        Octree node = DCOctreeSampler.fromMESH(OBJLoader.loadOBJ(new FileInputStream("exa1.obj")), 5);


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

        buildAndWrite(node);
    }

    private void buildAndWrite(Octree node) {

        Octree.collapse(node);

        LOGGER.info("CONTOURING.");
        VertexBuffer vbuf = DualContouring.contouring(node);

//        LOGGER.info("Write aabb");
//        Octree.DBG_AABB_LEAF=true;Octree.DBG_AABB_INTERN=false;Octree.DBG_AABB_HERMIT=false;
//        Octree.dbgaabbobj(node, "aabb_l.obj", min, size);
//        Octree.DBG_AABB_LEAF=false;Octree.DBG_AABB_INTERN=true;Octree.DBG_AABB_HERMIT=false;
//        Octree.dbgaabbobj(node, "aabb_i.obj", min, size);
//        Octree.DBG_AABB_LEAF=false;Octree.DBG_AABB_INTERN=false;Octree.DBG_AABB_HERMIT=true;
//        Octree.dbgaabbobj(node, "aabb_h.obj", min, size);

        LOGGER.info("Write Output.");
        vbuf.inituvnorm();
        vbuf.tmpsaveobjfile("ms.obj");
    }

}
