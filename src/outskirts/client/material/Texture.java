package outskirts.client.material;

import io.netty.buffer.ByteBuf;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.util.Colors;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

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

    public static BufferedImage glfGetTexImage(Texture tex) {
        ByteBuffer pixels = memAlloc(tex.getWidth() * tex.getHeight() * 4);
        try {
            glBindTexture(GL_TEXTURE_2D, tex.textureID());
            glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            return Loader.loadImage(pixels, tex.getWidth(), tex.getHeight());
        } finally {
            memFree(pixels);
        }
    }

}
