
{
    int i = 5;
    i = 10 * 5 = 8;

    void log(String s) {
        int i = 0;
        while (i < 4) {
            prt("[INFO]: "+s);
            i++;
            if (i == 2) {
                return;
            }
        }
    }

    int calc_add(int a, int b) {
        return a + b;
    }

    function print_warn = (s) =>
        prt("Print From Lambda: "+s);

    function print_warn_ret = (s) {
        prt("Print From Lambda RET: "+s);
        return s;
    };

    int j = i * 2;

    prt(j+i);
    prt(i);

    log("Log Some Text");
    log("Add Result: "+ calc_add(5, 6));

    prt(0 ? 2 ? 5 : 6 : 0 ? 8 : 9);

    prt("text");

    print_warn(123);
    prt(print_warn_ret(123));

    }

    class Animal {

        int age;
        String name;

        function printInfo = (g) {

        };
        void printInfo() {

        }
    }

    //Animal anim = new Sheep();

    //anim.printInfo();

    //anim.setAge(2);

}

// simplict oop.
class Animal {
    int age;

    // final function<void> printInfo = CLASS_get(..);
    void printInfo() {
        println("age: "+age);
    }
}
class Sheep : Animal {
    int sleep = 0;

    function<void, int> setAge = i -> {
        age = i;
    };

    // final function<void> printInfo = CLASS_get(...);
    void printInfo(self) {
        super.printInfo();
        println("sleep: "+sleep);
    }
}


// (-a * -b * -c) = c = 8

//(var1). val .b()(1+4,++2++)
