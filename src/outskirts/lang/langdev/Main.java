package outskirts.lang.langdev;

import outskirts.lang.langdev.interpreter.rtexec.RuntimeExec;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

        SyntaX.init();

        RuntimeExec.init();

        RuntimeExec.imports("main.g");
    }


}