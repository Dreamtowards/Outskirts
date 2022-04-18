package outskirts.world.gen;

import outskirts.block.Block;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.Chunk;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public class ChunkGenerator {

    private NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin();

    public Chunk generate(Vector3f chunkpos, World world) {
        Chunk chunk = new Chunk(world, chunkpos);
        // GenerationInfo gspec = new GenerationInfo();

        for (int x = 0;x < 16;x++) {
            for (int z = 0;z < 16;z++) {
                float f = noise.fbm((chunkpos.x+x)/8, (chunkpos.z+z)/20, 5);

//                if (f > 0 && f < 0.1f) {
//                    f = (f+1) / 2;
//                }
                if (chunkpos.y < 10) {
                    Log.LOGGER.warn("Noise: "+f);
                    chunk.setBlock(x, (int)(((f+1)/2)*16), z, new Block());
                }
            }
        }

        return chunk;
    }

    public void populate(World world, Vector3f chunkpos) {
//        for (int x = chunkpos.x; x < chunkpos.x+16;x++) {
//            for (int z = chunkpos.z; z < chunkpos.z+16;z++) {
//                int topY = world.getHighestBlock(x, z);
//
//                if (NoiseGenerator.hash(x * z * 238429480) > 0.984f) {
//                    new WorldGenTree().generate(world, new Vector3f(x, topY, z));
//                }
//
//                if (NoiseGenerator.hash(x * z * 234892374) > 0.194f) {
//                    new WorldGenTallGrass().generate(world, new Vector3f(x, topY, z));
//                }
//            }
//        }
    }

}
