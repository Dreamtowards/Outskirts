package outskirts.init;

import outskirts.block.*;

public class Blocks {

    public static final BlockDirt DIRT = register(new BlockDirt());
    public static final BlockGrass GRASS = register(new BlockGrass());
    public static final BlockStone STONE = register(new BlockStone());
    public static final BlockLeaf LEAF = register(new BlockLeaf());
    public static final BlockSand SAND = register(new BlockSand());
    public static final BlockFluidWater WATER = register(new BlockFluidWater());
    public static final BlockTallGrass TALL_GRASS = register(new BlockTallGrass());



    private static <T extends Block> T register(T block) {
        return (T)Block.REGISTRY.register(block);
    }

    static void init() {

    }

}
