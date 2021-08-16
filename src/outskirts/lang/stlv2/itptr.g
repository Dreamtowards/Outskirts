/*
using static stlx.opengl.GL11.*;
using std.lang.*;

namespace bulletphysics.collision.narrowphase {
    class Base { }
}
using bulletphysics.collision.narrowphase.Base b;
using minecraft.client.Client;
*/

class array {

}

class ushort { }

package stl.lang;

class string1 {

    array<ushort> value;

    ushort char_at(int i) {

    }

}

class system   {

    @private
    static int get_int() {
        // return 9;
    }

    @private
    static void s_print(string s, int i) {
        int n = 8;

        while (n < 100) {

            n = n + n;
        }
        n = 10;
        if (n == 2) {
            n = n + n + n + n;
        } else {
            n = 6;
        }

        // n = get_int();
        // new system().print("sth");
    }
}

package stl.util;

class linked_list { }


package test.pkg;

using static stl.lang.system.s_print;

class Base {

    int x;
    int y;

    static int DEF_X = 4;

    static class Sub : Base {

        int z;

    }
}

class Sub2 : Base {

    string printsth(int i) {

        // s_print("abc"+Base.DEF_X);
    }
}
