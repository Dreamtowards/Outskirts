package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.CollectionUtils;
import outskirts.util.Ref;
import outskirts.util.mx.VertexUtil;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.util.logging.Log.LOGGER;

public class ChunkMeshGen {

    public static VertexBuffer buildModel(RenderSection rs, Ref<float[]> vertm) {

        World world = Outskirts.getWorld();
        if (world==null) return null;
        Octree node = Outskirts.getWorld().getOctree(rs.position());
        if (node==null) return null;

        VertexBuffer vbuf = DualContouring.contouring(node);

        // Stitching. FACEs.
        Octree[] fp = new Octree[2];
        for (int i = 0;i < 3;i++) {
            // we choose stitching towards min-er. cuz miner are always exists. chunk-load is from min to max in each axis.
            Octree neib = world.getOctree(vec3(rs.position()).addv(i, 16));
            if (neib != null) {
                int beginv = vbuf.positions.size()/3;
                fp[0] = node;
                fp[1] = neib;
                DualContouring.doFaceContour(fp, i, vbuf);

                // vertex-axis-pos correct offset. because Octrees are all rel-pos.
                for (int vi = beginv;vi < vbuf.positions.size()/3;vi++) {
                    float f = vbuf.positions.get(vi*3+i);
                    if (f < 8) {
                        vbuf.positions.set(vi*3+i, 16+f);
                    }
                }
            }
        }

        // Stitching ADJACENTs.
        Octree[] ea = new Octree[4];
        for (int i = 0;i < 3;i++) {
            boolean comple = true;
            int[] adja = DualContouring.EDGE_ADJACENT[i*2];
            for (int j = 0;j < 4;j++) {
                Octree neib = world.getOctree(vec3(rs.position()).addScaled(16, Octree.VERT[adja[j]]));
                if (neib == null) {
                    comple = false;
                    break;
                }
                ea[j] = neib;
            }

            if (comple) {
                int beginv = vbuf.positions.size()/3;
                DualContouring.doEdgeContour(ea, i, vbuf);

                for (int vi = beginv;vi < vbuf.positions.size()/3;vi++) {
                    for (int j = 0;j < 3;j++) {
                        if (j == i) continue;
                        float f = vbuf.positions.get(vi*3+j);
                        if (f < 8) {
                            vbuf.positions.set(vi*3+j, 16+f);
                        }
                    }
                }
            }
        }



        vbuf.inituvnorm();

        VertexUtil.smoothnorm(vbuf);

        vertm.value = CollectionUtils.toArrayf((List<Float>)(Object)vbuf.verttags);
        assert vertm.value.length == vbuf.positions.size()/3;

        return vbuf;
    }

}
