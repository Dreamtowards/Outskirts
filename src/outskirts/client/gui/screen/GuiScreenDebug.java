package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.client.gui.screen.tools.debug.GuiMemoryLog;
import outskirts.client.gui.screen.tools.debug.GuiProfilerVisual;
import outskirts.client.material.Texture;
import outskirts.client.render.Light;
import outskirts.client.render.renderer.ModelRenderer;
import outskirts.entity.player.EntityPlayer;
import outskirts.init.Textures;
import outskirts.physics.collision.dispatch.CollisionObject;
import outskirts.physics.collision.dispatch.CollisionManifold;
import outskirts.physics.dynamics.RigidBody;
import outskirts.util.Colors;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtils;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import static org.lwjgl.opengl.GL11.*;

public class GuiScreenDebug extends GuiScreen {

    private static Vector3f TMP_VEC3 = new Vector3f(); // TMP_VEC3_TRANS heap cache
    private static Matrix3f TMP_MAT3 = new Matrix3f();

    private GuiMemoryLog guiMemoryLog = addGui(new GuiMemoryLog());
    private GuiProfilerVisual guiProfilerVisual = addGui(new GuiProfilerVisual());

//    private Gui guiLightSetter = addGui(new Gui() {
//        Light l = Outskirts.getWorld().lights.get(0);
//        GuiSetterScalars color = addGui(GuiSetterScalars.forVector3f(l.getColor())).setRelativeY(0);
//        GuiTextFieldNumerical distance = addGui(new GuiTextFieldNumerical()).addOnTextChangedListener(e -> {
//            Light.calculateApproximateAttenuation(e.<GuiTextFieldNumerical>gui().getValue(), l.getAttenuation());
//        }).setRelativeY(50);
//        GuiSetterScalars dir = addGui(GuiSetterScalars.forVector3f(l.getDirection())).setRelativeY(100).addOnDrawListener(e -> l.getDirection().normalize());
//        GuiSetterScalars cone = addGui(new GuiSetterScalars(new Pair<>(l::getConeAngleInner, l::setConeAngleInner),
//                new Pair<>(l::getConeAngleOuter, l::setConeAngleOuter))).setRelativeY(150);
//
//
//    }).setY(350);

    private Gui guiEnableDebugFuncs = addGui(new GuiScroll().setContentGui(
            new Gui() {
                GuiCheckBox ENABLE_PHYS_BOUNDINGBOX = addGui(new GuiCheckBox("EnablePhysBoundingBox")).addOnCheckedChangedListener(e -> showPhysBoundingbox = e.<GuiCheckBox>gui().isChecked()).setChecked(false);
                GuiCheckBox ENABLE_PHYS_VELOCITIES = addGui(new GuiCheckBox("EnablePhysVelocities")).addOnCheckedChangedListener(e -> showPhysVelicities = e.<GuiCheckBox>gui().isChecked()).setChecked(false).setRelativeY(50);
                GuiCheckBox ENABLE_PHYS_CP = addGui(new GuiCheckBox("EnablePhysContactpoints")).addOnCheckedChangedListener(e -> showPhysContactpoints = e.<GuiCheckBox>gui().isChecked()).setChecked(false).setRelativeY(100);

                GuiCheckBox ENABLE_DEBUG_MEM = addGui(new GuiCheckBox("EnableDebugMem")).addOnCheckedChangedListener(e -> guiMemoryLog.setVisible(e.<GuiCheckBox>gui().isChecked())).setChecked(true).setRelativeY(150);
                GuiCheckBox ENABLE_DEBUG_PROF = addGui(new GuiCheckBox("EnableDebugProf")).addOnCheckedChangedListener(e -> guiProfilerVisual.setVisible(e.<GuiCheckBox>gui().isChecked())).setChecked(true).setRelativeY(200);

                GuiSlider TOGGLE_WORLD_PAUSE = addGui(new GuiSlider("TogglePause")).addValueChangedListener(e -> Outskirts.setPauseWorld(e.<GuiSlider>gui().getCurrentUserValue())).setUserMinMaxValue(0.0f, 4.0f).setRelativeY(250);
                GuiSlider TOGGLE_WALKSPEED = addGui(new GuiSlider("WalkSpeed")).addValueChangedListener(e -> EntityPlayer.walkSpeed = (e.<GuiSlider>gui().getCurrentUserValue())).setUserMinMaxValue(0.0f, 5.0f).setRelativeY(300);


                //GuiCheckBox ENABLE_SETTER_LIGHT = addGui(new GuiCheckBox("EnableSetterLight")).addOnCheckedChangedListener(e -> guiLightSetter.setVisible(e.<GuiCheckBox>gui().isChecked())).setChecked(false).setRelativeY(250);
        }.setHeight(1200))
    ).setWidth(250).setHeight(300).setY(120).setClipChildren(true);

//    private GuiSlider slider = addGui(new GuiSlider().setUserMinValue(7).setUserMaxValue(3000).addValueChangedListener(e -> {
//        ((GuiSlider)e.gui()).setText(""+((GuiSlider)e.gui()).getCurrentUserValue());
//    }));

    private float deltaSumUntilOne = 0;
    private int currSecFrames = 0;
    private int prevSecFrames = 0;

    private boolean showPhysBoundingbox = false;
    private boolean showPhysVelicities = false;
    private boolean showPhysContactpoints = false;

    public GuiScreenDebug() {

        addOnDrawListener(e -> {
            Outskirts.getProfiler().push("debugOnDraw");

            int startY = 16;

            int MEM_LOG_HEIGHT = 230;
            guiMemoryLog.setX(0).setY(Outskirts.getHeight() - MEM_LOG_HEIGHT).setWidth(650).setHeight(MEM_LOG_HEIGHT);

            int PROF_HEIGHT = 550;
            int PROF_WIDTH = 350;
            guiProfilerVisual.setX(Outskirts.getWidth() - PROF_WIDTH - 10).setY(Outskirts.getHeight() - PROF_HEIGHT - 20)
                    .setWidth(PROF_WIDTH).setHeight(PROF_HEIGHT);

            drawString(String.format("U/T: %s / %s | JVM_Max: %s", FileUtils.toDisplaySize(SystemUtils.MEM_USED), FileUtils.toDisplaySize(SystemUtils.MEM_TOTAL), FileUtils.toDisplaySize(SystemUtils.MEM_MAXIMUM)), 0, startY+=16, Colors.WHITE);

            currSecFrames++;
            deltaSumUntilOne += Outskirts.getDelta();
            if (deltaSumUntilOne >= 1f) {
                prevSecFrames = currSecFrames;
                deltaSumUntilOne = 0;
                currSecFrames = 0;
            }
            drawString(String.format("P: avgT: %sms, s: %s", 1000f/prevSecFrames, prevSecFrames), 0, startY+=16, Colors.WHITE);


            drawString(String.format("CameraPos: %s", Outskirts.getCamera().getPosition()), 0, startY+=16, Colors.WHITE);

            // render camera's chunk bound
//        if (Outskirts.getCamera().getPosition().y >= 0) {
//            Outskirts.renderEngine.getModelRenderer().drawOutline(
//                    Vector3f.unit(new Vector3f(Outskirts.getCamera().getPosition()), 16),
//                    new Vector3f(16, 16, 16)
//            );
//        }




            if (Outskirts.getWorld() != null) {   // phys debug
                for (Light light : Outskirts.getWorld().lights) {
                    Gui.drawWorldpoint(light.getPosition(), (x, y) -> {
                        Gui.drawTexture(Textures.ICON_LIGHT, x, y, 24, 24);
                    });
                }

//                GuiRenderer.PARAM_roundradius=4;
//                drawRect(Colors.BLACK40, 16, 200, 200, 100);
//                drawString("Broadphase: "  + Outskirts.getWorld().dynamicsWorld.getBroadphase().getOverlappingPairs().size(), 24, 216, Colors.WHITE);
//                drawString("Narrowphase: " + Outskirts.getWorld().dynamicsWorld.getNarrowphase().getCollisionManifolds().size(), 24, 232, Colors.WHITE);

                // Contact Points
                if (showPhysContactpoints) {
                    for (CollisionManifold manifold : Outskirts.getWorld().dynamicsWorld.getCollisionManifolds()) {
                        for (int i = 0; i < manifold.getNumContactPoints(); i++) {
                            Gui.drawWorldpoint(manifold.getContactPoint(i).pointOnB, (x, y) -> {
                                drawRect(Colors.RED, x, y, 4, 4);
                            });
                        }
                    }
                }

                for (CollisionObject co : Outskirts.getWorld().dynamicsWorld.getCollisionObjects()) {
                    RigidBody body = (RigidBody)co;
                    if (showPhysBoundingbox) {
                        // draw AABB
                        boolean inBroadphase = false, inNarrowphase = false;
                        for (CollisionManifold manifold : Outskirts.getWorld().dynamicsWorld.getBroadphase().getOverlappingPairs()) {
                            if (manifold.containsBody(body))
                                inBroadphase = true;
                        }
                        for (CollisionManifold manifold : Outskirts.getWorld().dynamicsWorld.getCollisionManifolds()) {
                            if (manifold.containsBody(body))
                                inNarrowphase = true;
                        }
                        Vector4f color = Colors.GREEN;
                        if (inNarrowphase)
                            color = Colors.YELLOW;
                        else if (inBroadphase)
                            color = Colors.RED;
                        Outskirts.renderEngine.getModelRenderer().drawOutline(body.getAABB(), color);

                        // oriention
                        Outskirts.renderEngine.getModelRenderer().drawLine(body.transform().origin,
                                new Vector3f(body.transform().origin).add(Matrix3f.transform(body.transform().basis, new Vector3f(Vector3f.UNIT_Y))), Colors.RED);
                    }

                    if (showPhysVelicities) {
                        // angular velocity
                        Outskirts.renderEngine.getModelRenderer().drawLine(body.transform().origin,
                                new Vector3f(body.transform().origin).add(body.getAngularVelocity()), Colors.DARK_RED);
                        // linear  velocity
                        Outskirts.renderEngine.getModelRenderer().drawLine(body.transform().origin,
                                new Vector3f(body.transform().origin).add(body.getLinearVelocity()), Colors.DARK_GREEN);
                    }
                }
            }

            // renderCameraAxises
            Matrix3f.set(TMP_MAT3, Outskirts.renderEngine.getViewMatrix());
            float s = 0.003f;
            float l = 0.08f;
            renderCameraAxis(TMP_VEC3.set(l, s, s), Colors.FULL_R);
            renderCameraAxis(TMP_VEC3.set(s, l, s), Colors.FULL_G);
            renderCameraAxis(TMP_VEC3.set(s, s, l), Colors.FULL_B);
            Outskirts.getProfiler().pop("debugOnDraw");
        });
    }

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
