package outskirts.init;

import outskirts.command.Command;
import outskirts.command.client.CommandFly;
import outskirts.command.client.CommandGamemode;
import outskirts.command.client.CommandSay;
import outskirts.command.client.CommandTeleport;

public final class Commands {

    public static final Command GAMEMODE = register(new CommandGamemode());
    public static final Command FLY = register(new CommandFly());
    public static final Command SAY = register(new CommandSay());
    public static final Command TP = register(new CommandTeleport());


    private static Command register(Command command) {
        return Command.REGISTRY.register(command);
    }

    static void init() {}
}
