package outskirts.util;

public final class Validate {

    public static void isTrue(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static <T> T notNull(T object, String message, Object... args) {
        if (object == null) {
            throw new NullPointerException(String.format(message, args));
        } else {
            return object;
        }
    }

    public static void validState(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(message, args));
        }
    }

}
