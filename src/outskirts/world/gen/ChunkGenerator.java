package outskirts.world.gen;

import outskirts.client.Outskirts;
import outskirts.client.render.isoalgorithm.dc.Octree;
import outskirts.client.render.isoalgorithm.sdf.SDF;
import outskirts.init.Materials;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.function.TrifFunc;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;
import outskirts.world.chunk.ChunkPos;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.aabb;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public class ChunkGenerator {

    private NoiseGeneratorPerlin noise = new NoiseGeneratorPerlin();

    public Chunk generate(ChunkPos chunkpos, World world) {
        Chunk chunk = new Chunk(world, chunkpos.x, chunkpos.z);
        GenerationInfo gspec = new GenerationInfo();


        for (int i = 0;i < 1;i++) {
            int finalI = i;
            TrifFunc FUNC = (x, y, z) -> {
                x = x+chunkpos.x;
                y = y+ finalI *16;
                z = z+chunkpos.z;

                float b = SDF.box(vec3(x,y,z).sub(8), vec3(4f,5,4f));
                if (b < 0) return b;
                return y-5;//(noise.fbm((x)/29,(z)/29, 4)*8f+8);
            };
            Octree node = Octree.fromSDF(vec3(0), 16, FUNC, 4, lf -> {
                if (FUNC.sample(lf.min) < -1.5f) lf.material = Materials.DIRT;
                else lf.material = Materials.GRASS;
            });
//            Octree.doLOD((Octree.Internal)node, 4, vec3(0), 16);
            chunk.octree(i*16, node);
        }

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
