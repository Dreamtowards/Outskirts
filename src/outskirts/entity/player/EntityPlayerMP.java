package outskirts.entity.player;

import outskirts.network.play.packet.SPacketDisconnect;
import outskirts.server.OutskirtsServer;
import outskirts.server.ServerSettings;
import outskirts.util.Validate;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.world.World;
import outskirts.world.WorldServer;
import outskirts.world.terrain.Terrain;

import java.util.Map;

public class EntityPlayerMP extends EntityPlayer {

    private int viewDistance = 2 * Terrain.SIZE;

    private String uuid;

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    @Override
    public void onRead(Map mp) {

        if (mp.containsKey("world"))
            setWorld(OutskirtsServer.getWorlds().get((String)mp.get("world")));  // probably null. when saves curr not exists

        super.onRead(mp);
    }

    @Override
    public void onWrite(Map mp) {

        if (getWorld() != null)  // when is null ..?
            mp.put("world", getWorld().getRegistryID());

        super.onWrite(mp);
    }

    @Override
    public WorldServer getWorld() {
        return (WorldServer)super.getWorld();
    }

    public final void kickPlayer(String reason) {
        connection.sendPacket(new SPacketDisconnect(reason));
    }
}
