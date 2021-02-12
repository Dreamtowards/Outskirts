package outskirts.command.client;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.entity.player.EntityPlayer;
import outskirts.entity.player.Gamemode;
import outskirts.server.OutskirtsServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandGamemode extends Command {

    public CommandGamemode() {
        setRegistryID("gamemode");
        getUsages().add("/gamemode <survival|creative|spectator> [username]");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer)sender;
        Gamemode gmm = Gamemode.valueOf(args[0].toUpperCase());


        player.setGamemode(gmm);

        player.sendMessage("Ok. Gamemode: "+gmm);
    }

    @Override
    public List<String> tabComplete(int argi) {
        if (argi == 0) {
            return Stream.of(Gamemode.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
        }
        return null;
    }
}
