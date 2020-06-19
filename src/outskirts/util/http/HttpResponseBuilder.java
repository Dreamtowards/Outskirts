package outskirts.util.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class HttpResponseBuilder {

    private FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.buffer());
    private String enctype = "text/html";

    public HttpResponseBuilder protocol(HttpVersion protocol) {
        resp.setProtocolVersion(protocol);
        return this;
    }

    public HttpResponseBuilder status(HttpResponseStatus status) {
        resp.setStatus(status);
        return this;
    }

    public HttpHeaders headers() {
        return resp.headers();
    }
    public final HttpResponseBuilder enctype(String mime) {
        this.enctype = mime;
        return this;
    }

    public HttpResponseBuilder body(byte[] bytes) {
        resp.content().ensureWritable(bytes.length);
        resp.content().writeBytes(bytes);
        return this;
    }
    public final HttpResponseBuilder body(String str) {
        return body(str.getBytes(StandardCharsets.UTF_8));
    }

    FullHttpResponse build() {
        setHeaderIfNotExists("Date", new Date());
        setHeaderIfNotExists("Content-Length", resp.content().readableBytes());
        setHeaderIfNotExists("Content-Type", enctype + "; charset=utf-8");
        return resp;
    }

    private void setHeaderIfNotExists(String name, Object value) {
        if (!headers().contains(name)) {
            headers().set(name, value);
        }
    }
}
