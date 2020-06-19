package outskirts.network.play.packet;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

import java.io.IOException;

public class SPacketTerrainUnload extends Packet {

    private long posLong;

    public SPacketTerrainUnload() {}

    public SPacketTerrainUnload(long posLong) {
        this.posLong = posLong;
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        posLong = buf.readLong();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeLong(posLong);
    }

    public long getPosLong() {
        return posLong;
    }
}
