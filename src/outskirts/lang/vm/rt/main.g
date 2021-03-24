
{

    print("some thing123");
    print("Sth ABC");

    {
        var str;
        str="abc";
        print(str);
    }

    def func1(arg1, arg2) {
        var abc;
        abc = 0;

        while (abc < arg1) {

            abc = abc + 1;
            print("abc: " + abc);
            return;
        }
    }

    var a = func1(5, 6);
    print("ret: "+a);

    print("Call Func2: ");
    func1(2, 6);
}