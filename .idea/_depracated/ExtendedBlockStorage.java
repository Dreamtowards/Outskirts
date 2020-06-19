package outskirts.world.chunk;

import outskirts.block.state.BlockState;
import outskirts.util.vector.Vector3i;

public class ExtendedBlockStorage {

    public static final int SIZE = 16;

    private BlockState[] blocks = new BlockState[SIZE * SIZE * SIZE];

    public BlockState get(int x, int y, int z) {
        return blocks[asShort(x, y, z)];
    }

    public void set(int x, int y, int z, BlockState blockState) {
        blocks[asShort(x, y, z)] = blockState;
    }

    private static short asShort(int x, int y, int z) {
        return (short) ((x & 0xF) << 8 | (y & 0xF) << 4 | z & 0xF);
    }

}
