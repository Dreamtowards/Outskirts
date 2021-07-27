package outskirts.lang.jtest;

import outskirts.lang.langdev.Main;

import java.io.IOException;
import java.util.function.IntConsumer;

public class TestMain {

    public static void consumeInt(IntConsumer c) {
        c.accept(4);
    }

    private String myName = "abc";

    void test() {

        consumeInt(i -> {
            System.out.println("Instance: " + myName + ". accept Int: " + i);
        });
    }

    // How java lambda can access "this" outer instance.?

    static class java {
        static class util {
            static class concurrent {
//                static class locks {
//
//                }
            }
        }
    }

    @interface anno {

    }

    public static void main(String[] args) throws IOException {


        Object java = 0;

//        ja2va.util.concurrent.locks.Lock ls = null;

        new TestMain().test();
    }

}
