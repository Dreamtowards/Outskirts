package outskirts.client.gui.debug;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiPopupMenu;
import outskirts.client.gui.GuiMenubar;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiInspEntity;
import outskirts.client.material.Texture;
import outskirts.client.render.Light;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.entity.player.EntityPlayer;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtils;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class GuiDebugCommon extends Gui {

    public static final GuiDebugCommon INSTANCE = new GuiDebugCommon();

    public GuiMenubar debugMenu = addGui(new GuiMenubar());

    public boolean showCambasisAndInfos;
    public boolean showLightMarks;

    private float deltaSumUntilOne = 0;
    private int currSecFrames = 0;
    private int prevSecFrames = 0;

    {
        addGui(GuiDebugPhys.INSTANCE);

        Gui MEMLOG = addGui(new GuiMemoryLog());
        MEMLOG.setVisible(false);
        MEMLOG.addLayoutorAlignParentLTRB(0, Float.NaN, Float.NaN, 0);

        Gui PROFILERV = addGui(new GuiProfilerVisual());
        PROFILERV.setVisible(false);
        PROFILERV.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, 10, 10);

        Gui VERT3D = addGui(GuiVert3D.INSTANCE);
        VERT3D.setVisible(false);
        VERT3D.setRelativeY(32);

        Gui ENTITYINSP = addGui(new GuiWindow(GuiInspEntity.INSTANCE));
//        GuiInspEntity.INSTANCE.setVisible(true);

        debugMenu.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
        {
            GuiPopupMenu mDebug = debugMenu.addMenu("DebugV", new GuiPopupMenu());
            mDebug.addGui(GuiPopupMenu.Item.bswitch("Infos display", false, c -> GuiDebugCommon.INSTANCE.showCambasisAndInfos =c).bindKey(GLFW.GLFW_KEY_F3));
            mDebug.addGui(GuiPopupMenu.Item.bswitch("Memlog window", false, MEMLOG::setVisible));
            mDebug.addGui(GuiPopupMenu.Item.bswitch("Profile window", false, PROFILERV::setVisible));
            mDebug.addGui(GuiPopupMenu.Item.bswitch("3DVertices window", false, VERT3D::setVisible).bindKey(GLFW.GLFW_KEY_V));
            mDebug.addGui(GuiPopupMenu.Item.bswitch("Entity Insp", false, ENTITYINSP::setVisible).bindKey(GLFW.GLFW_KEY_I)); //ENTITYINSP::setVisible
            mDebug.addGui(GuiPopupMenu.Item.divider());
            mDebug.addGui(GuiPopupMenu.Item.bswitch("Show Lights Marks", true, c -> showLightMarks =c));
            mDebug.addGui(GuiPopupMenu.Item.divider());
            mDebug.addGui(GuiPopupMenu.Item.slider("WalkSpeed: %s", 1, 0, 5, v -> EntityPlayer.walkSpeed=v));

            GuiPopupMenu mPhys = debugMenu.addMenu("Phys", new GuiPopupMenu());
            mPhys.addGui(GuiPopupMenu.Item.bswitch("BoundingBox", false, c -> GuiDebugPhys.INSTANCE.showBoundingBox=c));
            mPhys.addGui(GuiPopupMenu.Item.bswitch("Velocities", false, c -> GuiDebugPhys.INSTANCE.showVelocities=c));
            mPhys.addGui(GuiPopupMenu.Item.bswitch("ContactPoints", false, c -> GuiDebugPhys.INSTANCE.showContactPoints=c));
            mPhys.addGui(GuiPopupMenu.Item.divider());
            mPhys.addGui(GuiPopupMenu.Item.slider("PhysSpeed: %s", 1, 0, 3, Outskirts::setPauseWorld));
        }
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

    @Override
    public float getWidth() {
        return Outskirts.getWidth();
    }

    @Override
    public float getHeight() {
        return Outskirts.getHeight();
    }
}
