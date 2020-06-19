package outskirts.command.server;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.server.OutskirtsServer;

public class CommandSay extends Command {

    public CommandSay() {
        setRegistryID("say");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        OutskirtsServer.getOnlinePlayers().sendBroadcast("[Server]: " + args[0]);

    }
}
