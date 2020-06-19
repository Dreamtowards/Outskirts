package outskirts.command;

public interface CommandSender {

    void sendMessage(String msg);

    default void sendMessage(String msg, Object... args) {
        sendMessage(String.format(msg, args));
    }

}
