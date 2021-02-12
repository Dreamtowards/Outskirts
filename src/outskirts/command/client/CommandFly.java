package outskirts.command.client;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.List;

public class CommandFly extends Command {

    private static final String MODE_ON = "on";
    private static final String MODE_OFF = "off";

    public CommandFly() {
        setRegistryID("fly");
        getUsages().add("/fly [on/off] [username]");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer)sender;
        boolean fly = !player.isFlymode();

        if (args.length >= 1) {
            String s = args[0]; assert s.equals(MODE_ON) || s.equals(MODE_OFF);
            fly = s.equals(MODE_ON);
        }

        player.setFlymode(fly);

        player.sendMessage("Ok. flymode: " + fly);
    }

    @Override
    public List<String> tabComplete(int argi) {
        if (argi==0) {
            return Arrays.asList(MODE_ON, MODE_OFF);
        }
        return null;
    }
}
