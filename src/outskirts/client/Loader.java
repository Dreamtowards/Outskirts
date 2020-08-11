package outskirts.client;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import outskirts.client.material.Model;
import outskirts.client.material.ex.ModelData;
import outskirts.client.material.Texture;
import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.Validate;
import outskirts.util.logging.Log;
import outskirts.util.mex.VerticesUtils;
import outskirts.util.obj.OBJLoader;
import outskirts.util.ogg.OggLoader;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public final class Loader {

    // opt params of loadTexture(). after finish the function, those opt field'll be set back to default GL_RGBA.
    public static int OP_TEX2D_internalformat;
    public static int OP_TEX2D_format;
    public static int OP_TEX2D_type;
    static {
        OP_TEX2D_SetDEF();
    }
    private static void OP_TEX2D_SetDEF() {
        OP_TEX2D_internalformat = GL_RGBA; // set back defaults. opt params.
        OP_TEX2D_format         = GL_RGBA;
        OP_TEX2D_type           = GL_UNSIGNED_BYTE;
    }

    private static List<Integer> texs = new ArrayList<>(); // GL Textures
    private static List<Integer> bufs = new ArrayList<>(); // AL Buffers


    // needs rename.
    /**
     * Load "General 3D Vertices Data" with Tangents. (positions:vec3, textureCoords:vec2, normals:vec3)
     */
    public static Model loadModelTAN(int[] indices, float[] positions, float[] textureCoords, float[] normals) {
        float[] tangents = new float[positions.length];
        Maths.computeTangentCoordinates(indices, positions, textureCoords, tangents);
        return loadModel(indices, 3,positions, 2,textureCoords, 3,normals, 3,tangents);
    }
    // vertexSize is Required.! its can't been get by calc data.length/indices.length.
    /**
     * Load VertexData to VAO
     * @param attrvsz_vdat [vertexSize:int, VAOAttributeData:float[]]
     */
    public static Model loadModel(int[] indices, Object... attrvsz_vdat) {
        Model model = Model.glfGenVertexArrays();
        model.createEBO(indices);

        for (int i = 0;i < attrvsz_vdat.length/2;i++) {
            int attrp_i = i*2;
            model.createAttribute(i, (int)attrvsz_vdat[attrp_i], (float[])attrvsz_vdat[attrp_i+1]);
        }

        return model;
    }
    public static Model loadModel(Object... attrvsz_vdat) {
        return loadModel(CollectionUtils.range(((float[])attrvsz_vdat[1]).length / (int)attrvsz_vdat[0]), attrvsz_vdat);
    }

    public static Model loadOBJ(InputStream inputStream, ModelData[] mdat, boolean toCenter) { // boolean center .?
        try {
            ModelData modeldat = OBJLoader.loadOBJ(inputStream);
            if (mdat != null)
                mdat[0] = modeldat;
            if (toCenter)
                VerticesUtils.alignAabbCenter(modeldat.positions);
            return Loader.loadModelTAN(modeldat.indices, modeldat.positions, modeldat.textureCoords, modeldat.normals);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to loadOBJ().", ex);
        }
    }
    public static Model loadOBJ(InputStream inputStream, ModelData[] mdat) {
        return loadOBJ(inputStream, mdat, false);
    }
    public static Model loadOBJ(InputStream inputStream) {
        return loadOBJ(inputStream, null);
    }

    public static int loadOGG(InputStream inputStream) {
        try {
            OggLoader.OggData oggData = OggLoader.loadOGG(inputStream);
            int bufferID = alGenBuffers(); bufs.add(bufferID);
            alBufferData(bufferID, oggData.format, oggData.data, oggData.frequency);
            return bufferID;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadOGG().", ex);
        }
    }

    public static BufferedImage loadPNG(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadPNG().", ex);
        }
    }
    public static BufferedImage loadPNG(byte[] bytes) {
        return Loader.loadPNG(new ByteArrayInputStream((bytes)));
    }
    public static void savePNG(BufferedImage bi, OutputStream outputStream) {
        try {
            ImageIO.write(bi, "PNG", outputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to loadPNG().", ex);
        }
    }
    public static byte[] savePNG(BufferedImage bi) { // TOOL METHOD
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Loader.savePNG(bi, baos);
        return baos.toByteArray();
    }
    public static byte[] savePNG(Texture texture) {  // TOOL METHOD
        return Loader.savePNG(Texture.glfGetTexImage(texture));
    }

    public static Texture loadTexture(InputStream pngInputStream) {
        return loadTexture(Loader.loadPNG(pngInputStream));
    }
    public static Texture loadTexture(byte[] pngBytes) { // TOOL METHOD
        return Loader.loadTexture(Loader.loadPNG(pngBytes));
    }

    public static Texture loadTexture(BufferedImage bufferedImage) {
        return loadTexture(null, bufferedImage);
    }
    public static Texture loadTexture(@Nullable Texture dest, BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if (dest == null) {
            dest = new Texture(glGenTextures()); texs.add(dest.textureID());
        }

        bindAndInitializeTexture(GL_TEXTURE_2D, dest.textureID());

        ByteBuffer buffer = Loader.loadTextureData(bufferedImage, true);

        if (width > dest.getWidth() || height > dest.getHeight()) {
            glTexImage2D(GL_TEXTURE_2D, 0, OP_TEX2D_internalformat, width, height, 0, OP_TEX2D_format, OP_TEX2D_type, buffer);
            dest.setWidth(width).setHeight(height);
        } else {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, OP_TEX2D_format, OP_TEX2D_type, buffer);
        }
        Loader.OP_TEX2D_SetDEF();

        glGenerateMipmap(GL_TEXTURE_2D);

        return dest;
    }

    /**
     * @param bufferedImages 6 faces. order: +X, -X, +Y, -Y, +Z, -Z.
     */
    public static Texture loadTextureCubeMap(@Nullable Texture dest, BufferedImage[] bufferedImages) {
        Validate.isTrue(bufferedImages.length == 6, "Texture CubeMap requires 6 faces.");
        int width = bufferedImages[0].getWidth();
        int height = bufferedImages[0].getHeight();

        for (BufferedImage bi : bufferedImages) {
            Validate.isTrue(bi.getWidth()==width && bi.getHeight()==height, "Texuure CubeMap faces required same size.");
        }

        if (dest == null) {
            dest = new Texture(glGenTextures()); texs.add(dest.textureID());
        }

        bindAndInitializeTexture(GL_TEXTURE_CUBE_MAP, dest.textureID());

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_REPEAT);

        for (int i = 0;i < 6;i++) {
            ByteBuffer buffer = Loader.loadTextureData(bufferedImages[i], false);

            if (width != dest.getWidth() || height != dest.getHeight()) {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                dest.setWidth(width).setHeight(height);
            } else {
                glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            }
        }

        return dest;
    }

    /**
     * TextureArray.length == srcImages.length + fromIndex
     */
    public static Texture loadTextureArray(@Nullable Texture dest, BufferedImage[] bufferedImagesArray, int fromIndex) {
        int maxWidth = 0;
        int maxHeight = 0;

        for (BufferedImage bufferedImage : bufferedImagesArray) {
            maxWidth = Math.max(maxWidth, bufferedImage.getWidth());
            maxHeight = Math.max(maxHeight, bufferedImage.getHeight());
        }

        if (dest == null) {
            dest = new Texture(glGenTextures()); texs.add(dest.textureID());
        }

        bindAndInitializeTexture(GL_TEXTURE_2D_ARRAY, dest.textureID());

        if (maxWidth > dest.getWidth() || maxHeight > dest.getHeight()) {
            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, maxWidth, maxHeight, fromIndex + bufferedImagesArray.length, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
            dest.setWidth(maxWidth).setHeight(maxHeight);
        }

        for (int i = 0;i < bufferedImagesArray.length;i++) {
            ByteBuffer pixels = Loader.loadTextureData(bufferedImagesArray[i], true);

            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, fromIndex + i, bufferedImagesArray[i].getWidth(), bufferedImagesArray[i].getHeight(), 1, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        }

        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);

        return dest;
    }

    private static void bindAndInitializeTexture(int target, int textureID) {

        glBindTexture(target, textureID);

        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST); //GL_LINEAR, GL_NEAREST, GL_NEAREST_MIPMAP_NEAREST
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0.2f);

        if (ClientSettings.ENABLE_FA) {
            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);  // set 0 f use TextureFilterAnisotropic

                float amount = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                glTexParameterf(target, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                Log.info("Unable to init Texture_Filter_Anisotropic. Device unsupported.");
            }
        }
    }

    /**
     * load to Direct-Memory. (not recommended
     */
//    public static FloatBuffer loadBuffer(float[] data) {
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
//        buffer.put(data);
//        buffer.flip();
//        return buffer;
//    }
//    public static IntBuffer loadBuffer(int[] data) {
//        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
//        buffer.put(data);
//        buffer.flip();
//        return buffer;
//    }
    public static ByteBuffer loadBuffer(byte[] data) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * @return RGBA pixels sequence.
     *         additional, when a pixel's alpha == 0, then its RGB will be load as 0 (even though its src-pixel's RGB is not 0)
     */
    static ByteBuffer loadTextureData(BufferedImage bufferedImage, boolean flipY) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int i = 0;i < height;i++) {
            int y = flipY ? height - i - 1 : i;
            for (int x = 0;x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                int alpha = ((argb >> 24) & 0xFF);
                if (alpha == 0) {
                    buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)0);
                } else {
                    buffer.put((byte)((argb >> 16) & 0xFF));  //RED
                    buffer.put((byte)((argb >>  8) & 0xFF));  //GREEN
                    buffer.put((byte)((argb      ) & 0xFF));  //BLUE
                    buffer.put((byte)  alpha              );  //ALPHA
                }
            }
        }
        buffer.flip();

        return buffer;
    }

    /**
     * load OBJ-Image from GL bytes pixels data.
     * @param rgbaGLPixels GL Y-Flipped Pixels.
     */
    public static BufferedImage loadImage(ByteBuffer rgbaGLPixels, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0;y < height;y++) {
            for (int x = 0;x < width;x++) {
                int r = rgbaGLPixels.get((y*width+x)*4)   & 0xFF;
                int g = rgbaGLPixels.get((y*width+x)*4+1) & 0xFF;
                int b = rgbaGLPixels.get((y*width+x)*4+2) & 0xFF;
                int a = rgbaGLPixels.get((y*width+x)*4+3) & 0xFF;
                bi.setRGB(x, height-1-y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return bi;
    }

    static void destroy() {
        for (int tex : texs)
            glDeleteTextures(tex);
        for (int buf : bufs)
            alDeleteBuffers(buf);
    }
}
