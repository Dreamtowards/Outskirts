package outskirts.command;

import static outskirts.util.logging.Log.LOGGER;

public class ConsoleCommandSender implements CommandSender {

    @Override
    public void sendMessage(String msg) {
        LOGGER.info(msg);
    }
}
