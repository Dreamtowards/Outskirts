package outskirts.world.gen;

import outskirts.init.Blocks;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;

public class ChunkGenerator {

    private NoiseGenerator noise = new NoiseGenerator();

    public Chunk generate(int chunkX, int chunkZ, World world) {
        Chunk chunk = new Chunk(chunkX, chunkZ, world);

        float g = 1;

        for (float x = chunkX;x < chunkX + 16;x+=g) {
            for (float y = 0;y < Chunk.CAPACITY_Y;y+=g) {
                for (float z = chunkZ;z < chunkZ + 16;z+=g) {
                    float block = (noise.octavesNoise(x/16f, z/16f, 3) * 2) + 5;

                    if (y == 0)
                        chunk.getOctree(x, y, z, 4, true).body(Blocks.BRICK.getDefaultState());
                }
            }
        }

        return chunk;
    }

}
