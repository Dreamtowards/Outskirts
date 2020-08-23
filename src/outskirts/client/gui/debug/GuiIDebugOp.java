package outskirts.client.gui.debug;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiMenubar;
import outskirts.client.gui.GuiPopupMenu;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiInspEntity;
import outskirts.client.gui.screen.GuiScreen;
import outskirts.entity.player.EntityPlayer;
import outskirts.util.logging.Log;

public class GuiIDebugOp {

    public static GuiPopupMenu debugMenu = new GuiPopupMenu();

    static {
        GuiPopupMenu mCommon = new GuiPopupMenu();
        debugMenu.addItem(GuiPopupMenu.GuiItem.menu("DebugV", mCommon));
        {
            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Infos", false, b -> {
                GuiDebugCommon.instance.showCambasisAndInfos=b;
            }));
            mCommon.addItem(GuiPopupMenu.GuiItem.button("MemLog WD", () -> {
                Gui memLog = Gui.getRootGUI().addGui(new GuiMemoryLog());
                memLog.addLayoutorAlignParentLTRB(0, Float.NaN, Float.NaN, 0);
            }));
            mCommon.addItem(GuiPopupMenu.GuiItem.button("ProfVisual WD", () -> {
                Gui profV = Gui.getRootGUI().addGui(new GuiProfilerVisual());
                profV.addLayoutorAlignParentLTRB(Float.NaN, Float.NaN, 10, 10);
            }));
            mCommon.addItem(GuiPopupMenu.GuiItem.button("Vert3D WD", () -> {
                Gui vert3D = Gui.getRootGUI().addGui(GuiVert3D.INSTANCE);
            }));
            mCommon.addItem(GuiPopupMenu.GuiItem.button("EntityInsp WD", () -> {
                Gui.getRootGUI().addGui(new GuiWindow(GuiInspEntity.INSTANCE));
            }));
            mCommon.addItem(GuiPopupMenu.GuiItem.divider());
            mCommon.addItem(GuiPopupMenu.GuiItem.bswitch("Show Lights Marks", true, c -> GuiDebugCommon.instance.showLightMarks =c));
            mCommon.addItem(GuiPopupMenu.GuiItem.divider());
            mCommon.addItem(GuiPopupMenu.GuiItem.slider("WalkSpeed: %s", 1, 0, 5, v -> EntityPlayer.walkSpeed=v));

            GuiPopupMenu mInsp = new GuiPopupMenu();
            mCommon.addItem(GuiPopupMenu.GuiItem.menu("Insp", mInsp));
            {
                mInsp.addItem(GuiPopupMenu.GuiItem.button("EntityInsp", () -> {}));
                mInsp.addItem(GuiPopupMenu.GuiItem.button("RigidbodyInsp", () -> {}));
                mInsp.addItem(GuiPopupMenu.GuiItem.button("LightInsp", () -> {}));
                mInsp.addItem(GuiPopupMenu.GuiItem.button("Other", () -> {}));
            }
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

        debugMenu.addItem(GuiPopupMenu.GuiItem.button("Store", () -> {}));
    }
}
