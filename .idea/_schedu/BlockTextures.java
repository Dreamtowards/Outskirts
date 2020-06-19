package outskirts.init;

import outskirts.client.material.TextureAtlas;
import outskirts.util.ResourceLocation;
import outskirts.util.Side;
import outskirts.util.SideOnly;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public final class BlockTextures {

    public static final TextureAtlas TEXTURE_ATLAS = new TextureAtlas();

    public static final TextureAtlas.Fragment DIRT = register("textures/blocks/dirt.png");

    public static final TextureAtlas.Fragment BRICK = register("textures/blocks/brick.png");



    static void initAndBuild() {
        // before this been call, all static fields already had been initialized by order.
        TEXTURE_ATLAS.buildAtlas();
    }

    private static TextureAtlas.Fragment register(String resourceID) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ResourceLocation(resourceID).getInputStream());
            return TEXTURE_ATLAS.register(bufferedImage);
        } catch (IOException ex) {
            throw new RuntimeException("failed to init BlockTextures res: " + resourceID);
        }
    }

}
