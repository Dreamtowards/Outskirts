
{

    call print("some thing123");
    call print("Sth ABC");


    def func1(arg1, arg2) {
        var abc;
        abc = 0;

        while (abc < arg1) {

            abc = abc + 1;
            print "abc: " + abc;
        }
    }

    call func1(5, 6);

    print "Call Func2: ";
    call func1(2, 6);
}