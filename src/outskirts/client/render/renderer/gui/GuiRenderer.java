package outskirts.client.render.renderer.gui;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.Renderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.vector.*;

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
            new ResourceLocation("shaders/gui/gui.vsh").getInputStream(),
            new ResourceLocation("shaders/gui/gui.fsh").getInputStream()
    );

    // once a render call finished, those OP fields'll been set back to Default.
    public static final Matrix2f OP_transmat = new Matrix2f();
    public static final Vector4f OP_colormul = new Vector4f();
    public static float OP_roundradius = 0;

    /**
     * @param x,y,width,height sometimes xywh should be floatpoint. in highDPI screen, in 1 pixel-screen-coords can actually display/draws 1+ actually pixels (1px[coords]=1px|4px[actuallydraw]
     * @param texOffsetX,texOffsetY 0-1 from left-top to right-bottom
     * @param texScaleX,texScaleY 0-1 for 0%-100%
     */
    public void render(Model model, Texture texture, float x, float y, float width, float height, float texOffsetX, float texOffsetY, float texScaleX, float texScaleY) {

        shader.useProgram();

        shader.setVector2f("posOffset", Maths.calculateNormalDeviceCoords(x, y, Outskirts.getWidth(), Outskirts.getHeight(), new Vector2f()));
        shader.setVector2f("posScale", width / Outskirts.getWidth(), height / Outskirts.getHeight());

        shader.setVector2f("texOffset", texOffsetX, 1f-texOffsetY-texScaleY);
        shader.setVector2f("texScale", texScaleX, texScaleY);

        if (!OP_transmat.equals(Matrix2f.IDENTITY))
            shader.setMatrix2f("transMatrix", OP_transmat);
        if (!OP_colormul.equals(Colors.WHITE))
            shader.setVector4f("colorMultiply", OP_colormul);

        if (OP_roundradius != 0) {
            shader.setFloat("renderrespect", width / height);
            shader.setFloat("roundradius", OP_roundradius / width);
        }

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.textureID());

        glBindVertexArray(model.vaoID());

        glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);

        // set defaults.
        if (!OP_transmat.equals(Matrix2f.IDENTITY)) {
            OP_transmat.setIdentity();
            shader.setMatrix2f("transMatrix", OP_transmat);
        }
        if (!OP_colormul.equals(Colors.WHITE)) {
            OP_colormul.set(Colors.WHITE);
            shader.setVector4f("colorMultiply", OP_colormul);
        }
        if (OP_roundradius != 0) {
            OP_roundradius = 0;
            shader.setFloat("roundradius", 0);
        }
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
    private static final LinkedList<Vector4f> scissorStack = new LinkedList<>();

    public static void pushScissor(float x, float y, float width, float height) {
        if (scissorStack.size() == 0) {
            glEnable(GL_SCISSOR_TEST);
        }
        // do the UnionArea (max(a.min, b.min), min(a.max, b.max), and ensure min < max.)
        float endX = x+width, endY = y+height;
        for (Vector4f area : scissorStack) {
            x = Math.max(x, area.x);
            y = Math.max(y, area.y);
            endX = Math.min(endX, area.x+area.z);
            endY = Math.min(endY, area.y+area.w);
        }
        width = endX -x;
        height = endY - y;
        if (width < 0) x = width = 0;
        if (height < 0) y = height = 0;
        // push scissor.
        Vector4f sc = new Vector4f(x, y, width, height);
        scissorStack.push(sc);
        glfScissor(sc.x, sc.y, sc.z, sc.w);
    }
    public static void popScissor() {
        scissorStack.pop();
        if (scissorStack.size() == 0) {
            glDisable(GL_SCISSOR_TEST);
        } else {
            Vector4f sc = scissorStack.peek();
            glfScissor(sc.x, sc.y, sc.z, sc.w);
        }
    }

    private static void glfScissor(float x, float y, float width, float height) {
        glScissor(Outskirts.toFramebufferCoords(x),
                  Outskirts.toFramebufferCoords(Outskirts.getHeight()-y-height),
                  Outskirts.toFramebufferCoords(width),
                  Outskirts.toFramebufferCoords(height));
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
    public static final Model MODEL_RECT = Loader.loadModel(2,new float[] {
            2, 0,
            0, 0,
            0,-2,
            0,-2,
            2,-2,
            2, 0
    }, 2,new float[] {
            1, 1,
            0, 1,
            0, 0,
            0, 0,
            1, 0,
            1, 1
    });
}
