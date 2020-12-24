package outskirts.world.gen.feature;

import outskirts.block.Block;
import outskirts.block.BlockGrass;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.init.Blocks;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.gen.NoiseGenerator;

import java.util.List;

public class WorldGenTallGrass extends WorldGen {


    @Override
    public boolean generate(World world, Vector3f blockpos) {

        Block b;
        Block downb;
        int r = 3;
        for (int x = -r;x <= r;x++) {
            for (int z = -r;z <= r;z++) {
                for (int y = -r;y <= r;y++) {
                    int bpX=(int)blockpos.x+x, bpY=(int)blockpos.y+y, bpZ=(int)blockpos.z+z;
                    b=world.getBlock(bpX, bpY, bpZ);
                    downb=world.getBlock(bpX, bpY-1, bpZ);
                    float f;
                    if ((f=NoiseGenerator.hash(bpX*bpY*bpZ)) > 0.95f) {
                        if (b == null || b.v <= 0) {
                            if (downb instanceof BlockGrass && downb.v > 0) {
                                Blocks.TALL_GRASS.v = 1;
                                world.setBlock(bpX, bpY, bpZ, Blocks.TALL_GRASS);
                                Log.LOGGER.info("Gen Grass "+f);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
