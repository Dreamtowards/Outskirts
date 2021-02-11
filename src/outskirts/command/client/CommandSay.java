package outskirts.command.client;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.server.OutskirtsServer;

public class CommandSay extends Command {

    public CommandSay() {
        setRegistryID("say");
        getUsages().add("/say <message>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        String message = "[Server]: " + args[0];

//        OutskirtsServer.getOnlinePlayers().sendBroadcast(message);
        sender.sendMessage(message);
    }
}
