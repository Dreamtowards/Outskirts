package general.lang;


class array<T> {

    ptr addr;
    int length;
    int elsize;

    T get(int idx) {
        return memory.get(addrof(idx));
    }

    void set(int idx, T o) {
        memory.set(addrof(idx), o);
    }

    @private
    ptr addrof(int idx) {
        return addr + elsize*idx;
    }

}