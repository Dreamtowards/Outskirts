package ext.ntty.pkt;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class SPacketTMPRedPrint extends Packet {

    private String printstuff;

    public SPacketTMPRedPrint() {}

    public SPacketTMPRedPrint(String printstuff) {
        this.printstuff = printstuff;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(printstuff);
    }

    @Override
    public void read(PacketBuffer buf) {
        printstuff = buf.readString();
    }

    public String getPrintstuff() {
        return printstuff;
    }
}
