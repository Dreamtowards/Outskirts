package outskirts.util.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import outskirts.util.HttpUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequestWrapper {

    private FullHttpRequest req;
    private String bodystrcache; // cache
    private Map<String, String> params = new HashMap<>();

    HttpRequestWrapper(FullHttpRequest req, Map<String, String> uriExpParams) {
        this.req = req;

        if (uriExpParams != null) {
            params.putAll(uriExpParams);
        } else {
            if (method() == HttpMethod.GET && req.getUri().contains("?")) {
                params.putAll(HttpUtils.parseParams(req.getUri()));
            } else if (method() == HttpMethod.POST) {
                try {
                    params.putAll(HttpUtils.parseParams(bodyString()));
                } catch (Exception ex) { }
            }
        }
    }

    /**
     * parsed params. URIExpressionParams or GET/POST-Params
     */
    public Map<String, String> params() {
        return params;
    }

    public HttpMethod method() {
        return req.getMethod();
    }

    public String uri() {
        return req.getUri();
    }

    public HttpVersion protocol() {
        return req.getProtocolVersion();
    }

    public HttpHeaders headers() {
        return req.headers();
    }

    public String bodyString() {
        if (bodystrcache == null) {
            bodystrcache = body().toString(StandardCharsets.UTF_8);
        }
        return bodystrcache;
    }

    public ByteBuf body() {
        return req.content();
    }
}
