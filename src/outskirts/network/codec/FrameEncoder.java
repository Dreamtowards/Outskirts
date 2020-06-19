package outskirts.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import outskirts.util.logging.Log;

public class FrameEncoder extends MessageToByteEncoder<ByteBuf> {

    /**
     * +---------------------+
     * |  data.length int32  |
     * +---------------------+
     * |                     |
     * |  data               |
     * |                     |
     * +---------------------+
     */

    // needs multi packets - size-threshold frame ..?
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {

        int frameLength = msg.readableBytes();
        out.writeInt(frameLength);
        out.writeBytes(msg);

        if (frameLength < 4)
        Log.warn("Send a %s len packet...", frameLength);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.warn("Outbound exception caught: %s", cause);

        cause.printStackTrace();
    }
}
