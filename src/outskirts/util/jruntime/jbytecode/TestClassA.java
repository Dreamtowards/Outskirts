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
    private static final int STATIC_I2 = 2;

    public int[] func1(int i) {

        switch (i) {
            case STATIC_I2:

                i++;
                break;
            default:
                break;
        }

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
