package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.AST_Stmt;
import outskirts.lang.langdev.ast.AST_Stmt_Block;
import outskirts.lang.langdev.compiler.ASTCompiler;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.interpreter.astprint.ASTPrinter;
import outskirts.lang.langdev.interpreter.rtexec.RuntimeExec;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.IOUtils;
//import outskirts.lang.langdev.java;


import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    static class lang {
        int String;
    }
//    static class java {
//        public lang lang;
//    }

    public static void main(String[] args) throws IOException {

//        Lexer lx = new Lexer();
//        lx.read(RuntimeExec.readfileInSrc("main.g"));
//
//        AST_Stmt_Block a = LxParser.parseStmtBlockStmts(lx, Token.EOF_T);

//        StringBuffer sb = new StringBuffer();
//        ASTPrinter.printStmtBlock(a, 0, sb);
//        System.out.println(sb);

//        ASTCompiler.compileRootStmtBlock(a);


        RuntimeExec.init();

        RuntimeExec.imports("itptr.g");

//        RuntimeExec.exec("using _main; _main.main();");
    }

}