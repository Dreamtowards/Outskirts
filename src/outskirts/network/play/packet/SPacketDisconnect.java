package outskirts.network.play.packet;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

import java.io.IOException;

public class SPacketDisconnect extends Packet {

    private String reason;

    public SPacketDisconnect() {}

    public SPacketDisconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        reason = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(reason);
    }

    public String getReason() {
        return reason;
    }
}
