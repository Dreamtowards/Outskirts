package stl.lang;


class array {

    @final
    int length;

    @private
    ptr base_ptr;

    T get(int idx) {
        return addr(idx).get();
    }

    void set(int idx, T v) {
        addr(idx).set(v);
    }

    @private
    ptr addr(int idx) {
        return base_ptr + idx * sizeof(T);
    }

}