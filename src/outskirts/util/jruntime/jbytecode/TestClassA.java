package outskirts.util.jruntime.jbytecode;

public class TestClassA implements Runnable {

    private int testfield = 0;

    public int testfunc(String[] s, Object o, int i, byte b) {

        int locali = 5;
        int sum = 1 + testfield + locali;

        if (false || sum > 2 || sum ==8) {
            sum = sum + 2;
            sum = 0;
        }

        for (int ni = 0; ni < 10; ni++) {

            sum = 1;
        }

        testfield = sum;

        return testfield;
    }

    public int[] func1() {
        return null;
    }

    public String fun2() {

        {
            int i123 = 1;
        }

        int i123 = 0;

        return null;
    }

    @Override
    public void run() {


    }

    public static void main(String[] args) {

        TestClassA test = new TestClassA();

        int i = test.testfunc(null, null, 1, (byte)1);

        System.out.println(i);
    }
}
