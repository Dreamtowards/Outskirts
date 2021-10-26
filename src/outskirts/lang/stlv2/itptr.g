
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

using stl.lang.string;

namespace test;

class vec2<T> {
    T x;
    T y;

    T sum() {
        return this->x;
    }
}

class _main {

    static void main() {

        int i = 10;

        vec2<int> v;
        v.x = 8;
        v.y = 9;
        int vfv = v.sum();

        vec2<byte> v2;
        v2.x = (byte)1;
        v2.y = (byte)2;

        byte b2 = v2.x;

        int sz1 = sizeof(vec2<int>);
        int sz2 = sizeof(vec2<byte>);
        int sz3 = sizeof(vec2i);


/*
        v.x = 8;
        v.y = 9;

        int vl = v.x;

        int sz = sizeof(vec2<byte>); // 2 +4
        int sz2 = sizeof(vec2<int>);  // 8 +4

        string s = string();
        s.hash = 21;
        s.base = "okStr";*/

    }
}
