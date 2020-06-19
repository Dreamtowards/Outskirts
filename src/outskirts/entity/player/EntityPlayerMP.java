package outskirts.entity.player;

import outskirts.network.play.packet.SPacketDisconnect;
import outskirts.server.OutskirtsServer;
import outskirts.server.ServerSettings;
import outskirts.util.Validate;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.world.World;
import outskirts.world.WorldServer;
import outskirts.world.terrain.Terrain;

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
    public void readNBT(NBTTagCompound tagCompound) {

        if (tagCompound.hasKey("world"))
            setWorld(OutskirtsServer.getWorlds().get(tagCompound.getString("world"))); // probably null. when saves curr not exists

        super.readNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound tagCompound) {

        if (getWorld() != null) // when is null ..?
            tagCompound.setString("world", getWorld().getRegistryID());

        return super.writeNBT(tagCompound);
    }

    @Override
    public WorldServer getWorld() {
        return (WorldServer)super.getWorld();
    }

    public final void kickPlayer(String reason) {
        connection.sendPacket(new SPacketDisconnect(reason));
    }
}
