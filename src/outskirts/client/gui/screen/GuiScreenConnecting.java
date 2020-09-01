package outskirts.client.gui.screen;

import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.network.ChannelHandler;
import outskirts.network.Packet;
import outskirts.network.login.PacketHandlerLoginClient;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.util.Colors;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;

import static outskirts.util.logging.Log.LOGGER;

/**
 * Connecting to server
 */
public class GuiScreenConnecting extends GuiScreen {

    private String statusMessage = "Initializing...";

    // Constructor always means "Allocate", but there are some heavy operations. then uses static method.
    private GuiScreenConnecting(String hostname, int port) throws InterruptedException {

        new Thread(() -> {
            try {
                Log.info("Connecting to %s:%s...", hostname, port);

                setStatusMessage("Connecting to server...");
                ChannelHandler conn = ChannelHandler.createConnection(hostname, port);
                conn.eventBus().register(new PacketHandlerLoginClient(conn));

                setStatusMessage("Logging in...");
                conn.sendPacket(new CPacketLogin(ClientSettings.ProgramArguments.UUID, ClientSettings.ProgramArguments.TOKEN, Packet.PROTOCOL_DIGEST));

            } catch (Throwable t) {

                Outskirts.getRootGUI().removeGui(this);
                Outskirts.getRootGUI().addGui(new GuiScreenDisconnect(String.format("Failed to connect.\n%s: %s", t.getClass().getName(), t.getMessage())));

                LOGGER.warn(t);
            }
        }, "Connector").start();

        addOnDrawListener(e -> {

            drawString(statusMessage, Outskirts.getWidth()/2f, Outskirts.getHeight()/3f, Colors.WHITE);
        });

    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @param host hostname:port / hostname[:25580]
     */
    public static GuiScreenConnecting connect(String host) {
        try {
            String hostname = host;
            int port = 25585;
            if (host.contains(":")) {
                String[] b = StringUtils.explode(host, ":");
                hostname = b[0];
                port = Integer.parseInt(b[1]);
            }
            return new GuiScreenConnecting(hostname, port);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Failed to create connection...", ex);
        }
    }
}
