

namespace stl.lang;

class ptr {}

class string
{
    ptr base;

    int len()
    {
        int i = 0;
        // while ((base + i++) != 0);
        // return i;
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


