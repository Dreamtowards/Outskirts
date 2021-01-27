package outskirts.init;

import com.jcraft.jorbis.Block;
import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.client.render.TextureAtlas;
import outskirts.util.IOUtils;
import outskirts.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class MaterialTextures {

    public static TextureAtlas TEXTURE_ATLAS = new TextureAtlas();

    public static void init() {


        register("materials/mc/stone.png");//mc/stone.png");
        register("materials/mc/grass.png");
        register("materials/mc/dirt.png");

        TEXTURE_ATLAS.buildAtlas();

        try {
            IOUtils.write(Loader.savePNG(Texture.glfGetTexImage(TEXTURE_ATLAS.getAtlasTexture())), new File("mtlatlas.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void register(String fn) {
        TEXTURE_ATLAS.register(Loader.loadPNG(new Identifier(fn).getInputStream()));
    }

}
