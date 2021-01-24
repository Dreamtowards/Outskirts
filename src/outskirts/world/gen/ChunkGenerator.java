package outskirts.world.gen;

import outskirts.client.render.isoalgorithm.dc.DCOctreeSampler;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.util.function.TrifFunc;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;

import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;

public class ChunkGenerator {

    private NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin();

    public Chunk generate(ChunkPos chunkpos, World world) {
        Chunk chunk = new Chunk(world, chunkpos.x, chunkpos.z);
        GenerationInfo gspec = new GenerationInfo();

        TrifFunc FUNC = (x,y,z) -> {
//            float b = -DistFunctions.box(vec3(x-8,y-8,z-8), vec3(7.9f,5,7.9f));
//            if (b > 0)
//                return b;

            return noise.fbm((chunkpos.x+x)/20,(chunkpos.z+z)/20, 5)*9f+(5-y);
//            return -DistFunctions.sphere(vec3(x,y,z), 16f);
        };
        Octree node = DCOctreeSampler.fromSDF(vec3(0), 16, FUNC, 5);

        chunk.octree(0, node);

        return chunk;
    }

    public void populate(World world, ChunkPos chunkpos) {
        for (int x = chunkpos.x; x < chunkpos.x+16;x++) {
            for (int z = chunkpos.z; z < chunkpos.z+16;z++) {
//                int topY = world.getHighestBlock(x, z);
//
//                if (NoiseGenerator.hash(x * z * 238429480) > 0.984f) {
//                    new WorldGenTree().generate(world, new Vector3f(x, topY, z));
//                }
//
//                if (NoiseGenerator.hash(x * z * 234892374) > 0.194f) {
//                    new WorldGenTallGrass().generate(world, new Vector3f(x, topY, z));
//                }
            }
        }
    }

    public static class GenerationInfo {


        public int seaLevel = 7;
    }

}
