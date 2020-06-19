package outskirts.network.play;

import outskirts.client.Outskirts;
import outskirts.client.gui.screen.GuiScreenDisconnect;
import outskirts.client.gui.screen.GuiScreenInGame;
import outskirts.client.gui.screen.GuiScreenMainMenu;
import outskirts.event.EventHandler;
import outskirts.event.conn.ChannelInactiveEvent;
import outskirts.network.ChannelHandler;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.network.play.packet.SPacketDisconnect;
import outskirts.network.play.packet.SPacketTerrainData;
import outskirts.network.play.packet.SPacketTerrainUnload;
import outskirts.util.logging.Log;
import outskirts.world.World;
import outskirts.world.WorldClient;
import outskirts.world.terrain.Terrain;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PacketHandlerPlayClient {

    private ChannelHandler connection;

    public PacketHandlerPlayClient(ChannelHandler conn) {
        this.connection = conn;
    }

    @EventHandler(scheduler = Outskirts.class) // tmp update terr model, provideTerrain->postEvent EventBus post exception.
    private void handleTerrainData(SPacketTerrainData packet) {

        Terrain terrain = Outskirts.getWorld().provideTerrain(packet.getPosLong());

        terrain.readNBT(packet.getTagCompound());

        terrain._update_texture();
        terrain._update_model();
    }

    @EventHandler(scheduler = Outskirts.class) // eventbushandlers UnmodificateException
    private void handleTerrainUnload(SPacketTerrainUnload packet) {

        Outskirts.getWorld().unloadTerrain(packet.getPosLong());
    }

    @EventHandler
    private void handleChatMessage(SPacketChatMessage packet) {

        GuiScreenInGame.INSTANCE.getGuiChatMessages().printMessage(packet.getMessage());
    }

    @EventHandler
    private void handleDisconnect(SPacketDisconnect packet) {
        connection.closeChannel(packet.getReason());
    }

    @EventHandler
    private void onDisconnect(ChannelInactiveEvent event) {
        if (Outskirts.getWorld() != null) { // world==null means initiative disconne...
            Outskirts.setWorld(null);

            Outskirts.closeAllScreen();
            Outskirts.startScreen(GuiScreenMainMenu.INSTANCE);
            Outskirts.startScreen(new GuiScreenDisconnect(connection.getTerminationReason()));
        }
    }
}
