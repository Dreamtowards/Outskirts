package outskirts.util;

public enum Side {

    SERVER,
    CLIENT;

    /**
     * Only set by Offical Mains.
     */
    public static Side CURRENT;

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isServer() {
        return !isClient();
    }
}
