package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.AST_Expr;
import outskirts.lang.langdev.ast.AST_Stmt;
import outskirts.lang.langdev.ast.AST_Stmt_Block;
import outskirts.lang.langdev.ast.AST_Stmt_DefVar;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Package;
import outskirts.lang.langdev.interpreter.astprint.ASTPrinter;
import outskirts.lang.langdev.interpreter.rtexec.RuntimeExec;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.ASTSymbolIden;
import outskirts.lang.langdev.symtab.SymbolClass;
import outskirts.lang.langdev.symtab.Symtab;
import outskirts.util.IOUtils;
//import outskirts.lang.langdev.java;


import java.io.FileInputStream;
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
        ASTSymbolIden._Iden_Packages(a, glob);


        // SEMANTIC

        // COMPILE

        ASTCompiler.compileRootStmtBlock(a);


//        RuntimeExec.init();
//
//        RuntimeExec.imports("itptr.g");

//        RuntimeExec.exec("using _main; _main.main();");
    }

}