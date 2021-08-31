

namespace stl.lang {

    class string {

        int base ;

        int char_at(int i) {

            i = 2;
            int off = sizeof(int)*i;
            int p = 1 + off;

            int c = dereference<int>(8+4*3);


            // i = 2;
            // int off = sizeof(int)*i;
            // return dereference<u64>(base + off);
        }
    }
}

namespace stl.lang;

// using test.myint as someint;

class _main {

    @permits
    @private
    static void main(int i) {

        i = 2;
    }
}











/*
struct string
{
    char* base;

    int len()
    {
        int i = 0;
        while (*(base+i++) != 0);
        return i;
    }
}*/


