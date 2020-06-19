package outskirts.server;

import java.io.File;

public class ServerSettings {

    public static int SERVER_PORT = 25585;


    public static final File DIR_SAVES = new File("saves");
    public static final File DIR_PLAYERDATA = new File(DIR_SAVES, ".playerdata");
    public static final String DEFAULT_WORLD = "world"; // saves/world

}
