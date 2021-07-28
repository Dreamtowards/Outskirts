package outskirts.lang.langdev.interpreter.rtexec;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.ex.FuncPtr;
import outskirts.lang.langdev.interpreter.ASTEvaluator;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.interpreter.nstdlib._nstdlib;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.parser.spp.SpParser;
import outskirts.util.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class RuntimeExec {

    private static final String[] SourceBasePaths = {"src/outskirts/lang/stlv2/"};
    private static final Scope RootScope = new Scope(null);


    public static void init() {

        init_rootscope();

        _nstdlib.register_binding_functions();

    }



    private static Scope exec(String code) {
//        System.out.println("ExecCode: "+code);
        Lexer lex = new Lexer();
        lex.read(code);

        Scope sc = new Scope(RootScope);

        AST ast = SpParser.parseStmtBlockStmts(lex, Token.EOF_T);
//        System.out.println("### Read Program: "+ast);

        if (!lex.eof())
            throw new IllegalStateException("Failed Read File Fully. syntax err. at '"+lex.peek()+"'");

//        ASTPrinter prtr = new ASTPrinter();
//        prtr.printExpr((AST_Expr)ast, 0);
//        System.out.println(prtr.buf.toString());


        ASTEvaluator evlr = new ASTEvaluator();
        evlr.eval(ast, sc);

        return sc;
    }


    // Exec Code, return the Scope, or a Variable if the scope have only one variable.
    public static GObject imports(String spath) {
        String sourc = readfileInSrc(spath);
        Scope sc = exec(sourc);

        if (sc.variables.size() == 1) {
            return sc.variables.get(sc.variables.keySet().iterator().next());
        } else {
            return new GObject(sc);
        }
    }


    public static File locateSourceFile(String spath, boolean fullcheck) {
        File dest = null;
        for (String basepath : SourceBasePaths) {
            File f = new File(basepath, spath);
            if (f.exists()) {
                if (fullcheck && dest != null)
                    throw new IllegalStateException("Found Duplicated File in path "+spath);
                dest = f;
                if (!fullcheck)
                    return f;
            }
        }
        return dest;
    }

    private static String readfileInSrc(String path) {
        try {
            return IOUtils.toString(new FileInputStream(locateSourceFile(path, true)));
        } catch (IOException ex) {
            throw new RuntimeException("Failed read file", ex);
        }
    }




    private static void init_rootscope() {
        Scope sc = RootScope;

        sc.declare("s_print", new GObject((FuncPtr) args1 -> {
            System.out.println(args1[0].value);
            return GObject.VOID;
        }));

        sc.declare("s_alert", new GObject((FuncPtr)args1 -> {
            JOptionPane.showMessageDialog(null, args1[0].value);
            return GObject.VOID;
        }));



        sc.declare("s_readfile", new GObject((FuncPtr) args1 -> {
            return new GObject(readfileInSrc((String)args1[0].value));
        }));

        sc.declare("s_exec", new GObject((FuncPtr) args1 -> {
            return new GObject(exec((String)args1[0].value));
        }));

        sc.declare("s_import", new GObject((FuncPtr) args1 -> {
            return imports((String)args1[0].value);
        }));

        sc.declare("s_internal_func", new GObject((FuncPtr) args1 -> {
            String s = (String)args1[0].value;
            return new GObject(Objects.requireNonNull(_nstdlib.INTERNAL_FUNC_TABLE.get(s), "InternalFunc not found. ('"+s+"')"));
        }));

        sc.declare("s_sleep", new GObject((FuncPtr) args1 -> {
            try {
                Thread.sleep(ASTEvaluator.toint(args1[0].value));
            } catch (InterruptedException ex) {
                throw new IllegalStateException("Failed slp thread", ex);
            }
            return GObject.VOID;
        }));

        sc.declare("false", new GObject(0));
        sc.declare("true", new GObject(1));

    }
}
