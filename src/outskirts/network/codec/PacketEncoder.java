package outskirts.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import outskirts.network.Packet;
import outskirts.network.PacketBuffer;
import outskirts.util.logging.Log;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {

        int packetID = Packet.findPacketID(packet.getClass());

        out.writeInt(packetID);

        packet.write(new PacketBuffer(out));

    }
}
