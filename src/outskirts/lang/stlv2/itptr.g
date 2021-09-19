
/**
 * TODO ls;
 * 1. Type Diff. TypeLiteral vs TypeInstance. Solved!
 * 1. Call Spec. ret-val. args. ret-ip.
 * 2. Assignment. local-variable. member-field.
 * 3. MemberAccess. from Variable vs. from TemporaryRvalues.
 * 4. StackObject Creation
 * 5. Pointers Syntax. & and *.
 * 6. Pointer MemberAccess a->b.
 *
 */

namespace stl.lang {

    /*
    class string {

        int base;

        int char_at(int i) {
            i = 2;
            int off = sizeof(int) * i;
            int c = dereference<int>(dereference<string>(this).base + off);
            // return c;
        }

        static void test(int pi) {

            int i = 10 * pi;
        }
    }*/

    class _main {

        static void main() {

            int i = sizeof(int*);
            int j = 12;
            int* p = &*&j;
            int** pp = &p;

            **pp = *p + 10;

            *(0 as int*) = 3;



            // string s1 = string();  // copy data at ..? stack top area.?
            // string s2 = s1;        // copy data at addr

            // i = string().base;  // access from ..? stack top area.?
            // i = s2.base;        // access from addr.

            // s.base = 2;
            // i = s.base;

            // stl.lang.string.test();

        }
    }
}
