package outskirts.world.gen;

import outskirts.block.*;
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

        for (int rx = 0;rx < 16;rx++) {
            for (int rz = 0; rz < 16; rz++) {
                int x = (int)chunkpos.x+rx;
                int z = (int)chunkpos.z+rz;
//                int fPlain = (int)(noise.fbm(x/60f, z/60f, 4)*20) + 60;
//                int fMountain = (int)(noise.fbm(x/80f, z/80f, 5)*180) + 60;

                for (int ry = 0;ry < 16;ry++) {
                    int y = (int)chunkpos.y + ry;
                    Block bl = null;

//                    boolean fill = y < fPlain;
                    float f3d = (noise.fbm(x/80f, y/120f, z/80f, 3)+1)/2f;
                    float yf = noise.noise(y/10f);
//                    yf = (yf+1f)/2f;
                    yf = yf*0.2f+1f;
                    boolean fill = (f3d * yf) > 0.5f;
//                    if (y < fMountain) {
//                        float f3d = noise.fbm(x/120f, y/220f, z/120f, 6);
//                        if (f3d > 0.2f)
//                            fill = true;
//                    }

                    if (fill)
                        bl = new BlockStone();
                    else if (y < 58)
                        bl = new BlockWater();

                    chunk.setBlock(rx, ry, rz, bl);
                }
            }
        }

        for (int rx = 0;rx < 16;rx++) {
            for (int rz = 0;rz < 16;rz++) {
                int numFill = 0;

                for (int ry = 15;ry >= 0;ry--) {
                    Block bl = chunk.getBlock(rx,ry,rz);

                    if (bl == null) {
                        numFill = -1;
                    } else if (bl instanceof BlockStone) {
                        if (numFill == -1) {  // top
                            numFill = 3;
                            int y = (int)chunkpos.y+ry;
                            bl = y < 65 ? new BlockSand() : new BlockGrass();
                            chunk.setBlock(rx,ry,rz, bl);
                        } else if (numFill > 0) {
                            numFill--;

                            chunk.setBlock(rx,ry,rz, new BlockDirt());
                        }
                    }
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
