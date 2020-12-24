package outskirts.world.gen;

import outskirts.block.Block;
import outskirts.block.BlockDirt;
import outskirts.block.BlockGrass;
import outskirts.block.BlockStone;
import outskirts.init.Blocks;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;
import outskirts.world.gen.feature.WorldGen;
import outskirts.world.gen.feature.WorldGenTallGrass;
import outskirts.world.gen.feature.WorldGenTree;

import java.util.Random;

public class ChunkGenerator {

    private Random rand = new Random();

    private NoiseGeneratorPerlin noisegen = new NoiseGeneratorPerlin();

    public Chunk generate(ChunkPos chunkpos, World world) {
        Chunk chunk = new Chunk(world, chunkpos.x, chunkpos.z);
        GenerationInfo gspec = new GenerationInfo();


        for (int x = chunkpos.x;x < chunkpos.x+16;x++) {
            for (int z = chunkpos.z; z < chunkpos.z + 16; z++) {
                float f = noisegen.fbm(x / 228f, z / 228f, 12);
                float ytop = 14 + f*32f;

                for (int i = 0;i < 50;i++) {
                    float f3 = noisegen.noise(x/32f, i/32f, z/32f);

                    Block b = i < ytop-4 ? new BlockStone() : i<ytop-2 ? new BlockDirt() : new BlockGrass();
                    b.v = (ytop-i) / 40;
                    if (f3 < -0.3f) {
                        b.v = f3;
                    }
                    chunk.setBlock(Maths.mod(x, 16), i, Maths.mod(z, 16), b);
                }
            }
        }


        chunk.markedRebuildModel=true;
        return chunk;
    }

    public void populate(World world, ChunkPos chunkpos) {
        for (int x = chunkpos.x; x < chunkpos.x+16;x++) {
            for (int z = chunkpos.z; z < chunkpos.z+16;z++) {
                int topY = world.getHighestBlock(x, z);

                if (NoiseGenerator.hash(x * z * 238429480) > 0.984f) {
                    new WorldGenTree().generate(world, new Vector3f(x, topY, z));
                }

                if (NoiseGenerator.hash(x * z * 234892374) > 0.194f) {
                    new WorldGenTallGrass().generate(world, new Vector3f(x, topY, z));
                }
            }
        }
    }

    public static class GenerationInfo {


        public int seaLevel = 7;
    }

}
