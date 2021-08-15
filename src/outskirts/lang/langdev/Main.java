package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.AST_Stmt_Block;
import outskirts.lang.langdev.compiler.ASTCompiler;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.lang.langdev.interpreter.rtexec.RuntimeExec;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.ASTSymbol;
import outskirts.lang.langdev.symtab.Symtab;
//import outskirts.lang.langdev.java;


import java.io.IOException;

public class Main {



    public static void main(String[] args) throws IOException {

        // LEX
        Lexer lx = new Lexer();
        lx.read(RuntimeExec.readfileInSrc("itptr.g"));

        // PARSE
        AST_Stmt_Block a = LxParser.parseStmtBlockStmts(lx, Token.EOF_T);

        // IDEN, SCOPE.
        Symtab glob = new Symtab(null);
        ASTSymbol._Iden_Packages(a, glob);


        // SEMANTIC
//        ASTSemantic.

        // COMPILE
        ASTCompiler.compilePkgStmtBlock(a);

        System.out.println(ClassFile._CLASSPATH.keySet());


//        RuntimeExec.init();
//
//        RuntimeExec.imports("itptr.g");

//        RuntimeExec.exec("using _main; _main.main();");
    }

}