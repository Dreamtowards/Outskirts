package outskirts.init;

import com.jcraft.jorbis.Block;
import outskirts.client.Loader;
import outskirts.client.render.Texture;
import outskirts.client.render.TextureAtlas;
import outskirts.material.Material;
import outskirts.util.BitmapImage;
import outskirts.util.IOUtils;
import outskirts.util.Identifier;
import outskirts.util.logging.Log;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class MaterialTextures {

    public static TextureAtlas DIFFUSE_ATLAS = new TextureAtlas();
    public static TextureAtlas NORMAL_ATLAS  = new TextureAtlas();
    public static TextureAtlas DISPLACEMENT_ATLAS = new TextureAtlas();

    public static void init() {
//        BufferedImage emp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
//        DIFFUSE_ATLAS.register(emp);
//        NORMAL_ATLAS.register(emp);
//        DISPLACEMENT_ATLAS.register(emp);

        for (Material mtl : Material.REGISTRY.values()) {
            if (mtl==null) continue; // the first. Air.
            String regid = new Identifier(mtl.getRegistryID()).getPath();
            BitmapImage diff = loadpng(String.format("materials/%s/%s.png", regid, regid));
            BitmapImage norm = loadpng(String.format("materials/%s/%s_norm.png", regid, regid));
            BitmapImage disp = loadpng(String.format("materials/%s/%s_disp.png", regid, regid));
            assert diff.getWidth()==norm.getWidth()   && diff.getWidth()==disp.getWidth() &&
                   diff.getHeight()==norm.getHeight() && diff.getHeight()==disp.getHeight();
            DIFFUSE_ATLAS.register(diff);
            NORMAL_ATLAS.register(norm);
            DISPLACEMENT_ATLAS.register(disp);
        }

        DIFFUSE_ATLAS.buildAtlas();
        NORMAL_ATLAS.buildAtlas();
        DISPLACEMENT_ATLAS.buildAtlas();

//        IOUtils.write(Loader.savePNG(Texture.glfGetTexImage(DIFFUSE_ATLAS.getAtlasTexture())), new File("mtlatlas_diff.png"));
//        IOUtils.write(Loader.savePNG(Texture.glfGetTexImage(NORMAL_ATLAS.getAtlasTexture())), new File("mtlatlas_norm.png"));
//        IOUtils.write(Loader.savePNG(Texture.glfGetTexImage(DISPLACEMENT_ATLAS.getAtlasTexture())), new File("mtlatlas_disp.png"));

    }

    private static BitmapImage loadpng(String fn) {
        return Loader.loadPNG(new Identifier(fn).getInputStream());
    }

}
