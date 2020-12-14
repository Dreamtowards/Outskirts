package outskirts.world.storage;

import outskirts.storage.dst.DObject;
import outskirts.storage.dst.DSTUtils;
import outskirts.util.FileUtils;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChunkLoader {

    public Chunk loadChunk(World world, ChunkPos chunkpos) {
        try {
            File chunkfile = chunkfile(chunkpos);
            if (!chunkfile.exists()) return null;

            FileInputStream fis = new FileInputStream(chunkfile);
            DObject mpChunk = DSTUtils.read(fis);fis.close();

            Chunk chunk = new Chunk(world, chunkpos.x, chunkpos.z);
            chunk.onRead(mpChunk);

            return chunk;
        } catch (IOException ex) {
            throw new RuntimeException("Failed loadChunk(). "+chunkpos, ex);
        }
    }

    public void saveChunk(Chunk chunk) {
        try {
            File chunkfile = chunkfile(ChunkPos.of(chunk));
            FileUtils.mkdirs(chunkfile.getParentFile());

            DObject mpChunk = new DObject();

            chunk.onWrite(mpChunk);

            DSTUtils.write(mpChunk, new FileOutputStream(chunkfile));
        } catch (IOException ex) {
            throw new RuntimeException("Failed saveChunk()." + ChunkPos.of(chunk), ex);
        }
    }

    private File chunkfile(ChunkPos chunkpos) {
        return new File(String.format("saves/world1/regions/r.%s.%s.rgn", chunkpos.x, chunkpos.z));
    }

}
