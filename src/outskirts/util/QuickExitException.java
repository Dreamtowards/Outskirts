package outskirts.util;

/**
 * use for pop method-stack
 */
public final class QuickExitException extends RuntimeException {

    public static final QuickExitException INSTANCE = new QuickExitException();

    private QuickExitException() {}

}
