

namespace stl.lang {

    class string {

        int base;

        int char_at(int i) {  i = 2;
            int off = sizeof(int)*i;

            int c = dereference<int>(8+4*3);

            // return c;
            // dereference<int>(this) = dereference<int>(this);
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

        string s = string();  // object creation - stack-alloc

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


