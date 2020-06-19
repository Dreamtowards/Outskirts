package outskirts.util.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultThreadFactory;
import outskirts.util.logging.Log;

import javax.net.ssl.SSLException;
import java.util.*;
import java.util.function.BiConsumer;

@ChannelHandler.Sharable
public class HttpServer extends SimpleChannelInboundHandler<FullHttpRequest> {

    private List<Handler> handlers = new ArrayList<>();

    private HttpServer() {}

    public void register(String uriexp, BiConsumer<HttpRequestWrapper, HttpResponseBuilder> handler) {
        handlers.add(new Handler(uriexp, handler));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();

        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        HttpResponseBuilder respBdr = new HttpResponseBuilder();

        Handler handler = findHandler(req.getUri());

        if (handler != null) {
            handler.processor.accept(new HttpRequestWrapper(req, handler.lastExpParams), respBdr);
        } else {
            respBdr.status(HttpResponseStatus.NOT_FOUND);
        }

        ctx.channel().writeAndFlush(respBdr.build());
    }

    private Handler findHandler(String uri) {
        int paramb = uri.indexOf('?');
        if (paramb != -1)
            uri = uri.substring(0, paramb);
        for (Handler h : handlers) {
            if (h.hasExp) {
                if ((h.lastExpParams=parseExpression(uri, h.uriexp))!=null) {
                    return h;
                }
            } else {
                if (h.uriexp.hashCode() == uri.hashCode() && h.uriexp.equals(uri)) {
                    return h;
                }
            }
        }
        return null;
    }

    private static Map<String, String> parseExpression(String uri, String exp) {
        Map<String, String> map = null;
        int expptr = 0, uriptr = 0;
        while (expptr < exp.length() && uriptr < uri.length()) {
            if (exp.charAt(expptr) == '{') {
                int s = ++expptr; // start param name
                while (exp.charAt(expptr) != '}')
                    expptr++;
                String nm = exp.substring(s, expptr); // param name
                int nextp = exp.indexOf('{', expptr);
                String suff = exp.substring(++expptr, nextp == -1 ? exp.length() : nextp);

                int m = uri.indexOf(suff, uriptr); // match uri param end
                if (m == -1)
                    return null;
                if (m == uriptr)
                    m = uri.length();
                if (map == null)
                    map = new HashMap<>();
                map.put(nm, uri.substring(uriptr, m));
                uriptr = m;
            } else {
                if (uri.charAt(uriptr++) != exp.charAt(expptr++))
                    return null;
            }
        }
        return map == null ? Collections.emptyMap() : map;
    }





    public static HttpServer bindHttpServerEndpoint(int port) throws InterruptedException, SSLException {

        EventLoopGroup EVENTLOOP = new NioEventLoopGroup(0, new DefaultThreadFactory("Http Server IO #", true));

        HttpServer httpServerHandler = new HttpServer();

        SslContext sslctx = null;//SslContext.newServerContext(new File("cert.pem"), new File("cpriv.key"));

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(EVENTLOOP, EVENTLOOP)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true) //SO_KEEPALIVE, AUTO_READ, TCP_NODELAY
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        if (sslctx != null) {
                            channel.pipeline().addLast("ssl_layer", sslctx.newHandler(channel.alloc()));
                        }
                        channel.pipeline()
                                .addLast("http_decoder", new HttpRequestDecoder())
                                .addLast("http_encoder", new HttpResponseEncoder())
                                .addLast("http_aggregator", new HttpObjectAggregator(1024 * 512))
                                .addLast("handler", httpServerHandler);
                    }
                });

        bootstrap.bind(port).sync();

        return httpServerHandler;
    }

    private static final class Handler {

        private Handler(String uriexp, BiConsumer processor) {
            this.uriexp = uriexp;
            this.processor = processor;
            this.hasExp = uriexp.contains("{");
        }

        private String uriexp;
        private boolean hasExp;
        private Map<String, String> lastExpParams;
        private BiConsumer<HttpRequestWrapper, HttpResponseBuilder> processor;

    }
}
