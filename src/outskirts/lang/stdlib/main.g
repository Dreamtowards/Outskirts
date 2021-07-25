package test.uuuu.adsasjd.asdasd;

// using general.lang.string;

// using general.lang.my.string;

class TheSuperClass {

    int memberColorType = TheSuperClass.CT_GREEN;

    @static
    int CT_RED = 1;

    @static
    int CT_GREEN = 2;

    @static
    void doPrintCol(int c) {
        s_print("STATIC PRINTX: "+c);
    }

    @static
    class MyStaticInnerClass {

        @static
        int stVal = 9;

        int memberVal = 1;

    }

    class TesInstInnClass {

        int stVal = 2;

    }

}


// var myValue = s_import("myFil.g");
// s_print(myValue);

int i = TheSuperClass.MyStaticInnerClass.stVal;

auto inst = new TheSuperClass();

auto inn = new inst.TesInstInnClass();

s_print(inn.stVal);



/*

class ParentClass {

    void yoWhatsUp() {

        sys_debug();

        // using general.lang.String;

        typedef List = import("List");

        List<XML> ls;

        // package myPackage;

        class MyType {

        }

    }
}


class String {

    @private
    int hash;

    @private
    array<short> value;

    @constructor
    void init(array<short> vals) {

    }

    void char_at(int idx) {
        return value.get(idx);
    }

}


class Entity<T, T2> {

    int age = 100;

    String name;

    void print(boolean v) {
        if (v) {
            s_print("Print some V.");
        }
        s_print("Entity{age: "+age+", name: "+name+"}");
    }
}

class Main {

    function<void> log = (s) {
        s_print("[LOG]: "+s);
    };

}


Entity e = new Entity();
e.print(1);

int i = 10 >> 2;
new Main().log(i);

*/