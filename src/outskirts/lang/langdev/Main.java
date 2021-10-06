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
import java.util.Arrays;

public class Main {

    public static Scope glob;
    public static Lexer currLxr;

    public static void main(String[] args) throws IOException {

        glob = new Scope(null);
        SymbolBuiltinType.init(glob);

        for (String cp : Arrays.asList(
                "stl/lang/string.g",
                "itptr.g"
        )) {
            // Lex
            Lexer lx = currLxr = new Lexer();
            lx.setSourceName(cp);
            lx.appendsource(IOUtils.toString(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/outskirts/lang/stlv2/"+cp)));

            // Parse
            AST__CompilationUnit a = LxParser.parse_CompilationUnit(lx);

            // Type Entering.
            // Attr. Identity   //  ASTSymbol.idenStmtBlockStmts(a, glob);
            a.accept(ASTSymolize.INSTANCE, glob);

            // Compile.
            ClassCompiler.compileStmtBlockStmts(a.getDeclrations());

        }


        // Exec
        SymbolFunction sf = glob.resolveQualifiedName("test._main.main");

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