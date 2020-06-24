package outskirts.client;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import outskirts.client.material.Model;
import outskirts.client.material.ModelData;
import outskirts.client.material.Texture;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.Validate;
import outskirts.util.logging.Log;
import outskirts.util.obj.OBJFileLoader;
import outskirts.util.obj.OBJLoader;
import outskirts.util.ogg.OggLoader;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.xml.ws.Holder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

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

    private static List<Integer> vaos = new ArrayList<>(); // GL VAO
    private static List<Integer> vbos = new ArrayList<>(); // GL VBO
    private static List<Integer> texs = new ArrayList<>(); // GL Textures
    private static List<Integer> bufs = new ArrayList<>(); // AL Buffers


    /**
     * 3D Vertices Data. (positions:vec3)
     */
//    public static Model loadModel(@Nullable int[] indices, float[] positions, float[] textureCoords, float[] normals) {
//        return loadModel(indices, positions,3, textureCoords,2, normals,3);
//    }
    public static Model loadModelWithTangent(int[] indices, float[] positions, float[] textureCoords, float[] normals) {
        float[] tangents = new float[positions.length];
        Maths.computeTangentCoordinates(indices, positions, textureCoords, tangents);
        return loadModel(indices, 3,positions, 2,textureCoords, 3,normals, 3,tangents);
    }
    /**
     * Load VertexData to VAO
     * @param indices uses EBO
     * @param attrvsz_vdat [VertexSize:int, VAO-AttributeList-Datas:float[]]
     */
    public static Model loadModel(int[] indices, Object... attrvsz_vdat) {
        int vaoID = glGenVertexArrays(); vaos.add(vaoID);
        glBindVertexArray(vaoID);
        int eboID = bindElementBuffer(indices);

        int[] vbos = new int[attrvsz_vdat.length/2];
        for (int i = 0;i < vbos.length;i++) {
            int attrp_i = i*2;
            vbos[i] = storeDataToAttributeList(i, (int)attrvsz_vdat[attrp_i], (float[])attrvsz_vdat[attrp_i+1]);
        }

        return new Model(vaoID, eboID, vbos, indices.length);
    }
    public static Model loadModel(Object... attrvsz_vdat) {
        return loadModel(CollectionUtils.range(((float[])attrvsz_vdat[1]).length / (int)attrvsz_vdat[0]), attrvsz_vdat);
    }
    /**
     * Store IndicesData (Indices[]) to EBO
     */
    private static int bindElementBuffer(int[] indices) {
        int eboID = glGenBuffers(); vbos.add(eboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        return eboID;
    }
    /**
     * Store VertexData (Vertices[]) to VBO, and setup AttributeList
     */
    private static int storeDataToAttributeList(int attributeNumber, int vertexSize, float[] data) {
        int vboID = glGenBuffers(); vbos.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glVertexAttribPointer(attributeNumber, vertexSize, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributeNumber);
        return vboID;
    }

    public static Model loadOBJ(InputStream inputStream, ModelData[] mdat) {
        try {
            ModelData modeldat = OBJFileLoader.loadOBJ(inputStream);
            if (mdat != null)
                mdat[0] = modeldat;
            return Loader.loadModelWithTangent(modeldat.indices, modeldat.positions, modeldat.textureCoords, modeldat.normals);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to loadOBJ().", ex);
        }
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

    public static Texture loadTexture(InputStream inputStream) {
        try {
            return loadTexture(Loader.loadPNG(inputStream));
        } catch (Exception ex) {
            throw new RuntimeException("Failed loadTexture().", ex);
        }
    }

    public static Texture loadTexture(BufferedImage bufferedImage) {
        return loadTexture(null, bufferedImage);
    }

    public static Texture loadTexture(@Nullable Texture dest, BufferedImage bufferedImage) {
        boolean create = (dest == null);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if (create) {
            dest = new Texture(glGenTextures(), width, height); texs.add(dest.textureID());
        } else if (width > dest.getWidth() || height > dest.getHeight()) {
            throw new IllegalStateException("Failed to loadTexture(). Source-Image size(width/height) is bigger over dest-Texture's size, can't storage completely.");
        }

        bindAndInitializeTexture(GL_TEXTURE_2D, dest.textureID());

        ByteBuffer buffer = Loader.loadTextureData(bufferedImage, true);

        if (create) {
            glTexImage2D(GL_TEXTURE_2D, 0, OP_TEX2D_internalformat, width, height, 0, OP_TEX2D_format, OP_TEX2D_type, buffer);
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
        boolean create = (dest == null);
        int width = bufferedImages[0].getWidth();
        int height = bufferedImages[0].getHeight();

        for (BufferedImage bi : bufferedImages) {
            Validate.isTrue(bi.getWidth()==width && bi.getHeight()==height, "Texuure CubeMap faces required same size.");
        }

        if (create) {
            dest = new Texture(glGenTextures(), width, height); texs.add(dest.textureID());
        } else if (width != dest.getWidth() || height != dest.getHeight()) {
            throw new IllegalStateException("Failed to loadTextureCubeMap(). Different .");
        }

        bindAndInitializeTexture(GL_TEXTURE_CUBE_MAP, dest.textureID());

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_REPEAT);

        for (int i = 0;i < 6;i++) {
            ByteBuffer buffer = Loader.loadTextureData(bufferedImages[i], false);

            if (create) {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
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
        boolean create = (dest == null);
        int maxWidth = 0;
        int maxHeight = 0;

        for (BufferedImage bufferedImage : bufferedImagesArray) {
            maxWidth = Math.max(maxWidth, bufferedImage.getWidth());
            maxHeight = Math.max(maxHeight, bufferedImage.getHeight());
        }

        if (create) {
            dest = new Texture(glGenTextures(), maxWidth, maxHeight); texs.add(dest.textureID());
        } else if (maxWidth > dest.getWidth() || maxHeight > dest.getHeight()) {
            throw new IllegalStateException("Failed to loadTextureArray().  Source-Images max-size(maxWidth/maxHeight) is bigger over dest-TextureArray's size, can't storage completely.");
        }

        bindAndInitializeTexture(GL_TEXTURE_2D_ARRAY, dest.textureID());

        if (create) {
            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, maxWidth, maxHeight, fromIndex + bufferedImagesArray.length, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
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

        if (GameSettings.ENABLE_FA) {
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
                    buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)alpha);
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

    static void destroy() {
        for (int vao : vaos)
            glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            glDeleteBuffers(vbo);
        for (int tex : texs)
            glDeleteTextures(tex);
        for (int buf : bufs)
            alDeleteBuffers(buf);
    }
}
