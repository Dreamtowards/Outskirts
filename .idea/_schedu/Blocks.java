package outskirts.init;

import outskirts.block.Block;
import outskirts.block.BlockBrick;
import outskirts.block.BlockDirt;

public final class Blocks {

    public static final BlockDirt DIRT = registerBlock(new BlockDirt());

    public static final BlockBrick BRICK = registerBlock(new BlockBrick());



    @SuppressWarnings("unchecked")
    private static <R extends Block> R registerBlock(Block block) {
        return (R) Block.REGISTRY.register(block);
    }

    static void init() {}
}
