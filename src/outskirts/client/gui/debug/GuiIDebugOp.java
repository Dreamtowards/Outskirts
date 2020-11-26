package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiPopupMenu;
import outskirts.client.gui.GuiSlider;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiIEntity;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.entity.EntityStaticMesh;
import outskirts.event.EventPriority;
import outskirts.init.ex.Models;
import outskirts.util.vector.Matrix3f;

import static org.lwjgl.opengl.GL11.*;
import static outskirts.util.logging.Log.LOGGER;

public class GuiIDebugOp {

//    public static GuiPopupMenu debugMenu = new GuiPopupMenu();

//    static {
//        GuiPopupMenu mCommon = new GuiPopupMenu();
//        debugMenu.addItem(GuiPopupMenu.GuiItem.menu("DebugV", mCommon));
//        {
//            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Infos", false, b -> toggleShow(GuiDebugTxInfos.INSTANCE, b)));
//
//            Matrix3f theBasis = new Matrix3f();
//            GuiBasisVisual camBasis = new GuiBasisVisual(theBasis, true);
//            camBasis.addOnDrawListener(e -> Matrix3f.set(theBasis, Outskirts.renderEngine.getViewMatrix())).priority(EventPriority.HIGH);
//            camBasis.addLayoutorAlignParentRR(0.5f, 0.5f);
//            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("CamBasis", false, b -> toggleShow(camBasis, b)));

//            Gui memLog = new GuiMemoryLog();
//            memLog.addLayoutorAlignParentLTRB(0, Float.NaN, Float.NaN, 0);
//            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("MemLog WD", false, b -> toggleShow(memLog, b)));
//
//            Gui profV = new GuiProfilerVisual();
//            profV.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, 10, 10);
//            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("ProfVisual WD", false, b -> toggleShow(profV, b)));

//            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Vert3D WD", false, b -> toggleShow(GuiVert3D.INSTANCE, b)));

//            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Poly Lines", false, b -> {
//                 glPolygonMode(GL_FRONT_AND_BACK, b?GL_LINE: GL_FILL);
//            }));
//        }
//        GuiPopupMenu mInsp = new GuiPopupMenu();
//        debugMenu.addItem(GuiPopupMenu.GuiItem.menu("Insp", mInsp));
//        {
//            mInsp.addItem(GuiPopupMenu.GuiItem.button("EntityInspection CurrEntity", () -> {
//                if (Outskirts.isShiftKeyDown()) {
//                    Gui.getRootGUI().addGui(new GuiWindow(new GuiIEntity(Outskirts.getPlayer())));
//                    debugMenu.hide();
//                    return;
//                }
//                if (Outskirts.getRayPicker().getCurrentEntity() == null) {
//                    LOGGER.info("null CurrentEntity.");
//                    return;
//                }
//                Gui.getRootGUI().addGui(new GuiWindow(new GuiIEntity(Outskirts.getRayPicker().getCurrentEntity())));
//                debugMenu.hide();
//            }));
//            mInsp.addItem(GuiPopupMenu.GuiItem.button("Add EntityStaticMesh", () -> {
//                if (Outskirts.getRayPicker().getCurrentPoint() == null) {
//                    LOGGER.info("null CurrentPoint.");
//                    return;
//                }
//                EntityStaticMesh entityStaticmesh = new EntityStaticMesh();
//                entityStaticmesh.setModel(Models.GEO_CUBE);
//                entityStaticmesh.getRigidBody().transform().origin.set(Outskirts.getRayPicker().getCurrentPoint());
//                Outskirts.getWorld().addEntity(entityStaticmesh);
//                debugMenu.hide();
//            }));
//            mInsp.addItem(GuiPopupMenu.GuiItem.bswitch("Show Lights Marks", true, c -> toggleShow(GuiILightsList.INSTANCE, c)));
//            mInsp.addItem(GuiPopupMenu.GuiItem.divider());
//            mInsp.addItem(GuiPopupMenu.GuiItem.slider("WalkSpeed: %s", 1, 0, 5, v -> EntityPlayer.walkSpeed=v));
//        }
//
//    }

//    private static void toggleShow(Gui g, boolean show) {
//        if (show) GuiIngame.INSTANCE.addGui(g);
//        else GuiIngame.INSTANCE.removeGui(g);
//    }




//    private static void renderAnimationDebug() {

//                    GuiVert3D.INSTANCE.vertices.clear();
//                    for (int i = 0;i < jts.length;i++) {
//                        Vector4f vo = Matrix4f.transform(jts[i]._bindTransform, new Vector4f(0,0,0,1));
//                        Vector4f vc = Matrix4f.transform(jts[i].currentTransform, new Vector4f(0,0,0,1));
//                        GuiVert3D.addVert("jo-"+jts[i].name, new Vector3f(vo.x, vo.y, vo.z), Colors.GREEN, i==0?new String[0]:new String[]{"jo-"+jts[jts[i].parentIdx].name});
//                        GuiVert3D.addVert("jc-"+jts[i].name, new Vector3f(vc.x, vc.y, vc.z), Colors.RED, i==0?new String[0]:new String[]{"jc-"+jts[jts[i].parentIdx].name});
//                    }
//    }
}
