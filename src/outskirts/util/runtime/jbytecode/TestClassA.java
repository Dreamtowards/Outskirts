package outskirts.util.runtime.jbytecode;

import java.io.FileInputStream;

public class TestClassA implements Runnable {

    private int testfield = 0;

    public int testfunc(String[] s, Object o, int i, byte b) {

        int locali = 5;
        int sum = 1 + testfield + locali;

        testfield = sum;

        return testfield;
    }

    public int[] func1() {
        return null;
    }

    public String fun2() {
        return null;
    }

    @Override
    public void run() {


    }
}
