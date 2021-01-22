package ext.etc;

import org.junit.Test;
import outskirts.util.http.HttpServer;
import outskirts.util.logging.Log;

import javax.net.ssl.SSLException;

public class TestHTTPServer {

    @Test
    public void starthttpsv() throws SSLException, InterruptedException {

        HttpServer server = HttpServer.bindHttpServerEndpoint(8123);

        server.register("/auth/{username}", (req, resp) -> {
            Log.info(req.uri());

            resp.body("username: " + req.params().get("username"));
        });

        while (true)
            Thread.sleep(10);
    }

}
