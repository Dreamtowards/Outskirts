package outskirts.world.gen;

import outskirts.block.Block;
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
                float f = noisegen.fbm(x / 32f, z / 32f, 7);
                float ytop = 4 + f*16f;

                for (int i = 0;i < 16;i++) {

                    Block b = Math.random() > 0.5 ? new BlockStone() : new BlockGrass();
                    b.v = (ytop-i) / 16;
                    chunk.setBlock(Maths.mod(x, 16), i, Maths.mod(z, 16), b);
                }
            }
        }

//        for (int x = chunkpos.x;x < chunkpos.x+16;x++) {
//            for (int z = chunkpos.z;z < chunkpos.z+16;z++) {
//                for (int y = 0;y < 32;y++) {
//                    float f = noisegen.noise(x/32f, y/32f, z/32f);
////                    if (f > 0) {
////                        System.out.println("SET");
//
//                        Block b = new BlockGrass();
//                        b.v = (f+1)/2f;
////                        if (b.v < 0.5f)
//                        chunk.setBlock(Maths.mod(x, 16), y, Maths.mod(z, 16), b);
////                    }
//                }
//            }
//        }

//        for (int x = chunkpos.x;x < chunkpos.x+16;x++) {
//            for (int z = chunkpos.z; z < chunkpos.z + 16; z++) {
//                float f = noisegen.fbm(x / 32f, z / 32f, 2);
//
//                int y = 10 + (int) (f * 16);
////                    if (v >= 0)
////                        vsection.setBlock(x, y, z, v < 1f/16f ? Blocks.GRASS : v <= 0.2f ? Blocks.DIRT : Blocks.STONE);
//                for (int i = 0; i <= y; i++) {
//                    Block block;
//                    if (Math.abs(y-gspec.seaLevel) <= 2 && (i-gspec.seaLevel <= 1 || i-gspec.seaLevel > -3))
//                        block = Blocks.SAND;
//                    else if (i == y) block = Blocks.GRASS;
//                    else if (i >= y-3) block = Blocks.DIRT;
//                    else block = Blocks.STONE;
//
//                    vsection.setBlock(x, i, z, block);
//                }
//                for (int i = y+1;i <= gspec.seaLevel+1;i++) {
//                    vsection.setBlock(x, i, z, Blocks.WATER);
//                }
//            }
//        }

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

                if (NoiseGenerator.hash(x * z * 234892374) > 0.94f) {

                    new WorldGenTallGrass().generate(world, new Vector3f(x, topY, z));
                }

            }
        }
    }

    public static class GenerationInfo {


        public int seaLevel = 7;
    }

}
