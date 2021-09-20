
/**
 * TODO ls;
 * 1. Type Diff. TypeLiteral vs TypeInstance. Solved!
 * 2. Assignment. local-variable. member-field. Solv. ptr sz assignment.
 * 3. StackObject Creation.  solv. space alloc.
 * 4. MemberAccess. from Variable vs. from TemporaryRvalues.  Solv. lval: ptr off, rval: slice.
 * 5. Pointers Syntax. & and *.  solv
 *
 * 1. Call Spec. ret-val. args. ret-ip.
 * 2. Lvalue Rvalue on used operation.
 *
 * 5. new HeapObjectCreation
 * 6. Pointer MemberAccess a->b.
 */

namespace stl.lang {


    class string {

        int* base;

        int char_at(int i) {
            //i = 2;
            int c = 3; //*(int*)( (int)(*this).base + sizeof(int) * i );
            // return c;
            return i;
        }

        static void test(int pi) {

            int i = 10 * pi;
        }
    }

    class _main {

        static void main() {

            int i = sizeof(int*);
            //int j = 12;
            //int* p = &j;

            string s = string();
            s.base = (int*)4;  //  4 as int*;
            *(int*)0 = (int)s.base;

            int i2 =
            s.char_at(19);


            //i = string().base as int;
            // *(0 as int*) = 3;
            // *(int*)0 = 3;
            // i = *p * 2;

        }
    }
}
