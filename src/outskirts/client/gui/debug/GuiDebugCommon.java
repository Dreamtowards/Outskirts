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



            }
        });
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
