

namespace test;

@private
class myint {


}

namespace stl.lang;

using test.myint as someint;


const class _main {

    @permits
    @private
    const static void main(int i) {

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


