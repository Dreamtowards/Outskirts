
/**
 * TODO ls;
 * 1. Type Diff. TypeLiteral vs TypeInstance.
 */

namespace stl.lang {

    class string {

        int base;

        int char_at(int i) {
            i = 2;
            int off = sizeof(int)*i;
            int c = dereference<int>(/*this.base +*/ off);
            // return c;

            // int p = reference(c);
        }

        static void test(int pi) {

            int i = 10 * pi;
        }
    }
}

namespace stl.lang;

class _main {

    static _main retm() {}

    static void main() {

        int i = 10;

        // myinfo fr = myinfo();  // rval. cpy(esp-=sizeof(myinfo), localptr['fl'], sizeof(myinfo));
        // myinfo fl = fr;        // lval. cpy(pop_ptr(), localptr['fl'], sizeof(myinfo));

        // int itmp = myinfo().i;  // access rval. cpy(esp-=sizeof(myinfo) +fldIdx('i'), esp, sizeof(i.type));
        // int itmp = fl.i;        // access lval. push_ptr(pop_ptr() +fldIdx('i'));

        // Assignment, MemberAccess, Ref and Deref (r/lvalues), StackAlloc-ObjectCreation


        string s = string();  // object creation - stack-alloc
        // when dispose? is same as "string s;" ..?

        // s.base = 2;

        i = s.base;

        stl.lang.string.test();

        // s.char_at(2);

        // retm()();  // problem. static-symbol vs. instance-symbol.

    }
}











/*
struct string
{
    char* base;

    int len()
    {
        int i = 0;
        while (*(base+i++) != 0);
        return i;
    }
}*/










namespace ns.to.sth {
    class MyType {}
}
MyType myinst;
MyType myfunc() {..}



