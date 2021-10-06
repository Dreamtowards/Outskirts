
/**
 * TODO ls;
 * 1. Type Diff. TypeLiteral vs TypeInstance. Solved!
 * 2. Assignment. local-variable. member-field. Solv. ptr sz assignment.
 * 3. StackObject Creation.  solv. space alloc.
 * 4. MemberAccess. from Variable vs. from TemporaryRvalues.  Solv. lval: ptr off, rval: slice.
 * 5. Pointers Syntax. & and *.  solv
 * 6. Call Spec. ret-val. args. ret-ip.
 *
 * 2. Lvalue Rvalue on used operations. relations.
 *
 * 5. new HeapObjectCreation
 * 6. Pointer MemberAccess a->b.
 * 7. Refied Generics
 * 8. using function<void, int> as int_consumer;
 * 9. Const Generics.
 */

using stl.lang.string;

namespace test;

class _main {

    static void main() {

        string s = string();

        s.base = "okString";

        int len = s.length();

    }
}
