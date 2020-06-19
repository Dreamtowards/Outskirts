package outskirts.util;

public enum Side {

    SERVER,
    CLIENT;

    public static Side CURRENT;

    public boolean isClient() {
        return this == CLIENT;
    }
}
