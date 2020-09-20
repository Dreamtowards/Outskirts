package outskirts.util;

import outskirts.util.logging.Log;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class HttpUtils {

    private static int DEFAULT_TIMEOUT = 10_000;

    public static String httpGet(String url, Map<String, String> headers) throws IOException {
        return request(openConnection("GET", url), null, headers);
    }
    public static String httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    public static String httpPost(String url, byte[] body, Map<String, String> headers) throws IOException {
        return request(openConnection("POST", url), body == null ? null : new ByteArrayInputStream(body), headers);
    }
    public static String httpPost(String url, byte[] body) throws IOException {
        return httpPost(url, body, null);
    }

    public static String request(HttpURLConnection connection, @Nullable InputStream body, @Nullable Map<String, String> headers) throws IOException {

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (body != null) {
            connection.setDoOutput(true);
            IOUtils.write(body, connection.getOutputStream());
            IOUtils.closeQuietly(connection.getOutputStream());
        } else {
            connection.setDoOutput(false);
        }

        connection.connect();

        // Redirection
        int code = connection.getResponseCode();
        if (code >= 300 && code < 400) {
            String location = connection.getHeaderField("Location");
            Log.warn("Redirection %s to URL: %s", code, location);
            return request(openConnection(connection.getRequestMethod(), location), body, headers);
        }

        // Read Body
        InputStream is = connection.getInputStream();
        String encoding = connection.getHeaderField("Content-Encoding");
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            is = new GZIPInputStream(is);
        }
        String result = IOUtils.toString(is);
        IOUtils.closeQuietly(is);

        return result;
    }

    public static HttpURLConnection openConnection(String method, String url, int timeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setRequestMethod(method);
        return connection;
    }

    public static HttpURLConnection openConnection(String method, String url) throws IOException {
        return openConnection(method, url, DEFAULT_TIMEOUT);
    }

    /**
     * @param s allowed examples:
     * http://example.com/?a=123&b=123
     * ?a=123&b=123
     * a=123
     */
    public static Map<String, String> parseParams(String s) {
        try {
            Map<String, String> result = new HashMap<>();
            if (s.contains("?"))
                s = s.substring(s.indexOf('?')+1);
            for (String arg : StringUtils.explode(s, "&")) {
                if (arg.contains("%"))
                    arg = URLDecoder.decode(arg, "utf-8");
                int eq = arg.indexOf('=');
                if (eq == -1) {
                    result.put(arg, null);
                } else {
                    result.put(arg.substring(0, eq), arg.substring(eq+1));
                }
            }
            return result;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Failed to parse params.", ex);
        }
    }

    public static String toURLEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static String fromURLEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
