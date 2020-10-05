package outskirts.util;

public final class Ref<T> {

    private Ref() { }

    public static Ref wrap() {
        return new Ref();
    }
//    public static Ref<Float> zero() {
//        return wrap();
//    }

    public static Ref wrap(Object val) {
        Ref r = new Ref();
        r.value=val;
        return r;
    }

    public T value;

}
