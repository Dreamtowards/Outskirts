/*
using static stlx.opengl.GL11.*;
using std.lang.*;

namespace bulletphysics.collision.narrowphase {
    class Base { }
}
using bulletphysics.collision.narrowphase.Base b;
using minecraft.client.Client;
*/



package stl.lang;

class void     { }
class int      { }
class function { }
class string   { int i; }
class system   {

    @static
    void s_print(string s) {

    }
}

package test.pkg;

using stl.lang.string;
using stl.lang.int;
using stl.lang.void;

using static stl.lang.system.s_print;

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

    string printsth() {

        s_print("abc"+Base.DEF_X);
    }
}

int i = 4 + (3+1);
s_print(i);

new Sub2().printsth();