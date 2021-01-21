package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.render.Texture;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GuiBasisVisual extends Gui {

    private Vector3f TMP_VEC3 = new Vector3f(); // TMP_VEC3_TRANS heap cache

    public GuiBasisVisual(Matrix3f theBasis, boolean useProj) {
        setWidth(100);
        setHeight(100);

        Vector2f ndc = new Vector2f();
        Vector3f pos = new Vector3f();
        addOnDrawListener(e -> {
            Maths.calculateNormalDeviceCoords(getX()+getWidth()/2f, getY()+getHeight()/2f, Outskirts.getWidth(), Outskirts.getHeight(), ndc);
            pos.set(ndc.x,ndc.y, useProj ? -1 : 0);

            // renderCameraAxises
            float s = 0.003f;
            float wr=getWidth()/Outskirts.getWidth(), hr=getHeight()/Outskirts.getHeight();
            float lw = useProj ? (wr+hr)/2f : axisLen(theBasis, Vector3f.UNIT_X, Vector3f.UNIT_X, wr, hr);
            float lh = useProj ? (wr+hr)/2f : axisLen(theBasis, Vector3f.UNIT_Y, Vector3f.UNIT_Y, hr, wr);
            float lz = useProj ? (wr+hr)/2f : axisLen(theBasis, Vector3f.UNIT_Z, Vector3f.UNIT_X, wr, hr);
            renderCameraAxis(pos, theBasis, TMP_VEC3.set(lw, s, s), Colors.FULL_R, useProj);
            renderCameraAxis(pos, theBasis, TMP_VEC3.set(s, lh, s), Colors.FULL_G, useProj);
            renderCameraAxis(pos, theBasis, TMP_VEC3.set(s, s, lz), Colors.FULL_B, useProj);
        });
    }

    private float axisLen(Matrix3f theBasis, Vector3f axis, Vector3f targAxis, float inAxisLen, float outAxisLen) {
        Matrix3f.transform(theBasis, TMP_VEC3.set(axis));
        return Maths.lerp(Math.abs(Vector3f.dot(TMP_VEC3, targAxis)), outAxisLen, inAxisLen);
    }

    private void renderCameraAxis(Vector3f pos, Matrix3f rot, Vector3f scale, Vector4f color, boolean useProj) {
        Outskirts.renderEngine.getModelRenderer().render(ModelRenderer.MODEL_CUBE_BZERO, Texture.UNIT,
                pos,
                scale,
                rot,
                color,
                false,
                useProj,
                GL_TRIANGLES
        );
    }
}
