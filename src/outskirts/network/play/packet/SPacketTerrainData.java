package outskirts.network.play.packet;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;
import outskirts.util.nbt.NBTTagCompound;
import outskirts.world.terrain.Terrain;

import java.io.IOException;

public class SPacketTerrainData extends Packet {

    private Long posLong;
    private NBTTagCompound tagCompound;

    public SPacketTerrainData() {}

    public SPacketTerrainData(Terrain terrain) {
        this.posLong = Terrain.posLong(terrain.x, terrain.z);
        this.tagCompound = terrain.writeNBT(new NBTTagCompound());
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        posLong = buf.readLong();
        tagCompound = buf.readNBT();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeLong(posLong);
        buf.writeNBT(tagCompound);
    }

    public Long getPosLong() {
        return posLong;
    }

    public NBTTagCompound getTagCompound() {
        return tagCompound;
    }
}
