package outskirts.network.login;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.client.gui.screen.GuiScreenDisconnect;
import outskirts.client.gui.screen.GuiScreenMainMenu;
import outskirts.event.EventHandler;
import outskirts.event.conn.ChannelInactiveEvent;
import outskirts.network.ChannelHandler;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.PacketHandlerPlayClient;
import outskirts.util.logging.Log;
import outskirts.world.WorldClient;

public class PacketHandlerLoginClient {

    private ChannelHandler connection;

    public PacketHandlerLoginClient(ChannelHandler conn) {
        this.connection = conn;
    }

    @EventHandler // sheculer=Client cuz switch PacketHandler
    private void handleLoginSuccess(SPacketLoginSuccess packet) {

        // switch handler
        connection.eventBus().unregister(this); // modify list
        connection.eventBus().register(new PacketHandlerPlayClient(connection));

        // init GUI
        Outskirts.getRootGUI().removeAllGuis();
        Outskirts.getRootGUI().addGui(GuiIngame.INSTANCE);

        Outskirts.getPlayer().connection = connection;

        Outskirts.setWorld(new WorldClient());
        Outskirts.getWorld().addEntity(Outskirts.getPlayer());
    }

    @EventHandler
    private void handleDisconnect(SPacketDisconnect packet) {
        connection.closeChannel(packet.getReason());
    }

    @EventHandler
    private void onDisconnect(ChannelInactiveEvent event) { //todo ext.test
        Outskirts.getRootGUI().removeLastGui(); // pop GuiwConnecting, to GuiwMultiplay
        Outskirts.getRootGUI().addGui(new GuiScreenDisconnect(connection.getTerminationReason()));
    }
}