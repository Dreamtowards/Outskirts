package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.material.Texture;
import outskirts.client.render.Light;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.init.Textures;
import outskirts.util.Colors;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GuiDebugCommon extends Gui {

    public static final GuiDebugCommon INSTANCE = new GuiDebugCommon();

    public boolean showCambasis;
    public boolean showLightMarks;

    {
        addOnDrawListener(e -> {

            // Pointer. this actually not belong Debug.
            int POINTER_SIZE = 4;
            drawRect(Colors.WHITE, Outskirts.getWidth()/2f-POINTER_SIZE/2f, Outskirts.getHeight()/2f-POINTER_SIZE/2f, POINTER_SIZE, POINTER_SIZE);

            if (Outskirts.getWorld() != null && showLightMarks) {
                // showLightsMark
                for (Light light : Outskirts.getWorld().lights) {
                    Gui.drawWorldpoint(light.getPosition(), (x, y) -> {
                        Gui.drawTexture(Textures.ICON_LIGHT, x, y, 24, 24);
                    });
                }
            }

            if (showCambasis) {
                // renderCameraAxises
                Matrix3f.set(TMP_MAT3, Outskirts.renderEngine.getViewMatrix());
                float s = 0.003f;
                float l = 0.08f;
                renderCameraAxis(TMP_VEC3.set(l, s, s), Colors.FULL_R);
                renderCameraAxis(TMP_VEC3.set(s, l, s), Colors.FULL_G);
                renderCameraAxis(TMP_VEC3.set(s, s, l), Colors.FULL_B);
            }
        });
    }

    private static Vector3f TMP_VEC3 = new Vector3f(); // TMP_VEC3_TRANS heap cache
    private static Matrix3f TMP_MAT3 = new Matrix3f();
    private static final Vector3f POS_OFF = new Vector3f(0, 0, -1f);
    private static void renderCameraAxis(Vector3f scale, Vector4f color) {
        Outskirts.renderEngine.getModelRenderer().render(ModelRenderer.MODEL_CUBE_BZERO, Texture.UNIT,
                POS_OFF,
                scale,
                TMP_MAT3,
                color,
                false,
                true,
                GL_TRIANGLES
        );
    }
}
