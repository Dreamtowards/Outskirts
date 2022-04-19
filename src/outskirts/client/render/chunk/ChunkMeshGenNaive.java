package outskirts.client.render.chunk;

import outskirts.block.Block;
import outskirts.client.Outskirts;
import outskirts.client.render.TextureAtlas;
import outskirts.client.render.VertexBuffer;
import outskirts.init.BlockTextures;
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

                    Block bl = chunk.getBlock(x, y, z);
                    if (bl != null) {
                        bl.getVertexData(vbuf, chunkpos, x, y, z);
                    }
                }
            }
        }

        vbuf.inituvnorm();
        return vbuf;
    }

    public static void putCubeFaces(VertexBuffer vbuf, int x, int y, int z, Vector3f chunkpos, TextureAtlas.AtlasFragment sameface) {
        putCubeFaces(vbuf, x, y, z, chunkpos, new TextureAtlas.AtlasFragment[]{sameface,sameface,sameface,sameface,sameface,sameface});
    }

    public static void putCubeFaces(VertexBuffer vbuf, int x, int y, int z, Vector3f chunkpos, TextureAtlas.AtlasFragment[] faces) {
        Block bl = Outskirts.getWorld().getBlock(chunkpos.x+x, chunkpos.y+y, chunkpos.z+z);

        // Left
        if (Outskirts.getWorld().getBlock(chunkpos.x+x-1, chunkpos.y+y, chunkpos.z+z) == null) {
            vbuf.addpos(0f + x, 0f + y, 1f + z);
            vbuf.addpos(0f + x, 1f + y, 1f + z);
            vbuf.addpos(0f + x, 1f + y, 0f + z);
            vbuf.addpos(0f + x, 0f + y, 1f + z);
            vbuf.addpos(0f + x, 1f + y, 0f + z);
            vbuf.addpos(0f + x, 0f + y, 0f + z);

            addUV(vbuf, faces[0]);
        }

        // Right
        if (Outskirts.getWorld().getBlock(chunkpos.x+x+1, chunkpos.y+y, chunkpos.z+z) == null) {
            vbuf.addpos(1f + x, 0f + y, 0f + z);
            vbuf.addpos(1f + x, 1f + y, 0f + z);
            vbuf.addpos(1f + x, 1f + y, 1f + z);
            vbuf.addpos(1f + x, 0f + y, 0f + z);
            vbuf.addpos(1f + x, 1f + y, 1f + z);
            vbuf.addpos(1f + x, 0f + y, 1f + z);

            addUV(vbuf, faces[1]);
        }

        // Bottom
        if (Outskirts.getWorld().getBlock(chunkpos.x+x, chunkpos.y+y-1, chunkpos.z+z) == null) {
            vbuf.addpos(0f + x, 0f + y, 1f + z);
            vbuf.addpos(0f + x, 0f + y, 0f + z);
            vbuf.addpos(1f + x, 0f + y, 0f + z);
            vbuf.addpos(0f + x, 0f + y, 1f + z);
            vbuf.addpos(1f + x, 0f + y, 0f + z);
            vbuf.addpos(1f + x, 0f + y, 1f + z);

            addUV(vbuf, faces[2]);
        }

        // Top
        if (Outskirts.getWorld().getBlock(chunkpos.x+x, chunkpos.y+y+1, chunkpos.z+z) == null) {
            vbuf.addpos(0f + x, 1f + y, 1f + z);
            vbuf.addpos(1f + x, 1f + y, 1f + z);
            vbuf.addpos(1f + x, 1f + y, 0f + z);
            vbuf.addpos(0f + x, 1f + y, 1f + z);
            vbuf.addpos(1f + x, 1f + y, 0f + z);
            vbuf.addpos(0f + x, 1f + y, 0f + z);

            addUV(vbuf, faces[3]);
        }

        // Front
        if (Outskirts.getWorld().getBlock(chunkpos.x+x, chunkpos.y+y, chunkpos.z+z-1) == null) {
            vbuf.addpos(0f + x, 0f + y, 0f + z);
            vbuf.addpos(0f + x, 1f + y, 0f + z);
            vbuf.addpos(1f + x, 1f + y, 0f + z);
            vbuf.addpos(0f + x, 0f + y, 0f + z);
            vbuf.addpos(1f + x, 1f + y, 0f + z);
            vbuf.addpos(1f + x, 0f + y, 0f + z);

            addUV(vbuf, faces[4]);
        }

        // Back
        if (Outskirts.getWorld().getBlock(chunkpos.x+x, chunkpos.y+y, chunkpos.z+z+1) == null) {
            vbuf.addpos(1f + x, 0f + y, 1f + z);
            vbuf.addpos(1f + x, 1f + y, 1f + z);
            vbuf.addpos(0f + x, 1f + y, 1f + z);
            vbuf.addpos(1f + x, 0f + y, 1f + z);
            vbuf.addpos(0f + x, 1f + y, 1f + z);
            vbuf.addpos(0f + x, 0f + y, 1f + z);

            addUV(vbuf, faces[5]);
        }

    }

    private static void addUV(VertexBuffer vbuf, TextureAtlas.AtlasFragment frag) {
        addUV(vbuf, frag.OFFSET.x, frag.OFFSET.y, frag.SCALE.x, frag.SCALE.y);
    }
    private static void addUV(VertexBuffer vbuf, float offX, float offY, float facX, float facY) {

        vbuf.adduv(1*facX+offX, 0*facY+offY);
        vbuf.adduv(1*facX+offX, 1*facY+offY);
        vbuf.adduv(0*facX+offX, 1*facY+offY);
        vbuf.adduv(1*facX+offX, 0*facY+offY);
        vbuf.adduv(0*facX+offX, 1*facY+offY);
        vbuf.adduv(0*facX+offX, 0*facY+offY);
    }

}
