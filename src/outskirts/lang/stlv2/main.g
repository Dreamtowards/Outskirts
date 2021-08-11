package test.v2;

using stl.lang.void;
using stl.lang.int;

using stl.lang.string;
using stl.lang.system;

class _main : supcls1.MapEntry {

    int i;

    @static
    string str;

    /*
    @static
    class innCls {

    }*/

    @static
    void main() {

        string s = "good";
        // ldc "good"
        // store 0  //pop() -> s


        system.print(s.char_at(1));
        // getstatic system::print
        //     load 0
        //     getinstance char_at
        //   const_i 1
        //   invokefunction
        // invokefunction

        int ch = 123;
        // push 123
        // store 1  //ch

        system.print(ch);
        // getstatic system::print
        //   load 1
        // invokefunction

    }

}