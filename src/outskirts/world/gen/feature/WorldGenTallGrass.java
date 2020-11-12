package outskirts.world.gen.feature;

import outskirts.block.Block;
import outskirts.client.render.VertexBuffer;
import outskirts.client.render.chunk.ChunkModelGenerator;
import outskirts.init.Blocks;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.gen.NoiseGenerator;

import java.util.List;

public class WorldGenTallGrass extends WorldGen {


    @Override
    public boolean generate(World world, Vector3f blockpos) {

        int r = 3;
        for (int x = -r;x <= r;x++) {
            for (int z = -r;z <= r;z++) {
                for (int y = -r;y <= r;y++) {
                    int bpX=(int)blockpos.x+x, bpY=(int)blockpos.y+y, bpZ=(int)blockpos.z+z;
                    if (Math.random() > 0.96f && world.getBlock(bpX, bpY, bpZ) == null && world.getBlock(bpX, bpY-1, bpZ) == Blocks.GRASS) {
                        world.setBlock(bpX, bpY, bpZ, Blocks.TALL_GRASS);
                    }
                }
            }
        }

        return true;
    }
}
