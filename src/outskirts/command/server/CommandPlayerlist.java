package outskirts.command.server;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.server.OutskirtsServer;

public class CommandPlayerlist extends Command {

    public CommandPlayerlist() {
        setRegistryID("playerlist");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        sender.sendMessage("Online players: %s", OutskirtsServer.getOnlinePlayers().names());

    }
}
