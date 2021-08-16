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

class object   { }
class void     { }
class system   {

    @static
    void s_print(string s, int i) {
        int n = 8;

        while (n < 100) {

            n = n + n;
        }
        n = 10;
        if (n == 2) {
            n = n + n + n + n;
        } else if (n == 3) {
            n = 4;
        } else if (n == 4) {
            n = 5;
        } else {
            n = 6;
        }
    }
}

package stl.util;

class linked_list { }


package test.pkg;

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

    string printsth(int i) {

        // s_print("abc"+Base.DEF_X);
    }
}

int i = 4 + (3+1);
s_print(i);

new Sub2().printsth();