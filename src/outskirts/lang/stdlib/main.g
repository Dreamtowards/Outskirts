package test.uuuu.adsasjd.asdasd;

// using general.lang.string;

// using general.lang.my.string;


string srx = s_readfile("myFil.g");
var myValue = s_import("myFil.g");

s_print(myValue);





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