package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.isoalgorithm.dc.DualContouring;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.Ref;
import outskirts.util.mx.VertexUtil;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;

import java.util.Arrays;

import static outskirts.util.logging.Log.LOGGER;

public class ChunkModelGen {

    public static VertexBuffer buildModel(RenderSection rs, Ref<int[]> vertm) {

        World world = Outskirts.getWorld();
        if (world==null) return null;
        Chunk chunk = world.getLoadedChunk(rs.position());
        if (chunk==null) return null;
        Octree node = chunk.octree(rs.position().y);
        if (node ==null) return null;

        VertexBuffer vbuf = DualContouring.contouring(node);

        vbuf.inituvnorm();

        VertexUtil.smoothnorm(vbuf);

//        LOGGER.info("MODEL GEN.D.");

        vertm.value = new int[vbuf.positions.size()/3];

        return vbuf;
    }

}
