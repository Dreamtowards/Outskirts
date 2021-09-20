
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
            return *(int*)( (int)(*this).base + sizeof(int) * i );
        }

        int length() {
            int i = 0;
            while ((*this).char_at(i) != 0) {
                i = i+1;
            }
            return i;
        }

    }

    class _main {

        static void main() {

            string s = string();

            s.base = (int*)60;
            *(int*)60 = 'H';
            *(int*)64 = 'e';
            *(int*)68 = 'l';
            *(int*)72 = 'l';
            *(int*)76 = 'o';
            *(int*)80 = 0;

            //int c = s.char_at(0);
            //int expected = 'H';


            int len = s.length();


        }
    }
}
