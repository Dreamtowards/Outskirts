package outskirts.command;

import outskirts.util.CollectionUtils;
import outskirts.util.ResourceLocation;
import outskirts.util.StringUtils;
import outskirts.util.Validate;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class Command implements Registrable {

    public static final Registry<Command> REGISTRY = new Registry<>();

    private String registryID;
    private List<String> usages = new ArrayList<>();

    public abstract void onCommand(CommandSender sender, String[] args);

    public List<String> tabComplete(String[] args) {
        return Collections.emptyList();
    }

    public List<String> getUsages() {
        return usages;
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }

    /**
     * @param commandline "cmdid args[0] args[1]..."
     */
    public static void dispatchCommand(CommandSender sender, String commandline) {
        String[] bounds = StringUtils.explode(commandline, " "); // bounds
        String cmdid = new ResourceLocation(bounds[0]).toString(); // there are probably not very good.. for other domains

        Command command = REGISTRY.get(cmdid);

        if (command == null) {
            sender.sendMessage("Command \""+cmdid+"\" not found.");
            return;
        }

        try
        {
            command.onCommand(sender, CollectionUtils.subarray(bounds, 1));
        }
        catch (WrongUsageException ex)
        {
            sender.sendMessage("Wrong command usage. Usages: \n" + CollectionUtils.toString(command.getUsages(), "\n"));
        }
        catch (CommandException ex)
        {
            sender.sendMessage(ex.getMessage());
        }
        catch (Throwable t)
        {
            sender.sendMessage("An exception occurred on the command execution. (" + t);
        }
    }

    public static List<String> dispatchTabComplete(String commandline) {
        String[] bounds = StringUtils.explode(commandline, " ");
        String cmdid = new ResourceLocation(bounds[0]).toString();

        if (bounds.length == 1) { // complete cmdid
            List<String> result = new ArrayList<>();
            for (String k : REGISTRY.keys()) {
                if (k.startsWith(cmdid))
                    result.add(k);
            }
            return result;
        } else if (bounds.length > 1) { // complete args
            Command command = REGISTRY.get(cmdid);

            if (command != null) {
                return command.tabComplete(CollectionUtils.subarray(bounds, 1));
            }
        }

        return Collections.emptyList();
    }

    protected static Vector3f parseVector3f(String s) {
        String[] b = StringUtils.explode(s, ",");
        if (b.length != 3)
            throw new CommandException("Wrong vector3f format.");
        return new Vector3f(Float.parseFloat(b[0]), Float.parseFloat(b[1]), Float.parseFloat(b[2]));
    }
}
