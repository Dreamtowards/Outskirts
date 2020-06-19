package outskirts.client.render.renderer;

import outskirts.client.GameSettings;
import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.logging.Log;
import outskirts.util.vector.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * GuiRenderer. mainly for 2D GUI rendering
 */
public class GuiRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/gui.vsh").getInputStream(),
            new ResourceLocation("shaders/gui.fsh").getInputStream()
    );

    // once a render call finished, those OP fields'll been set back to Default. todo: to OP prefix.
    public static final Matrix2f PARAM_transMatrix = new Matrix2f();
    public static final Vector4f PARAM_colorMultiply = new Vector4f(Colors.WHITE);
    public static float PARAM_roundradius = 0;

    /**
     * @param x,y,width,height sometimes xywh should be floatpoint. in highDPI screen, in 1 pixel-screen-coords can actually display/draws 1+ actually pixels (1px[coords]=1px|4px[actuallydraw]
     * @param texOffsetX,texOffsetY Window/Display Coords. 0-1 from left-top to right-bottom
     */
    public void render(Model model, Texture texture, float x, float y, float width, float height, float texOffsetX, float texOffsetY, float texScaleX, float texScaleY) {

        shader.useProgram();

        shader.setVector2f("posOffset", Maths.calculateNormalDeviceCoords(x, y, Outskirts.getWidth(), Outskirts.getHeight(), new Vector2f()));
        shader.setVector2f("posScale", width / Outskirts.getWidth(), height / Outskirts.getHeight());

        shader.setVector2f("texOffset", texOffsetX, 1f-texOffsetY-texScaleY);
        shader.setVector2f("texScale", texScaleX, texScaleY);

        shader.setVector4f("colorMultiply", PARAM_colorMultiply);
        shader.setMatrix2f("transMatrix", PARAM_transMatrix);

        shader.setFloat("renderrespect", width/height);
        shader.setFloat("roundradius", PARAM_roundradius/width);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.textureID());

        glBindVertexArray(model.vaoID());

        drawElementsOrArrays(model);

//        glBindVertexArray(0);

        PARAM_transMatrix.setIdentity();
        PARAM_colorMultiply.set(Colors.WHITE);
        PARAM_roundradius = 0;
    }

    public void render(Model model, Texture texture, float x, float y, float width, float height) {
        render(model, texture, x, y, width, height, 0f, 0f, 1f, 1f);
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }

    /**
     * this is for supports multi-level(layer) scissor
     */
    private static final LinkedList<Vector4i> scissorStack = new LinkedList<>();

    public static void pushScissor(float x, float y, float width, float height) {
        if (scissorStack.size() == 0) {
            glEnable(GL_SCISSOR_TEST);
        }
        Vector4i sc = new Vector4i(
                (int)(Outskirts.toFramebufferCoords(x)),
                (int)(Outskirts.toFramebufferCoords(Outskirts.getHeight()-y-height)),
                (int)(Outskirts.toFramebufferCoords(width)),
                (int)(Outskirts.toFramebufferCoords(height)));
        scissorStack.push(sc);
        glScissor(sc.x, sc.y, sc.z, sc.w);
    }
    public static void popScissor() {
        scissorStack.pop();
        if (scissorStack.size() == 0) {
            glDisable(GL_SCISSOR_TEST);
        } else {
            Vector4i sc = scissorStack.peek();
            glScissor(sc.x, sc.y, sc.z, sc.w);
        }
    }

    /**
     * a full square rectangle start from NDC center to right-bottom
     *
     *  1     0,5
     *  +-----+
     *  |  /  |
     *  +-----+
     *  2,3   4
     */
    public static final Model MODEL_RECT = Loader.loadModel(null, new float[] {
            2, 0,
            0, 0,
            0,-2,
            0,-2,
            2,-2,
            2, 0
    },2, new float[] {
            1, 1,
            0, 1,
            0, 0,
            0, 0,
            1, 0,
            1, 1
    },2);
}
