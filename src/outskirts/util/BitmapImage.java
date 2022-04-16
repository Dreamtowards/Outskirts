package outskirts.util;

import org.lwjgl.BufferUtils;
import outskirts.client.Loader;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;

import java.nio.ByteBuffer;

public class BitmapImage {

    // RGBA, left->right, top->bottom.
    private ByteBuffer pixels;
    private int width;
    private int height;

    public BitmapImage(int width, int height, ByteBuffer pixels) {
        resize(width, height, pixels);
    }
    public BitmapImage(int width, int height) {
        resize(width, height);
    }

    public void resize(int width, int height, ByteBuffer pixels) {
        this.width = width;
        this.height = height;

        Validate.isTrue(pixels.capacity() == width*height*4);
        this.pixels = pixels;
    }
    public void resize(int width, int height) {
        resize(width, height, BufferUtils.createByteBuffer(width*height*4));  // copy old pixels?
    }

    private int pxbase(int x, int y) {
        return y*width*4 + x*4;
    }
    public void setPixel(int x, int y, int rgba) {
        int bas = pxbase(x,y);
        pixels.put(bas, (byte)((rgba >> 24) & 0xFF));
        pixels.put(bas+1, (byte)((rgba >> 16) & 0xFF));
        pixels.put(bas+2, (byte)((rgba >> 8) & 0xFF));
        pixels.put(bas+3, (byte)(rgba & 0xFF));
    }
    public int getPixel(int x, int y) {
        int bas = pxbase(x,y);
        return ((pixels.get(bas) * 0xFF) << 24) |
               ((pixels.get(bas+1) * 0xFF) << 16) |
               ((pixels.get(bas+2) * 0xFF) << 8) |
                (pixels.get(bas+3) * 0xFF);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public ByteBuffer getPixels() {
        return pixels;
    }
    public ByteBuffer loadFlippedPixelsY() {
        return flipPixelsY(width, height, pixels);
    }
    public static ByteBuffer flipPixelsY(int width, int height, ByteBuffer src) {
        Log.info("Flipping: "+width+", "+height);
        ByteBuffer dst = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0;y < height;y++) {
            int flipped_pos_base = (height-1-y) * width * 4;
            for (int xb = 0;xb < width*4; xb++) {
                dst.put(src.get(flipped_pos_base+xb));
            }
        }
        dst.flip();
        Validate.isTrue(dst.capacity() == src.capacity());
        return dst;
    }

    public static BitmapImage ofSingleColor(int rgba) {
        return new BitmapImage(1, 1,
                Loader.loadBuffer(IOUtils.writeInt(new byte[4], 0, rgba)));
    }
    public static BitmapImage fromGL(int width, int height, ByteBuffer rgbaYFlipped) {
        return new BitmapImage(width, height, flipPixelsY(width, height, rgbaYFlipped));
    }
}
