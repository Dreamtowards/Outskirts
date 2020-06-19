package outskirts.command.server;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.server.OutskirtsServer;

import java.util.List;

public class CommandShutdown extends Command {

    public CommandShutdown() {
        setRegistryID("shutdown");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        OutskirtsServer.shutdown();
    }
}
