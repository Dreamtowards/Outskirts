package outskirts.client.render.chunk;

import outskirts.client.Outskirts;
import outskirts.client.render.VertexBuffer;
import outskirts.util.vector.Vector3f;
import outskirts.world.Chunk;

import java.util.List;
import java.util.Objects;

public class ChunkMeshGenNaive {

    public static VertexBuffer buildMesh(Vector3f chunkpos) {
        VertexBuffer vbuf = new VertexBuffer();

        Chunk chunk = Outskirts.getWorld().getLoadedChunk(chunkpos);

        for (int x = 0;x < 16;x++) {
            for (int y = 0;y < 16;y++) {
                for (int z = 0;z < 16;z++) {

                    if (chunk.getBlock(x, y, z) != null) {
                        putFace(vbuf.positions, x, y, z);
                    }
                }
            }
        }

        vbuf.inituvnorm();
        return vbuf;
    }

    private static void putFace(List<Float> p, int x, int y, int z) {

        // Bottom
        p.add(0f+x); p.add(0f+y); p.add(1f+z);
        p.add(0f+x); p.add(0f+y); p.add(0f+z);
        p.add(1f+x); p.add(0f+y); p.add(0f+z);
        p.add(0f+x); p.add(0f+y); p.add(1f+z);
        p.add(1f+x); p.add(0f+y); p.add(0f+z);
        p.add(1f+x); p.add(0f+y); p.add(1f+z);

        // Top
        p.add(0f+x); p.add(1f+y); p.add(1f+z);
        p.add(1f+x); p.add(1f+y); p.add(1f+z);
        p.add(1f+x); p.add(1f+y); p.add(0f+z);
        p.add(0f+x); p.add(1f+y); p.add(1f+z);
        p.add(1f+x); p.add(1f+y); p.add(0f+z);
        p.add(0f+x); p.add(1f+y); p.add(0f+z);
    }

}
