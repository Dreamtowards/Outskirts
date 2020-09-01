package outskirts.network.play;

import outskirts.client.Outskirts;
import outskirts.client.gui.screen.GuiScreenDisconnect;
import outskirts.client.gui.screen.GuiScreenMainMenu;
import outskirts.event.EventHandler;
import outskirts.event.conn.ChannelInactiveEvent;
import outskirts.network.ChannelHandler;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.network.play.packet.SPacketDisconnect;
import outskirts.util.logging.Log;
import outskirts.world.World;
import outskirts.world.WorldClient;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PacketHandlerPlayClient {

    private ChannelHandler connection;

    public PacketHandlerPlayClient(ChannelHandler conn) {
        this.connection = conn;
    }

    @EventHandler
    private void handleChatMessage(SPacketChatMessage packet) {

//        GuiScreenInGame.INSTANCE.getGuiChatMessages().printMessage(packet.getMessage());
        Log.LOGGER.info("handleChatMessage: {}", packet.getMessage());
    }

    @EventHandler
    private void handleDisconnect(SPacketDisconnect packet) {
        connection.closeChannel(packet.getReason());
    }

    @EventHandler
    private void onDisconnect(ChannelInactiveEvent event) {
        if (Outskirts.getWorld() != null) { // world==null means initiative disconne, then dont handle that.
            Outskirts.setWorld(null);

            Outskirts.getRootGUI().removeAllGuis();
            Outskirts.getRootGUI().addGui(GuiScreenMainMenu.INSTANCE);
            Outskirts.getRootGUI().addGui(new GuiScreenDisconnect(connection.getTerminationReason()));
        }
    }
}
