package outskirts.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import outskirts.util.logging.Log;

import java.util.List;

public class FrameDecoder extends ByteToMessageDecoder {

    /**
     * approximately concept.
     *
     * in = cumulation;
     * while (in.isReadable()) {
     *     decode(ctx, in, out)
     * }
     * for (Object obj : out) {
     *     ctx.fireChannelRead(obj);
     * }
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < 4) { // not enough a Integer (valid frame length
            return;
        }

        in.markReaderIndex();

        int frameLength = in.readInt();
        if (in.readableBytes() < frameLength) { // not a completed frame
            in.resetReaderIndex();
            return;
        }

        if (frameLength<4)
            Log.warn("rec %s len frame", frameLength);

        out.add( in.readBytes(frameLength) );
    }
}
