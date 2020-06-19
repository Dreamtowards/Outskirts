package ext.ntty.pkt;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class SPacketKeepAlive extends Packet {

    public SPacketKeepAlive() {}

    @Override
    public void write(PacketBuffer buf) {
        //just none
    }

    @Override
    public void read(PacketBuffer buf) {

    }
}
