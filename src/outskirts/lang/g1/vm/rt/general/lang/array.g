package general.lang;

inline class array<E> extends Iterable<E> {

    int length;

    new(Class componentType, int length) {

        alloc componentType.size() * length;
    }

    int length();

    T get(int i) {
        return reference( pointer(this) + i * sizeof(T) )
    }

    void set(int i, E e);

}