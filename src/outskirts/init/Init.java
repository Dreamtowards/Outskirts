package outskirts.init;

import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiMenu;
import outskirts.client.gui.GuiMenubar;
import outskirts.client.gui.debug.*;
import outskirts.client.gui.ex.GuiWindow;
import outskirts.client.gui.inspection.GuiInspEntity;
import outskirts.command.Command;
import outskirts.command.server.*;
import outskirts.entity.Entity;
import outskirts.entity.EntityModel;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.network.Packet;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.util.Side;
import outskirts.util.logging.Log;

public final class Init {

    private static void registerPackets() {


        Packet.registerPacket(CPacketLogin.class);
        Packet.registerPacket(SPacketDisconnect.class);
        Packet.registerPacket(SPacketLoginSuccess.class);

        Packet.registerPacket(outskirts.network.play.packet.SPacketDisconnect.class);
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

        Entity.REGISTRY.register(side.isClient() ? EntityPlayerSP.class : EntityPlayerMP.class);

        Entity.REGISTRY.register(EntityModel.class);
    }


    //use event to register/release..?
    public static void registerAll(Side side) {

        if (side.isClient()) {

            Sounds.init();

            Textures.init();

            Models.init();

        } else {

            registerCommands();
        }

        registerPackets();

        registerEntities(side);
    }

}
