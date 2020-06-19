package ext.testing;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class CPacketStrMsg extends Packet {

    private String msg;

    public CPacketStrMsg() {}

    public CPacketStrMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(msg);
    }

    @Override
    public void read(PacketBuffer buf) {
        msg = buf.readString();
    }

    public String getMsg() {
        return msg;
    }
}
