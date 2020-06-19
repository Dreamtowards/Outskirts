package outskirts.server.management;

import outskirts.entity.player.EntityPlayerMP;
import outskirts.network.Packet;
import outskirts.network.play.packet.SPacketChatMessage;
import outskirts.world.WorldServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class PlayerList implements Iterable<EntityPlayerMP> {

    private List<EntityPlayerMP> onlinePlayers = new ArrayList<>();
    private List<String> namescache = new ArrayList<>();

    public void add(EntityPlayerMP player) {
        onlinePlayers.add(player);
        namescache.add(player.getName());
    }

    public void remove(EntityPlayerMP player) {
        onlinePlayers.remove(player);
        namescache.remove(player.getName());
    }

    public EntityPlayerMP get(String playername) {
        for (EntityPlayerMP p : this) {
            if (p.getName().equals(playername))
                return p;
        }
        return null;
    }

    public List<String> names() {
        return namescache;
    }

    public final void sendBroadcast(String message) {
        sendPacket(new SPacketChatMessage(message));
    }

    public final void sendPacket(Packet packet) {
        for (EntityPlayerMP player : this) {
            player.connection.sendPacket(packet);
        }
    }

    @Override
    public Iterator<EntityPlayerMP> iterator() {
        return onlinePlayers.iterator();
    }
}
