package outskirts.world;

import outskirts.util.FileUtils;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.util.nbt.NBTUtils;
import outskirts.world.terrain.Terrain;

import java.io.*;

public class TerrainLoader {

    private WorldServer world;

    public TerrainLoader(WorldServer world) {
        this.world = world;
    }

    public Terrain loadTerrain(int x, int z) {
        try {
            File terrfile = terrainfile(x, z);
            if (!terrfile.exists())
                return null;

            Terrain terrain = new Terrain(world, x, z);

            NBTTagCompound tagCompound = NBTUtils.read(new FileInputStream(terrfile));

            terrain.readNBT(tagCompound);

            return terrain;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load terrain.", ex);
        }
    }

    public void saveTerrain(Terrain terrain) {
        try {
            File terrfile = terrainfile(terrain.x, terrain.z);
            if (!terrfile.exists()) {
                FileUtils.mkdirs(terrfile.getParentFile());
            }

            NBTTagCompound tagCompound = terrain.writeNBT(new NBTTagCompound());

            NBTUtils.write(tagCompound, new FileOutputStream(terrfile));
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save terrain.", ex);
        }
    }

    private File terrainfile(int x, int z) {
        return new File(world.getWorldDirectory(), String.format("terrains/t.%s.%s.ter", x, z));
    }
}
