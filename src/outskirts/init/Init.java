package outskirts.init;

import outskirts.client.Outskirts;
import outskirts.client.gui.GuiMenu;
import outskirts.client.gui.GuiMenubar;
import outskirts.client.gui.debug.*;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.command.Command;
import outskirts.command.server.*;
import outskirts.entity.Entity;
import outskirts.entity.EntityStall;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.network.Packet;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.network.play.packet.SPacketTerrainData;
import outskirts.network.play.packet.SPacketTerrainUnload;
import outskirts.util.Side;

public final class Init {

    private static void registerPackets() {


        Packet.registerPacket(CPacketLogin.class);
        Packet.registerPacket(SPacketDisconnect.class);
        Packet.registerPacket(SPacketLoginSuccess.class);

        Packet.registerPacket(outskirts.network.play.packet.SPacketDisconnect.class);
        Packet.registerPacket(SPacketTerrainData.class);
        Packet.registerPacket(SPacketTerrainUnload.class);
        Packet.registerPacket(SPacketChatMessage.class);


        Packet.buildRegistry();
    }

    private static void registerCommands() {

        Command.REGISTRY.register(new CommandShutdown());
        Command.REGISTRY.register(new CommandKick());
        Command.REGISTRY.register(new CommandSummon());
        Command.REGISTRY.register(new CommandPlayerlist());
        Command.REGISTRY.register(new CommandSay());

    }

    private static void registerEntities(Side side) {

        Entity.REGISTRY.register(EntityStall.class);

        Entity.REGISTRY.register(side.isClient() ? EntityPlayerSP.class : EntityPlayerMP.class);
    }


    //use event to register/release..?
    public static void registerAll(Side side) {

        if (side.isClient()) {

            Sounds.init();

            Textures.init();

            Models.init();

            initGuiMenu();

        } else {

            registerCommands();
        }

        registerPackets();

        registerEntities(side);
    }



    private static void initGuiMenu() {

        GuiWindow TXTINFOS = Outskirts.getRootGUI().addGui(new GuiWindow(new GuiDebugTextInfos())).setVisible(false);
        GuiWindow MEMLOG = Outskirts.getRootGUI().addGui(new GuiWindow(new GuiMemoryLog())).setVisible(false);
        GuiWindow PROFILERV = Outskirts.getRootGUI().addGui(new GuiWindow(new GuiProfilerVisual())).setVisible(false);
        GuiWindow VERT3D = Outskirts.getRootGUI().addGui(new GuiWindow(GuiScreen3DVertices._TMP_DEF_INST)).setVisible(false);

        GuiMenubar menubar = Outskirts.getRootGUI().addGui(new GuiMenubar());
        menubar.addLayoutorAlignParentLTRB(0, 0, 0, Float.NaN);
        {
            GuiMenu mDebug = menubar.addMenu("DebugV", new GuiMenu());
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Infos display", false, TXTINFOS::setVisible));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Memlog window", false, MEMLOG::setVisible));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Profile window", false, PROFILERV::setVisible));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("3DVertices window", false, VERT3D::setVisible));
            mDebug.addGui(GuiMenu.GuiItem.divider());
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Cam Basis", false, c -> GuiDebugCommon.INSTANCE.showCambasis =c));
            mDebug.addGui(GuiMenu.GuiItem.bswitch("Show Lights Marks", true, c -> GuiDebugCommon.INSTANCE.showLightMarks =c));
            mDebug.addGui(GuiMenu.GuiItem.divider());
            mDebug.addGui(GuiMenu.GuiItem.slider("WalkSpeed: %s", 1, 0, 5, v -> EntityPlayer.walkSpeed=v));

            GuiMenu mPhys = menubar.addMenu("Phys", new GuiMenu());
            mPhys.addGui(GuiMenu.GuiItem.bswitch("BoundingBox", false, c -> GuiDebugPhys.INSTANCE.showBoundingBox=c));
            mPhys.addGui(GuiMenu.GuiItem.bswitch("Velocities", false, c -> GuiDebugPhys.INSTANCE.showVelocities=c));
            mPhys.addGui(GuiMenu.GuiItem.bswitch("ContactPoints", false, c -> GuiDebugPhys.INSTANCE.showContactPoints=c));
            mPhys.addGui(GuiMenu.GuiItem.divider());
            mPhys.addGui(GuiMenu.GuiItem.slider("PhysSpeed: %s", 1, 0, 3, Outskirts::setPauseWorld));
        }
    }

}
