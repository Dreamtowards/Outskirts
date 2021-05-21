package outskirts.util;

public final class Intptr {

    public int i;

    private Intptr(int i) {
        this.i = i;
    }

    public static Intptr of(int i) {
        return new Intptr(i);
    }

    public static Intptr zero() {
        return Intptr.of(0);
    }

}
