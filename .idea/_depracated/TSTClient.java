import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import outskirts.util.logging.Log;

public class TSTClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new Handler());
                    }
                });

        ChannelFuture f = b.connect("127.0.0.1", 1050).sync();

        f.channel().closeFuture().sync();

        group.shutdownGracefully();
    }

    private static class Handler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            System.out.println("Client conn");
            String s = "some data from client.";
            ByteBuf buf = Unpooled.buffer(s.length());
            buf.writeBytes(s.getBytes());
            ctx.writeAndFlush(buf);

            super.channelActive(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf buf) throws Exception {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            Log.info("Client: "+new String(bytes));
        }
    }

}
