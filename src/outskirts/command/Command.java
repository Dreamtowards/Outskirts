package outskirts.command;

import outskirts.util.*;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;
import outskirts.util.vector.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Command implements Registrable {

    public static final Registry<Command> REGISTRY = new Registry<>();

    private String registryID;
    private List<String> usages = new ArrayList<>();

    public abstract void onCommand(CommandSender sender, String[] args);

    /**
     * @return nullable. null, means unsupported.
     */
    public List<String> tabComplete(int argi) {
        return null;
    }

    public final List<String> getUsages() {
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
        String[] bounds = StringUtils.explode(commandline.substring(1), " "); // bounds
        String cmdid = new Identifier(bounds[0]).toString(); // there are probably not very good.. for other domains

        Command command = REGISTRY.get(cmdid);

        if (command == null) {
            sender.sendMessage("Command \"%s\" not found.", cmdid);
            return;
        }

        try
        {
            command.onCommand(sender, CollectionUtils.subarray(bounds, 1));
        }
        catch (WrongUsageException ex) {
            sender.sendMessage("Wrong command usage. Usages: \n%s", CollectionUtils.toString(command.getUsages(), "\n"));
        } catch (CommandException ex) {
            sender.sendMessage(ex.getMessage());
        } catch (Throwable t) {
            sender.sendMessage("An exception occurred on the command execution. (" + t);
        }
    }

    public static List<String> dispatchTabComplete(String commandline, int cursorpos) {
        String[] bound = StringUtils.explode(commandline.substring(1, cursorpos), " ");
        String command = new Identifier(bound[0]).toString();
        int argi = bound.length-2;
        String start = bound[bound.length-1];

        if (argi == -1) {
            return filterStartSwith(REGISTRY.keys(), command);
        } else {
            Command cmd = Command.REGISTRY.get(new Identifier(command).toString());
            if (cmd == null)
                return Collections.emptyList();
            return filterStartSwith(CollectionUtils.orDefault(cmd.tabComplete(argi), Collections.emptyList()), start);
        }
    }
    private static List<String> filterStartSwith(List<String> ls, String start) {
        List<String> l = new ArrayList<>();
        for (String s : ls) {
            if (s.startsWith(start)) l.add(s);
        }
        return l;
    }

}
