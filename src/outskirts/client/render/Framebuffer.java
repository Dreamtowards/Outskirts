package outskirts.client.render;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.util.CollectionUtils;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

// in package glw .?
public final class Framebuffer {

    private final int fboID;
    private int width=1; // tmp init alloc
    private int height=1;

    private Texture[] textures_color = new Texture[8]; // actually can 32.  GL_COLOR_ATTACHMENT0+/ GL_TEXTURE_2D
    private Texture texture_depth;
    private Texture texture_depthStencil;

    private int rbo_depthStencil = -1;

    private int[] textures_color_internalformat = new int[8];

    // there means, not "simply" new, its had a sort of Operations.     glwGenFramebuffer(), or glfGenFramebuffer()
    public static Framebuffer glfGenFramebuffer() {
        return new Framebuffer();
    }

    private Framebuffer() {
        this.fboID = glGenFramebuffers();
    }

    private static LinkedList<Framebuffer> stackFramebuffer = new LinkedList<>();
    private Framebuffer doBindFramebuffer() { // needs bind corresponding Renderbuffer
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
        glViewport(0, 0, width, height);
        return this;
    }
    public Framebuffer pushFramebuffer() {
        stackFramebuffer.push(this);
        doBindFramebuffer();
        return this;
    }
    public Framebuffer popFramebuffer() {
        stackFramebuffer.pop();
        if (stackFramebuffer.isEmpty()) {
            Framebuffer.bindMainFramebuffer();
        } else {
            stackFramebuffer.peek().doBindFramebuffer();
        }
        return this;
    }

    private static void bindMainFramebuffer() { // or should be isolate with this Object, as a static method bindMainFramebuffer() ..?
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Outskirts.toFramebufferCoords(Outskirts.getWidth()), Outskirts.toFramebufferCoords(Outskirts.getHeight()));
    }

    //
    //  ATTACH ATTACHMENT
    //

    public Framebuffer attachTextureColor(int i, int internalformat) {
        int format, type;
        switch (internalformat) {
            case GL_RGB:
                format=GL_RGB;type=GL_UNSIGNED_BYTE;break;
            case GL_RGBA:
                format=GL_RGBA;type=GL_UNSIGNED_BYTE;break;
            case GL_RGB16F:
            case GL_RGB32F:
                format=GL_RGB;type=GL_FLOAT;break;
            case GL_RGBA16F:
            case GL_RGBA32F:
                format=GL_RGBA;type=GL_FLOAT;break;
            default:
                throw new IllegalArgumentException("Unsupported enum");
        }
        if (type==GL_FLOAT) Loader.OP_TEX2D_nullbuffer=true;
        textures_color[i] = internalAttachTexture2D(internalformat, format, type, GL_COLOR_ATTACHMENT0+i);
        textures_color_internalformat[i]=internalformat;
        return this;
    }
    public Framebuffer attachTextureColor(int i) {
        return attachTextureColor(i, GL_RGB);
    }
    public Framebuffer attachTextureDepth() {
        texture_depth = internalAttachTexture2D(GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_FLOAT, GL_DEPTH_ATTACHMENT);
        return this;
    }
    public Framebuffer attachTextureDepthStencil() {
        texture_depthStencil = internalAttachTexture2D(GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, GL_DEPTH_STENCIL_ATTACHMENT);
        return this;
    }
    private Texture internalAttachTexture2D(int internalformat, int format, int type, int attachment) {
        Loader.OP_TEX2D_internalformat = internalformat;
        Loader.OP_TEX2D_format         = format;
        Loader.OP_TEX2D_type           = type;
        Texture texture = Loader.loadTexture(null, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.textureID(), 0);
        return texture;
    }

    public Framebuffer attachRenderbufferDepthStencil() {
        int rboID = glGenRenderbuffers();
        rbo_depthStencil = rboID;
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboID);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        return this;
    }



    public Texture colorTextures(int i) {
        return textures_color[i];
    }
    public Texture depthTexture() {
        return texture_depth;
    }

    public Framebuffer checkFramebufferStatus() {
        int statuscode = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (statuscode != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("GL Framebuffer status not complete. ("+statuscode+")");
        }
        return this;
    }

    public Framebuffer disableColorBuffer() {
        glDrawBuffer(GL_NONE); // do not render any Color Data.
        glReadBuffer(GL_NONE);
        return this;
    }

    public Framebuffer initMRT() {
        int sz = CollectionUtils.indexOf(textures_color, null);
        glDrawBuffers(Loader.loadBuffer(CollectionUtils.range(GL_COLOR_ATTACHMENT0, sz)));
        return this;
    }

    public Framebuffer resize(int width, int height) {
        this.width=width;
        this.height=height;
        // resize buffers
        for (int i = 0;i < textures_color.length;i++) {
            if (textures_color[i] != null) {
                attachTextureColor(i, textures_color_internalformat[i]);
            }
        }
        if (texture_depth != null)
            attachTextureDepth();
        if (texture_depthStencil != null)
            attachTextureDepthStencil();
        if (rbo_depthStencil != -1)
            attachRenderbufferDepthStencil();
        return this;
    }

    public Framebuffer uExec(Runnable r) {
        r.run();
        return this;
    }



    private void delete() {
        glDeleteFramebuffers(fboID);
    }
}
