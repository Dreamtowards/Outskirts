package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.astprint.ASTPrinter;
import outskirts.lang.langdev.ast.AST_Stmt_Block;
import outskirts.lang.langdev.compiler.ASTCompiler;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.interpreter.RuntimeExec;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.machine.Machine;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.ASTSymbol;
import outskirts.lang.langdev.symtab.SymbolBuiltinType;
import outskirts.lang.langdev.symtab.Symtab;


import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

        // LEX
        Lexer lx = new Lexer();
        lx.read(RuntimeExec.readfileInSrc("itptr.g"));

        // PARSE
        AST_Stmt_Block a = LxParser.parseStmtBlockStmts(lx, Token.EOF_T);


        // IDEN, SCOPE.
        Symtab glob = new Symtab(null);  SymbolBuiltinType.init(glob);
        ASTSymbol.idenStmtBlockStmts(a, glob);


        // COMPILE
        ASTCompiler.compileStmtBlockStmts(a.stmts);


        CodeBuf cbuf = ASTCompiler._COMPILED.get(0);
        Machine.exec(cbuf.toByteArray(), cbuf.constantpool, cbuf.localsize());







        System.out.println(ClassFile._CLASSPATH.keySet());


//        StringBuffer sb = new StringBuffer();
//        ASTPrinter.printStmt(a, 0, sb);
//        System.out.println(sb.toString());


//        RuntimeExec.init();
//
//        RuntimeExec.imports("itptr.g");

//        RuntimeExec.exec("using _main; _main.main();");
    }

}