package outskirts.command;

public class CommandException extends RuntimeException {

    public CommandException(String message, String... args) {
        super(String.format(message, args));
    }

}
