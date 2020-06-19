package outskirts.network.login.packet;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

public class SPacketDisconnect extends Packet {

    private String reason;

    public SPacketDisconnect() {}

    public SPacketDisconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void read(PacketBuffer buf) {
        reason = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(reason);
    }

    public String getReason() {
        return reason;
    }
}
