package outskirts.world;

import outskirts.util.FileUtils;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.util.nbt.NBTUtils;
import outskirts.world.chunk.Chunk;

import java.io.*;

public class ChunkLoader {

    // ref to the world
    private World world;

    public ChunkLoader(World world) {
        this.world = world;
    }

    public Chunk loadChunk(int x, int z) {
        try {
            File file = chunkfile(x, z);
            if (!file.exists())
                return null;

            NBTTagCompound tagCompound = NBTUtils.read(new FileInputStream(file));

            Chunk chunk = new Chunk(x, z, world);
            chunk.readNBT(tagCompound);

            return chunk;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadChunk.", ex);
        }
    }

    public void saveChunk(Chunk chunk) throws IOException {
        File file = chunkfile(chunk.x, chunk.z);

        FileUtils.mkdirs(file.getParentFile());

        NBTTagCompound tagCompound = chunk.writeNBT(new NBTTagCompound());

        NBTUtils.write(tagCompound, new FileOutputStream(file));
    }

    private File chunkfile(int x, int z) {
        return new File(world.getWorldDirectory(), String.format("regions/c.%s.%s.chunk", x, z));
    }

}
