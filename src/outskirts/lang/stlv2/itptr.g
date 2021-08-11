package test.pkg;

using stl.lang.string;


class Base {

    int x;
    int y;

    @static
    int DEF_X = 4;

    @static
    class Sub : Base {

        int z;

    }
}

class Sub2 : Base {

    void printsth() {

        s_print("abc"+Base.DEF_X);
    }
}

int i = 4 + (3+1);
s_print(i);

new Sub2().printsth();