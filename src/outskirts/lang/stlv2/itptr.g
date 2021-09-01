

namespace stl.lang {

    class string {

        int base;

        int char_at(int i) {  i = 2;
            int off = sizeof(int)*i;

            int c = dereference<int>(8+4*3);

            // return c;
            // dereference<int>(this) = dereference<int>(this);
        }

        static void test() {

            int i = 10 * 9;
        }
    }
}

namespace stl.lang;

class _main {

    static void main() {

        int i = 10;

        string s = string();


        stl.lang.string.test();
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


