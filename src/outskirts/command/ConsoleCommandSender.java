package outskirts.command;

import outskirts.util.logging.Log;

public class ConsoleCommandSender implements CommandSender {
    @Override
    public void sendMessage(String msg) {
        Log.info(msg);
    }
}
