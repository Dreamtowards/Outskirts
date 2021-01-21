package outskirts.network.login;

import outskirts.entity.player.EntityPlayerMP;
import outskirts.event.EventHandler;
import outskirts.network.ChannelHandler;
import outskirts.network.Packet;
import outskirts.network.login.packet.SPacketDisconnect;
import outskirts.network.login.packet.CPacketLogin;
import outskirts.network.login.packet.SPacketLoginSuccess;
import outskirts.network.play.PacketHandlerPlayServer;
import outskirts.server.OutskirtsServer;
import outskirts.util.logging.Log;

import java.io.IOException;

public class PacketHandlerLoginServer {

    private ChannelHandler connection;

    public PacketHandlerLoginServer(ChannelHandler conn) {
        this.connection = conn;
    }

    @EventHandler(scheduler = OutskirtsServer.class) // cuz switch PacketHandler
    private void processLogin(CPacketLogin packet) throws IOException {

        if (packet.getProtocol() != Packet.PROTOCOL_DIGEST) {
            refuseConnection("unexcepted protocol.");
            return;
        }
        if (!packet.getUUID().equals("0cfaee92-5103-4176-b469-5d3dfb6064c0") ||
                !packet.getToken().equals("a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8")) { // wrong token
            refuseConnection("Wrong token");
            return;
        }

        EntityPlayerMP player = OutskirtsServer.getPlayerManager().loadPlayer(connection, packet.getUUID(), "TheUser01");

        // switch handler
        connection.eventBus().unregister(this);
        connection.eventBus().register(new PacketHandlerPlayServer(player));

        connection.sendPacket(new SPacketLoginSuccess());

        Log.info("Player %s[%s]{%s, %s} logged in.", player.getName(),
                connection.channel().remoteAddress(), player.getWorld().getRegistryID(), player.position());

        OutskirtsServer.getOnlinePlayers().sendBroadcast(String.format("%s logged in.", player.getName()));
    }

    private void refuseConnection(String reason) {
        connection.sendPacket(new SPacketDisconnect(reason));
        connection.closeChannel(reason);
    }
}
