package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.material.Texture;
import outskirts.client.render.Light;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtils;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GuiDebugCommon extends Gui {

    public static final GuiDebugCommon instance = new GuiDebugCommon();

    public boolean showCambasisAndInfos;
    public boolean showLightMarks;

    private float deltaSumUntilOne = 0;
    private int currSecFrames = 0;
    private int prevSecFrames = 0;

    {
        addGui(GuiDebugPhys.INSTANCE);
    }

    {
        addOnDrawListener(e -> {

            // Pointer. this actually not belong Debug.
            int POINTER_SIZE = 4;
            drawRect(Colors.WHITE, Outskirts.getWidth()/2f-POINTER_SIZE/2f, Outskirts.getHeight()/2f-POINTER_SIZE/2f, POINTER_SIZE, POINTER_SIZE);

            if (Outskirts.getWorld() != null && showLightMarks) {
                // showLightsMark
                for (Light light : Outskirts.getWorld().lights) {
                    Gui.drawWorldpoint(light.getPosition(), (x, y) -> {
                        Gui.drawString("Light", x, y, Colors.WHITE);
                    });
                }
            }

            if (showCambasisAndInfos) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("\nU/T: %s / %s | JVM_Max: %s\n", FileUtils.toDisplaySize(SystemUtils.MEM_USED), FileUtils.toDisplaySize(SystemUtils.MEM_TOTAL), FileUtils.toDisplaySize(SystemUtils.MEM_MAXIMUM)));

                currSecFrames++;
                deltaSumUntilOne += Outskirts.getDelta();
                if (deltaSumUntilOne >= 1f) {
                    prevSecFrames = currSecFrames;
                    deltaSumUntilOne = 0;
                    currSecFrames = 0;
                }
                sb.append(String.format("P: avgT: %sms, s: %s\n", 1000f/prevSecFrames, prevSecFrames));

                sb.append(String.format("CameraPos: %s\n", Outskirts.getCamera().getPosition()));

                drawString(sb.toString(), getX(), getY()+32, Colors.WHITE);


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


    private static void renderAnimationDebug() {

//                    GuiVert3D.INSTANCE.vertices.clear();
//                    for (int i = 0;i < jts.length;i++) {
//                        Vector4f vo = Matrix4f.transform(jts[i]._bindTransform, new Vector4f(0,0,0,1));
//                        Vector4f vc = Matrix4f.transform(jts[i].currentTransform, new Vector4f(0,0,0,1));
//                        GuiVert3D.addVert("jo-"+jts[i].name, new Vector3f(vo.x, vo.y, vo.z), Colors.GREEN, i==0?new String[0]:new String[]{"jo-"+jts[jts[i].parentIdx].name});
//                        GuiVert3D.addVert("jc-"+jts[i].name, new Vector3f(vc.x, vc.y, vc.z), Colors.RED, i==0?new String[0]:new String[]{"jc-"+jts[jts[i].parentIdx].name});
//                    }
    }

    @Override
    public float getWidth() {
        return Outskirts.getWidth();
    }

    @Override
    public float getHeight() {
        return Outskirts.getHeight();
    }
}
