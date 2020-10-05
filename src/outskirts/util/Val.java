package outskirts.util;

public final class Val {

    public static Val zero() {
        return new Val(0);
    }

    public static Val of(float f) {
        return new Val(f);
    }

    private Val(float f) {
        this.val = f;
    }

    public float val;

}
