package outskirts.client.render;

import org.lwjgl.BufferUtils;
import outskirts.client.Loader;
import outskirts.util.BitmapImage;
import outskirts.util.Colors;
import outskirts.util.vector.Vector4f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public final class Texture {

    public static final Texture UNIT = Loader.loadTexture(BitmapImage.ofSingleColor(Colors.toRGBA(Vector4f.ONE)));
    public static final Texture ZERO = Loader.loadTexture(BitmapImage.ofSingleColor(Colors.toRGBA(Vector4f.ZERO)));

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

    public static BitmapImage glfGetTexImage(Texture tex) {
        ByteBuffer pixels = BufferUtils.createByteBuffer(tex.getWidth() * tex.getHeight() * 4);
        glBindTexture(GL_TEXTURE_2D, tex.textureID());
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        return BitmapImage.fromGL(tex.getWidth(), tex.getHeight(), pixels);
    }

}
