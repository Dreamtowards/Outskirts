package outskirts.util;

public final class Ref<T> {

    private Ref() { }

    public static <T> Ref<T> wrap() {
        return new Ref<>();
    }

    public static <T> Ref<T> wrap(T val) {
        Ref<T> r = new Ref<>();
        r.value=val;
        return r;
    }

    public T value;

}
