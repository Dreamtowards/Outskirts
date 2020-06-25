package outskirts.client.material;

import outskirts.client.Loader;
import outskirts.util.Colors;
import outskirts.util.vector.Vector4f;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public final class Texture {

    public static final Texture UNIT = Loader.loadTexture(gen1x1tex(Vector4f.ONE));
    public static final Texture ZERO = Loader.loadTexture(gen1x1tex(Vector4f.ZERO));

    private int textureID;
    private int width;
    private int height;

    public Texture(int textureID) {
        this.textureID = textureID;
    }

    public int textureID() {
        return textureID;
    }

    public int getWidth() {
        return width;
    }
    public Texture setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }
    public Texture setHeight(int height) {
        this.height = height;
        return this;
    }

    private void delete() {
        glDeleteTextures(textureID);
    }

    private static BufferedImage gen1x1tex(Vector4f color) {
        BufferedImage b = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        b.setRGB(0, 0, Colors.toARGB(color));
        return b;
    }
}
