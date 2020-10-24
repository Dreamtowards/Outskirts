package outskirts.init;

import outskirts.block.Block;
import outskirts.block.BlockDirt;
import outskirts.block.BlockGrass;
import outskirts.block.BlockStone;

public class Blocks {

    public static final BlockDirt DIRT = register(new BlockDirt());
    public static final BlockGrass GRASS = register(new BlockGrass());
    public static final BlockStone STONE = register(new BlockStone());



    private static <T extends Block> T register(T block) {
        return (T)Block.REGISTRY.register(block);
    }

    static void init() {

    }

}
