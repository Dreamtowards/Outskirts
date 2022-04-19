package outskirts.init;

import outskirts.client.Loader;
import outskirts.client.render.TextureAtlas;
import outskirts.util.ResourceLocation;

public final class BlockTextures {

    public static TextureAtlas ATLAS = new TextureAtlas();

    public static TextureAtlas.AtlasFragment STONE = reg("blocks/stone.png");
    public static TextureAtlas.AtlasFragment DIRT = reg("blocks/dirt.png");
    public static TextureAtlas.AtlasFragment GRASS = reg("blocks/grass_top.png");

    private static TextureAtlas.AtlasFragment reg(String s) {
        return ATLAS.register(Loader.loadPNG(new ResourceLocation(s).getInputStream()));
    }

    public static void init()
    {
        ATLAS.buildAtlas();
    }

}
