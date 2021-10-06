
namespace stl.lang;

class string {

    int* base;

    int hash;
    // array<ushort> value;

    int char_at(int i) {
        return *(int*)((int)(*this).base + sizeof(int) * i);
    }

    int length() {
        int i = 0;
        while ((*this).char_at(i) != 0) {
            i = i + 1;
        }
        return i;
    }
/*
    static void memcpy(void* src, void* dest, int size) {
        while(size--)
            *(dest++) = *(src++);
    }

    string substring(int begin, int end) {
        int len = end - begin;
        int* p = (int*)new((len + 1) * sizeof(int));
        memcpy( (int*)((int)(*this).base + begin * sizeof(int)), p, len);
        *(int*)( (int)p + len * sizeof(int)) = 0;

        string s = string();
        s.base = p;

        return s;
    }

    /*
    @impl
    int hashcode() {
        if (hash == 0 && value.length) {
           hash = arrays.hash(value);  // won't return 0 since arrlen > 0.
        }
        return hash;
    }*/
}