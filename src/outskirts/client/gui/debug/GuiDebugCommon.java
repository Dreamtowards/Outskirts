package outskirts.client.gui.debug;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiMenu;
import outskirts.client.gui.GuiMenubar;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiInspEntity;
import outskirts.client.material.Texture;
import outskirts.client.render.Light;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.entity.player.EntityPlayer;
import outskirts.init.Textures;
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

        Gui MEMLOG = addGui(new GuiMemoryLog()).setVisible(false).addLayoutorAlignParentLTRB(0, Float.NaN, Float.NaN, 0);
        Gui PROFILERV = addGui(new GuiProfilerVisual()).setVisible(false).addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, 10, 10);
        Gui VERT3D = addGui(GuiVert3D.INSTANCE).setVisible(false).setRelativeY(32);

        Gui ENTITYINSP = Outskirts.getRootGUI().addGui(new GuiWindow(GuiInspEntity.INSTANCE)).setVisible(false);

        debugMenu.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
        {
            GuiMenu mDebug = debugMenu.addMenu("DebugV", new GuiMenu());
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Infos display", false,  c -> GuiDebugCommon.INSTANCE.showCambasisAndInfos =c).bindKey(GLFW.GLFW_KEY_F3));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Memlog window", false, MEMLOG::setVisible));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Profile window", false, PROFILERV::setVisible));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("3DVertices window", false, VERT3D::setVisible).bindKey(GLFW.GLFW_KEY_V));
//            mDebug.addGui(GuiMenu.GuiItem.bswitch("Entity Insp", false, ENTITYINSP::setVisible).bindKey(GLFW.GLFW_KEY_I)); //ENTITYINSP::setVisible
            mDebug.addGui(GuiMenu.GuiItem.divider());
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Show Lights Marks", true, c -> showLightMarks =c));
            mDebug.addGui(GuiMenu.GuiItem.divider());
            mDebug.addGui(GuiMenu.GuiItem.slider("WalkSpeed: %s", 1, 0, 5, v -> EntityPlayer.walkSpeed=v));

            GuiMenu mPhys = debugMenu.addMenu("Phys", new GuiMenu());
            mPhys.addGui(GuiMenu.GuiItem.bswitch("BoundingBox", false, c -> GuiDebugPhys.INSTANCE.showBoundingBox=c));
            mPhys.addGui(GuiMenu.GuiItem.bswitch("Velocities", false, c -> GuiDebugPhys.INSTANCE.showVelocities=c));
            mPhys.addGui(GuiMenu.GuiItem.bswitch("ContactPoints", false, c -> GuiDebugPhys.INSTANCE.showContactPoints=c));
            mPhys.addGui(GuiMenu.GuiItem.divider());
            mPhys.addGui(GuiMenu.GuiItem.slider("PhysSpeed: %s", 1, 0, 3, Outskirts::setPauseWorld));
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
                        Gui.drawTexture(Textures.ICON_LIGHT, x, y, 24, 24);
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
