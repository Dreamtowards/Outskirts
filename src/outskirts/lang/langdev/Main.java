package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.AST__CompilationUnit;
import outskirts.lang.langdev.compiler.ClassCompiler;
import outskirts.lang.langdev.compiler.ClassFile;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.interpreter.RuntimeExec;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.machine.Machine;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.ASTSymolEnter;
import outskirts.lang.langdev.symtab.SymbolBuiltinType;
import outskirts.lang.langdev.symtab.Scope;


import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {

        // Lex
        Lexer lx = new Lexer();
        lx.appendsource(RuntimeExec.readfileInSrc("itptr.g"));

        // Parse
        AST__CompilationUnit a = LxParser.parse_CompilationUnit(lx);

        // Type Entering.
        // Attr. Identity
        Scope glob = new Scope(null);  SymbolBuiltinType.init(glob);  //  ASTSymbol.idenStmtBlockStmts(a, glob);
        a.accept(new ASTSymolEnter(), glob);


        // Compile.
        ClassCompiler.compileStmtBlockStmts(a.getDeclrations());


        // Exec
        CodeBuf cbuf = ClassCompiler._COMPILED.get(0);
        Machine.exec(cbuf);






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