package outskirts.client.render;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Texture;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private final int fboID;
    private final int width;
    private final int height;

    private Texture[] textures_color = new Texture[8]; // actually can 32.  GL_COLOR_ATTACHMENT0+/ GL_TEXTURE_2D
    private Texture texture_depth;

    // there means, not "simply" new, its had a sort of Operations.
    public static Framebuffer glfGenFramebuffer(int width, int height) {
        return new Framebuffer(width, height);
    }

    private Framebuffer(int width, int height) {
        this.fboID = glGenFramebuffers();
        this.width = width;
        this.height = height;
    }

    public Framebuffer bindFramebuffer() { // needs bind corresponding Renderbuffer
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        glViewport(0, 0, width, height);
        return this;
    }

    public static void bindMainFramebuffer() { // or should be isolate with this Object, as a static method bindMainFramebuffer() ..?
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, (int)Outskirts.toFramebufferCoords(Outskirts.getWidth()), (int)Outskirts.toFramebufferCoords(Outskirts.getHeight()));
    }

    //
    //  ATTACH ATTACHMENT
    //

    public Framebuffer attachTextureColor(int i) {
        textures_color[i] = internalAttachTexture2D(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, GL_COLOR_ATTACHMENT0+i);
        return this;
    }
    public Framebuffer attachTextureDepth() {
        texture_depth = internalAttachTexture2D(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_FLOAT, GL_DEPTH_ATTACHMENT);
        return this;
    }
    public Framebuffer attachTextureStencil() {
        internalAttachTexture2D(GL_STENCIL_INDEX, GL_STENCIL_INDEX, GL_UNSIGNED_BYTE, GL_STENCIL_ATTACHMENT); // todo: check type correction.
        return this;
    }
    private Texture internalAttachTexture2D(int internalformat, int format, int type, int attachment) {
        Loader.OP_TEX2D_internalformat = internalformat;
        Loader.OP_TEX2D_format         = format;
        Loader.OP_TEX2D_type           = type;
        Texture texture = Loader.loadTexture(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.textureID(), 0);
        return texture;
    }

    public Framebuffer attachRenderbufferDepthStencil() {
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
//        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboID);
        return this;
    }



    public Texture colorTextures(int i) {
        return textures_color[i];
    }
    public Texture depthTexture() {
        return texture_depth;
    }

    public Framebuffer checkFramebufferStatus() {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("GL Framebuffer Status Not Complete.");
        }
        return this;
    }

    private void delete() {
        glDeleteFramebuffers(fboID);
    }
}
