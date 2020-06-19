package outskirts.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import outskirts.network.Packet;
import outskirts.network.PacketBuffer;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() == 0) // channelInactive() ctx.fireOutbound(EMPTY_BUFFER)
            return;

        int packetID = in.readInt();

        Packet packet = Packet.createPacket(packetID);

        packet.read(new PacketBuffer(in));

        if (in.readableBytes() > 0) {
            throw new IllegalStateException("Packet had not read fully.");
        }

        out.add(packet);
    }
}
