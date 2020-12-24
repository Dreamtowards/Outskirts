package outskirts.world.gen.feature;

import outskirts.block.BlockGrass;
import outskirts.init.Blocks;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;
import outskirts.world.chunk.Chunk;
import outskirts.world.gen.MapGen;

public class WorldGenTree extends WorldGen {

    @Override
    public boolean generate(World world, Vector3f pos) {
        if (!(world.getBlock(pos.x, pos.y-1, pos.z) instanceof BlockGrass))
            return false;

        // leaves
        for (int y = 0;y < 4;y++) {
            for (int x = -2;x <= 2;x++) {
                for (int z = -2;z <= 2;z++) {
                    if (new Vector3f(x,y,z).length() <= 3)
                    world.setBlock(pos.x+x, pos.y+3+y, pos.z+z, Blocks.LEAF);
                }
            }
        }
        // trunk
        for (int i = 0;i < 5;i++) {
            world.setBlock(pos.x, pos.y+i, pos.z, Blocks.STONE);
        }

        return true;
    }
}
