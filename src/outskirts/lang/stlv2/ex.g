
/**
 * TODO ls;
 * 1. Type Diff. TypeLiteral vs TypeInstance. Solved!
 * 2. Assignment. local-variable. member-field. Solv. ptr sz assignment.
 * 3. StackObject Creation.  solv. space alloc.
 * 4. MemberAccess. from Variable vs. from TemporaryRvalues.  Solv. lval: ptr off, rval: slice.
 * 5. Pointers Syntax. & and *.  solv
 * 6. Call Spec. ret-val. args. ret-ip.  solv.
 *
 * 2. Lvalue Rvalue on used operations. relations.
 * 6. Pointer MemberAccess a->b.
 *
 * 5. new HeapObjectCreation
 * 7. Refied Generics
 * 8. using function<void, int> as int_consumer;
 * 9. Const Generics.
 */



/*
using static stlx.opengl.GL11.*;
using std.lang.*;

namespace bulletphysics.collision.narrowphase {
    class Base { }
}
using bulletphysics.collision.narrowphase.Base b;
using minecraft.client.Client;
*/






class a {}
/*
class memory {

    native T get<T>(long addr);

    native void set<T>(long addr, T v);

}

class array<T> {

    ptr base_ptr;
    uint length;

    T get(uint idx) {

    }

}*/

class ushort { }


namespace stl.lang {


    /*class string1 {

        array<ushort> value;

        ushort char_at(int i) {
            value.get(i);

            //return *(base_ptr+i);
        }
    }*/

    class system {

        static int get_int() {
            // return 9;
        }

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

}



namespace stl.util
{
    class linked_list { }
}

namespace test.ns
{
    using static stl.lang.system.s_print;

    class Base
    {

        int x;
        int y;

        static int DEF_X = 4;

        static class Sub : Base
        {

            int z;

        }
    }

    class Sub2 : Base
    {
        string printsth(int i)
        {
            // s_print("abc"+Base.DEF_X);
        }
    }
}