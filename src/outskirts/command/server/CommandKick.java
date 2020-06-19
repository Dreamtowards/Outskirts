package outskirts.command.server;

import outskirts.command.Command;
import outskirts.command.CommandException;
import outskirts.command.CommandSender;
import outskirts.command.WrongUsageException;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.server.OutskirtsServer;

public class CommandKick extends Command {

    public CommandKick() {
        setRegistryID("kick");
        getUsages().add("/kick <player> [reason]");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1)
            throw new WrongUsageException();

        EntityPlayerMP player = OutskirtsServer.getOnlinePlayers().get(args[0]);
        if (player == null)
            throw new CommandException("Player %s not exists.", args[0]);

        String reason = "undefined reason.";
        if (args.length > 1)
            reason = args[1];

        player.kickPlayer(reason);
    }
}
