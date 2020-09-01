package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiPopupMenu;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiInspectionEntity;
import outskirts.entity.player.EntityPlayer;
import outskirts.event.EventPriority;
import outskirts.util.vector.Matrix3f;

public class GuiIDebugOp {

    public static GuiPopupMenu debugMenu = new GuiPopupMenu();

    static {
        GuiPopupMenu mCommon = new GuiPopupMenu();
        debugMenu.addItem(GuiPopupMenu.GuiItem.menu("DebugV", mCommon));
        {
            Gui txInfos = new GuiDebugTxInfos();
            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Infos", false, b -> toggleShow(txInfos, b)));

            GuiBasisVisual camBasis = new GuiBasisVisual();
            camBasis.addOnDrawListener(e -> Matrix3f.set(camBasis.theBasis, Outskirts.renderEngine.getViewMatrix())).priority(EventPriority.HIGH);
            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("CamBasis", false, b -> toggleShow(camBasis, b)));

            Gui memLog = new GuiMemoryLog();
            memLog.addLayoutorAlignParentLTRB(0, Float.NaN, Float.NaN, 0);
            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("MemLog WD", false, b -> toggleShow(memLog, b)));

            Gui profV = new GuiProfilerVisual();
            profV.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, 10, 10);
            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("ProfVisual WD", false, b -> toggleShow(profV, b)));

            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Vert3D WD", false, b -> toggleShow(GuiVert3D.INSTANCE, b)));
        }
        GuiPopupMenu mInsp = new GuiPopupMenu();
        debugMenu.addItem(GuiPopupMenu.GuiItem.menu("Insp", mInsp));
        {
            mInsp.addItem(GuiPopupMenu.GuiItem.button("EntityInsp WD", () -> {
                Gui.getRootGUI().addGui(new GuiWindow(new GuiInspectionEntity(Outskirts.getRayPicker().getCurrentEntity())));
                debugMenu.hide();
            }));
            mInsp.addItem(GuiPopupMenu.GuiItem.bswitch("Show Lights Marks", true, c -> {}));
            mInsp.addItem(GuiPopupMenu.GuiItem.divider());
            mInsp.addItem(GuiPopupMenu.GuiItem.slider("WalkSpeed: %s", 1, 0, 5, v -> EntityPlayer.walkSpeed=v));
        }
        GuiPopupMenu mPhys = new GuiPopupMenu();
        debugMenu.addItem(GuiPopupMenu.GuiItem.menu("Phys", mPhys));
        {
            mPhys.addItem(GuiPopupMenu.GuiItem.bswitch("BoundingBox", false, c -> GuiDebugPhys.INSTANCE.showBoundingBox=c));
            mPhys.addItem(GuiPopupMenu.GuiItem.bswitch("Velocities", false, c -> GuiDebugPhys.INSTANCE.showVelocities=c));
            mPhys.addItem(GuiPopupMenu.GuiItem.bswitch("ContactPoints", false, c -> GuiDebugPhys.INSTANCE.showContactPoints=c));
            mPhys.addItem(GuiPopupMenu.GuiItem.divider());
            mPhys.addItem(GuiPopupMenu.GuiItem.slider("PhysSpeed: %s", 1, 0, 3, Outskirts::setPauseWorld));
        }

        debugMenu.addItem(GuiPopupMenu.GuiItem.button("Gui Widgets Test Window", () -> {
            Gui.getRootGUI().addGui(new GuiWindow(new GuiTestWindowWidgets()));
        }));
    }

    private static void toggleShow(Gui g, boolean show) {
        if (show) Gui.getRootGUI().addGui(g);
        else Gui.getRootGUI().removeGui(g);
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
}
