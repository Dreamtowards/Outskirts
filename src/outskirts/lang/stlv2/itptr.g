
namespace prims {

    class u16 {}

}

namespace stl.lang {

    class u64 { }
    class u32 { }

    class string {

        u64 base;

        static int char_at(u32 i) {

            int j = sizeof(prims.u16);

            //int i = 1;
            //int i = 2;

            // return dereference<u64>(base + i*sizeof(u32));
        }
    }
}

namespace test {
    class myint {}
}

namespace stl.lang;

using test.myint as someint;


class _main {

    @permits
    @private
    static void main(int i) {

        someint m;

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


