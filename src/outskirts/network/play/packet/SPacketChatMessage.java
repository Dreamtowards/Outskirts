package outskirts.network.play.packet;

import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

import java.io.IOException;

public class SPacketChatMessage extends Packet {

    private String message;

    public SPacketChatMessage() {}

    public SPacketChatMessage(String message) {
        this.message = message;
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        message = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(message);
    }

    public String getMessage() {
        return message;
    }
}
