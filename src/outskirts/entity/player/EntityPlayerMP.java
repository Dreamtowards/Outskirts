package outskirts.entity.player;

import outskirts.network.play.packet.SPacketDisconnect;
import outskirts.server.OutskirtsServer;
import outskirts.storage.dst.DObject;
import outskirts.world.WorldServer;

import java.io.IOException;

public class EntityPlayerMP extends EntityPlayer {

    private String uuid;

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void onRead(DObject mp) throws IOException {

        if (mp.containsKey("world"))
            setWorld(OutskirtsServer.getWorlds().get(mp.getString("world")));  // probably null. when saves curr not exists

        super.onRead(mp);
    }

    @Override
    public DObject onWrite(DObject mp) throws IOException {

        if (getWorld() != null)  // when is null ..?
            mp.put("world", getWorld().getRegistryID());

        return super.onWrite(mp);
    }

    @Override
    public WorldServer getWorld() {
        return (WorldServer)super.getWorld();
    }

    public final void kickPlayer(String reason) {
        connection.sendPacket(new SPacketDisconnect(reason));
    }
}
