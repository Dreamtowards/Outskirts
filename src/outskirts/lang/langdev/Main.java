package outskirts.lang.langdev;

import outskirts.lang.langdev.interpreter.rtexec.RuntimeExec;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.parser.SyntaX;
import outskirts.lang.langdev.parser.spp.SpParser;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

        Lexer lex = new Lexer();
        lex.read("a = b = true ? then : else ? thn : el2 && a || sec");

        System.out.println(
                SpParser.parseExpr(lex)
        );


//        RuntimeExec.init();
//
//        RuntimeExec.imports("main.g");
    }

}