package ext.ntty.pkt;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class CPacketCustomString extends Packet {

    private String content;

    public CPacketCustomString() {}

    public CPacketCustomString(String content) {
        this.content = content;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(content);
    }

    @Override
    public void read(PacketBuffer buf) {
        content = buf.readString();
    }

    public String getContent() {
        return content;
    }
}
