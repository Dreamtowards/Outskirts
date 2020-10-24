package outskirts.world.gen;

import outskirts.block.Block;
import outskirts.init.Blocks;
import outskirts.world.chunk.Chunk;

import java.util.Random;

public class ChunkGenerator {

    private Random rand = new Random();

    private NoiseGeneratorPerlin noisegen = new NoiseGeneratorPerlin();

    public Chunk generate(int xBase, int zBase) {
        assert xBase%16==0 && zBase%16==0;

        Chunk vsection = new Chunk(xBase, zBase);

        for (int x = xBase;x < xBase+16;x++) {
            for (int z = zBase; z < zBase + 16; z++) {
                float f = noisegen.fbm(x / 32f, z / 32f, 2);

                int y = 10 + (int) (f * 16);

//                    if (v >= 0)
//                        vsection.setBlock(x, y, z, v < 1f/16f ? Blocks.GRASS : v <= 0.2f ? Blocks.DIRT : Blocks.STONE);
                for (int i = 0; i <= y; i++) {
                    vsection.setBlock(x, i, z, i == y ? Blocks.GRASS : i < y - 3 ? Blocks.STONE : Blocks.DIRT);
                }
//                    rand.setSeed(x * 13 * z);
                if (NoiseGenerator.hash(x * z) > 0.994f) {
                    vsection.setBlock(x, y, z, Blocks.DIRT);
                    vsection.setBlock(x, y+1, z, Blocks.STONE);
                    vsection.setBlock(x, y+2, z, Blocks.STONE);
                    vsection.setBlock(x, y+3, z, Blocks.STONE);
                    vsection.setBlock(x, y+4, z, Blocks.STONE);
                }
            }
        }

        vsection.markedRebuildModel=true;
        return vsection;
    }

}
