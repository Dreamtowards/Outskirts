package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.AST__CompilationUnit;
import outskirts.lang.langdev.compiler.ClassCompiler;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.machine.Machine;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.*;
import outskirts.util.IOUtils;


import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static Scope glob;
    public static Lexer currLxr;

    public static void main(String[] args) throws IOException {

        // Lex
        Lexer lx = new Lexer(); currLxr = lx;
        lx.appendsource(IOUtils.toString(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/stlv2/itptr.g")));

        // Parse
        AST__CompilationUnit a = LxParser.parse_CompilationUnit(lx);

        // Type Entering.
        // Attr. Identity
        glob = new Scope(null);  SymbolBuiltinType.init(glob);  //  ASTSymbol.idenStmtBlockStmts(a, glob);
        a.accept(new ASTSymolize(), glob);


        // Compile.
        ClassCompiler.compileStmtBlockStmts(a.getDeclrations());


        // Exec
        SymbolFunction sf = glob.resolveQualifiedName("stl.lang._main.main");

        Machine.exec(sf.codebuf);






//        StringBuffer sb = new StringBuffer();
//        ASTPrinter.printStmt(a, 0, sb);
//        System.out.println(sb.toString());


//        RuntimeExec.init();
//
//        RuntimeExec.imports("itptr.g");

//        RuntimeExec.exec("using _main; _main.main();");
    }

}