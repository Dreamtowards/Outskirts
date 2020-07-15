package outskirts.network.play;

import outskirts.client.Outskirts;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.event.EventHandler;
import outskirts.event.conn.ChannelInactiveEvent;
import outskirts.network.ChannelHandler;
import outskirts.server.OutskirtsServer;
import outskirts.util.logging.Log;

public class PacketHandlerPlayServer {

    private ChannelHandler connection;
    private EntityPlayerMP player;

    public PacketHandlerPlayServer(EntityPlayerMP player) {
        this.connection = player.connection;
        this.player = player;
    }



    @EventHandler
    private void onDisconnect(ChannelInactiveEvent event) {
        Log.info("Player %s disconnected.", player.getName());

        // logoutPlayer(player)
        OutskirtsServer.getPlayerManager().savePlayer(player);

        OutskirtsServer.getOnlinePlayers().remove(player);

//        player.getWorld().getTerrains().forEach(terr -> terr.listeningPlayers.remove(player));
        player.getWorld().removeEntity(player);


        OutskirtsServer.getOnlinePlayers().sendBroadcast(String.format("%s logged out.", player.getName()));
    }
}
