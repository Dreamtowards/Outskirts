package ext;

import java.util.Scanner;

public class TestCalculator {

    private static Scanner s = new Scanner(System.in);
    private static TestCalculator calc = new TestCalculator(4, 3);

    public static void main(String[] args) {

//        // Task8 TestCode
//        TestCalculator calc = new TestCalculator(4, 3);
//        calc.prtAdd();
//        calc.prtSub();
//        calc.prtMul();
//        calc.prtDiv();

        theRecursiveLoop();

    }

    private static void theRecursiveLoop() {
        boolean c = true;
        if (s.hasNext()) {
            switch (s.next()) {
                case "A":
                    calc.prtAdd(); break;
                case "S":
                    calc.prtSub(); break;
                case "M":
                    calc.prtMul(); break;
                case "D":
                    calc.prtDiv(); break;
                case "X":
                    System.out.println("Goodbey"); c=false; break;
            }
        }
        if (c) {
            theRecursiveLoop();
        }
    }

    float num1;
    float num2;

    public TestCalculator(float num1, float num2) {
        this.num1 = num1;
        this.num2 = num2;
    }

    public void prtAdd() {
        float r = num1 + num2;
        System.out.println("Add: "+r);
    }
    public void prtSub() {
        float r = num1 - num2;
        System.out.println("Sub: "+r);
    }

    public void prtMul() {
        float r = num1 * num2;
        System.out.println("Mul: "+r);
    }
    public void prtDiv() {
        float r = num1 / num2;
        System.out.println("Div: "+r);
    }


}
