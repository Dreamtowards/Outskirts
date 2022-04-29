package outskirts.block;

import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkMeshGenNaive;
import outskirts.init.BlockTextures;
import outskirts.util.vector.Vector3f;

public class BlockStone extends Block {

    @Override
    public void getVertexData(VertexBuffer vbuf, Vector3f chunkpos, int rx, int ry, int rz) {

        ChunkMeshGenNaive.putCubeFaces(vbuf, rx, ry, rz, chunkpos, BlockTextures.STONE);
    }
}