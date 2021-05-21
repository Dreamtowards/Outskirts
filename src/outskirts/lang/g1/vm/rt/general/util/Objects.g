package general.util;


class Objects {

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b))
    }

    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static <T> T requireNonNull(T obj);

    public static boolean isNull(Object o);

    public static boolean nonNull(Object o);

    public static <T> T orDefault(T obj, T def, Predicate<T> predicate);

}