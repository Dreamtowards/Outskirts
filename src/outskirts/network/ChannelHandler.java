package outskirts.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import outskirts.event.EventBus;
import outskirts.event.conn.ChannelInactiveEvent;
import outskirts.network.codec.FrameDecoder;
import outskirts.network.codec.FrameEncoder;
import outskirts.network.codec.PacketDecoder;
import outskirts.network.codec.PacketEncoder;
import outskirts.util.CopyOnIterateArrayList;
import outskirts.util.Side;
import outskirts.util.logging.Log;

import java.util.List;
import java.util.function.Consumer;

public class ChannelHandler extends SimpleChannelInboundHandler<Packet> {

    private Side side;

    private Channel channel;

    /**
     * Packet EventBus in this Channel
     */
    private EventBus eventBus = new EventBus().listFactory(CopyOnIterateArrayList::new);

    private String terminationReason = "Unknwon Reason.";

    public ChannelHandler(Side side) {
        this.side = side;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.warn("Inbound exception caught: %s", cause);

        cause.printStackTrace();

        closeChannel("Inbound exception caught: %s" + cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        eventBus.post(packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        eventBus.post(new ChannelInactiveEvent());
    }

    public ChannelFuture sendPacket(Packet packet) {
        return channel.writeAndFlush(packet);
    }

    public boolean isChannelOpen() {
        return channel != null && channel.isOpen();
    }

    public void closeChannel(String reason) {
        if (isChannelOpen()) {
            terminationReason = reason;
            channel.close().syncUninterruptibly();
        }
    }

    public Channel channel() {
        return channel;
    }

    public EventBus eventBus() {
        return eventBus;
    }

    public String getTerminationReason() {
        return terminationReason;
    }






    @SuppressWarnings("all")
    public static void bindServerEndpoint(int port, Consumer<ChannelHandler> connInit) throws InterruptedException {
        EventLoopGroup SERVER_NIO_EVENTLOOP = new NioEventLoopGroup(0, new DefaultThreadFactory("Netty Server IO #", true));

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(SERVER_NIO_EVENTLOOP, SERVER_NIO_EVENTLOOP)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true) //SO_KEEPALIVE, AUTO_READ, TCP_NODELAY
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelHandler channelHandler = new ChannelHandler(Side.SERVER);
                        channel.pipeline()
                                .addLast("frame_decoder", new FrameDecoder())
                                .addLast("frame_encoder", new FrameEncoder())
                                .addLast("packet_decoder", new PacketDecoder())
                                .addLast("packet_encoder", new PacketEncoder())
                                .addLast("handler", channelHandler);

                        connInit.accept(channelHandler);
                    }
                });

        bootstrap.bind(port).sync();
    }

    @SuppressWarnings("all")
    public static ChannelHandler createConnection(String host, int port) throws InterruptedException {
        EventLoopGroup CLIENT_NIO_EVENTLOOP = new NioEventLoopGroup(0, new DefaultThreadFactory("Netty Client IO #", true));

        ChannelHandler channelHandler = new ChannelHandler(Side.CLIENT);

        Bootstrap bootstrap = new Bootstrap()
                .group(CLIENT_NIO_EVENTLOOP)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("frame_decoder", new FrameDecoder())
                                .addLast("frame_encoder", new FrameEncoder())
                                .addLast("packet_decoder", new PacketDecoder())
                                .addLast("packet_encoder", new PacketEncoder())
                                .addLast("handler", channelHandler);
                    }
                });

        bootstrap.connect(host, port).sync();

        // sync until channel open
        while (!channelHandler.isChannelOpen()) {
            Thread.sleep(1);
        }

        return channelHandler;
    }

}
