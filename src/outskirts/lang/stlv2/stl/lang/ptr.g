package stl.lang;

class ptr<T> {

    long addr;

    T get() {
        return memory.get<T>(addr);
    }

    void set(T v) {
        memory.set<T>(v);
    }

}