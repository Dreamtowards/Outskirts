package outskirts.command.client;

import outskirts.command.Command;
import outskirts.command.CommandSender;
import outskirts.entity.player.EntityPlayer;
import outskirts.util.vector.Vector3f;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public class CommandTeleport extends Command {

    public CommandTeleport() {
        setRegistryID("tp");
        getUsages().add("/tp <x> <y> <z>");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        EntityPlayer targetplayer = (EntityPlayer)sender;
        Vector3f pos = vec3(args, 0);

        targetplayer.position().set(pos);

        targetplayer.sendMessage("Ok. teleported.");
    }
}
