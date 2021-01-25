package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.util.IOUtils;
import outskirts.util.Identifier;
import outskirts.util.Side;

import java.io.File;
import java.io.IOException;

import static outskirts.util.logging.Log.LOGGER;

public final class Blocks {

//    public static final BlockStone STONE = register(new BlockStone());
//    public static final BlockDirt DIRT = register(new BlockDirt());
//    public static final BlockGrass GRASS = register(new BlockGrass());
//    public static final BlockLeaf LEAF = register(new BlockLeaf());
//    public static final BlockSand SAND = register(new BlockSand());
//    public static final BlockFluidWater WATER = register(new BlockFluidWater());
//    public static final BlockTallGrass TALL_GRASS = register(new BlockTallGrass());
//
//
//
//    private static <T extends Block> T register(T block) {
//        return (T)Block.REGISTRY.register(block);
//    }
//
//    static void init() {
//
//        if (Side.CURRENT.isClient()) {
//            for (String id : Block.REGISTRY.keys()) {
//                Block.REGISTRY.get(id).theTxFrag =
//                        Block.TEXTURE_ATLAS.register(
//                                Loader.loadPNG(new Identifier("materials/mc/"+new Identifier(id).getPath()+".png").getInputStream()));
//            }
//            Block.TEXTURE_ATLAS.buildAtlas();
//
//            try {
//                IOUtils.write(Loader.savePNG(Texture.glfGetTexImage(Block.TEXTURE_ATLAS.getAtlasTexture())), new File("blxatlas.png"));
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
}
