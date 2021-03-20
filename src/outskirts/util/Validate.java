package outskirts.util;

public final class Validate {

    public static void isTrue(boolean exp, String msg, Object... args) {
        if (!exp) {
            throw new IllegalArgumentException(String.format(msg, args));
        }
    }
    public static void isTrue(boolean exp) {
        if (!exp) {
            throw new IllegalArgumentException();
        }
    }


    public static <T> T notNull(T object, String msg, Object... args) {
        if (object == null) {
            throw new NullPointerException(String.format(msg, args));
        } else {
            return object;
        }
    }

    public static void validState(boolean exp, String msg, Object... args) {
        if (!exp) {
            throw new IllegalStateException(String.format(msg, args));
        }
    }

}
