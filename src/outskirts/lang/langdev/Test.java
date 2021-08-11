package outskirts.lang.langdev;

import java.util.Arrays;
import java.util.function.Function;

public class Test {

    interface Func {
        void ivk(int arg);
    }

    private static void the_lambda_invoker(Func func) {
        func.ivk(2);  // Just normal object passing. the lambda seems been encapsulated inside the func.
    }

    private static void callr(int arg) {
        the_lambda_invoker(i -> System.out.printf("lambda arg: %s, captured: %s", i, arg));
    }

    public static void main(String[] args) {
//        the_lambda_invoker(i -> System.out.printf("lambda arg: %s, captured: %s", i, Arrays.toString(args)));

        Function<String, Integer> a1 = String::length;
        Function<String, Integer> a2 = String::length;
        System.out.println(a1 == a2);
        System.out.println(a1.equals(a2));
        System.out.println("Firs: "+a1.getClass()+"; \nSecn: "+a2.getClass());
        System.out.println(a1.getClass() == a2.getClass());
        System.out.println(a1.getClass().equals(a2.getClass()));
//        callr(3);
//        callr(5);
//        callr(6);  // is lambda obj been create every time call/walk through the expr?
    }

}