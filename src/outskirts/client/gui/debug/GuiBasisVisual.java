package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.Colors;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GuiBasisVisual extends Gui {

    public final Matrix3f theBasis = new Matrix3f();

    private Vector3f TMP_VEC3 = new Vector3f(); // TMP_VEC3_TRANS heap cache

    {
        addOnDrawListener(e -> {
            // renderCameraAxises
            float s = 0.003f;
            float l = 0.08f;
            renderCameraAxis(TMP_VEC3.set(l, s, s), Colors.FULL_R);
            renderCameraAxis(TMP_VEC3.set(s, l, s), Colors.FULL_G);
            renderCameraAxis(TMP_VEC3.set(s, s, l), Colors.FULL_B);
        });
    }

    private static final Vector3f POS_OFF = new Vector3f(0, 0, -1f);
    private void renderCameraAxis(Vector3f scale, Vector4f color) {
        Outskirts.renderEngine.getModelRenderer().render(ModelRenderer.MODEL_CUBE_BZERO, Texture.UNIT,
                POS_OFF,
                scale,
                theBasis,
                color,
                false,
                true,
                GL_TRIANGLES
        );
    }
}
