package outskirts.command.server;

import outskirts.command.Command;
import outskirts.command.CommandException;
import outskirts.command.CommandSender;
import outskirts.command.WrongUsageException;
import outskirts.entity.Entity;
import outskirts.entity.player.EntityPlayerMP;
import outskirts.util.ResourceLocation;

import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;

public class CommandSummon extends Command {

    public CommandSummon() {
        setRegistryID("summon");
        getUsages().add("/summon <entity.registryID> [x,y,z]");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1)
            throw new WrongUsageException();
        if (!(sender instanceof EntityPlayerMP))
            throw new CommandException("This command requires player to perform."); // for determine entity.world

        String registryID = new ResourceLocation(args[0]).toString();

        if (!Entity.REGISTRY.containsKey(registryID))
            throw new CommandException("Entity registryID \""+registryID+"\" not found.");

        Entity entity = Entity.createEntity(registryID);

        if (args.length >= 2)
            entity.position().set(vec3(args[1]));

        ((EntityPlayerMP)entity).getWorld().addEntity(entity);

        sender.sendMessage("Summoned %s in %s", entity.getRegistryID(), entity.position());
    }
}
