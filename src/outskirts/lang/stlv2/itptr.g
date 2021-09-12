
/**
 * TODO ls;
 * 1. Type Diff. ClassInstance vs LiteralClass.
 * 2. Reduce of StackNew.
 * 3. Parse for TmpReference.
 */

namespace stl.lang {

    class string {

        int base;

        int char_at(int i) {
            i = 2;
            int off = sizeof(int)*i;
            int c = dereference<int>(this.base + off);
            return c;

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

        // data-uniform of rvalues and lvalues on runtime.
        // lvals always a ptr/addr, but rvals always a bunch of data.
        // how we manulate it, a uniform way? or special for two.

        // lets see some "Same case" but may diff in rvals/lvals.
        myinfo fr = myinfo();  // rval. cpy(esp-=sizeof(myinfo), localptr['fl'], sizeof(myinfo));
        myinfo fl = fr;        // lval. cpy(pop_ptr(), localptr['fl'], sizeof(myinfo));

        int itmp = myinfo().i;  // access rval. cpy(esp-=sizeof(myinfo) +fldIdx('i'), esp, sizeof(i.type));
        int itmp = fl.i;        // access lval. push_ptr(pop_ptr() +fldIdx('i'));

        // Assignment, MemberAccess, Ref and Deref (r/lvalues), StackAlloc-ObjectCreation


        string s = string();  // object creation - stack-alloc
        // when dispose? is same as "string s;" ..?

        s.base = 2;

        i = s.base;

        stl.lang.string.test();

        // s.char_at(2);

        // retm()();  // problem. static-symbol vs. instance-symbol.

        //Java// if (symf.isStatic) buf._invokestatic(symf.getQualifiedName());
        //Cpp//  if (symf.isStatic) buf.push_fnaddr(symf.getQualifiedName()); buf._invokefunc()

        // still requires evals the expr.
        // stl.lang.string().inn.doSthRetStr().testStatic();  // call static on ret of some function

        // (new string()).char_at(2);
        // new $string
        // getfield
        // call fsym.QName();  // fail, curr the FuncCall doesn't have FuncSym/FuncName, just have Args, just a operator.

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


