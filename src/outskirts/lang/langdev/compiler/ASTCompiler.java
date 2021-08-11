package outskirts.lang.langdev.compiler;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Package;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Using;
import outskirts.lang.langdev.compiler.symtab.Symbol;
import outskirts.lang.langdev.compiler.symtab.Symtab;
import outskirts.lang.langdev.interpreter.ASTEvaluator;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ASTCompiler {

    private Symtab currentScope = null;

    public void pushScope() {
        currentScope = new Symtab(currentScope);
    }
    public void popScope() {
        currentScope = currentScope.getParent();
    }

    private static final int CLASS_VERSION = 1;

    public static ClassFile compileClass(AST_Stmt_DefClass a, Scope scope) {
        ConstantPool constantpool = new ConstantPool();

        String thisclass = scope.currentClassnamePrefix()+a.name;

        Scope scpdefclass = new Scope(scope);
        scpdefclass.setClassnamePrefix(a.name);

        List<String> superclasses = new ArrayList<>();
        for (AST_Typename typename : a.superclasses) {
            String leadingname = LxParser._PeakQualifiedName(typename.nameptr, true);
            String fullname = LxParser._ExpandQualifiedName(typename.nameptr);

            GObject typ = scope.tryaccess(leadingname);
            if (typ != null) {
                // There is SoStupid.
                // should be use of Varname+OperBin Fetch.  "String.Entry -> scope.findtype(String) . Entry", "org.json.JSONObject -> scope.findtype(org) -> .json.JSONObject"
                fullname = (String)typ.value + (fullname.length() > leadingname.length() ? ("." + fullname.substring(leadingname.length()+".".length())) : "");
            }
            superclasses.add(fullname);
        }

        List<ClassFile.Field> fields = new ArrayList<>();
        for (AST_Class_Member mb : a.members) {
            short mod = 0;
            if (mb.isStatic()) mod |= ClassFile.Field.MASK_STATIC;
            AST_Stmt m = mb.member;
            if (m instanceof AST_Stmt_DefClass) {
//                compileClass((AST_Stmt_DefClass)m, scpdefclass);
                throw new UnsupportedOperationException("Inner Class Not Supported yet.");
            } else if (m instanceof AST_Stmt_DefFunc) {
                AST_Stmt_DefFunc c = (AST_Stmt_DefFunc)m;

                CodeBuf codebuf = new CodeBuf(constantpool);
                if (!mb.isStatic())
                    codebuf.defvar("this");
                for (AST_Stmt_DefFunc.AST_Func_Param param : c.params)
                    codebuf.defvar(param.name);
                FuncCompiler.compileStmtBlock(c.body, codebuf);
                System.out.println("Compiled Function: "+codebuf);

                String typename = "function<"+AST_Typename.EvalTypename(c.type, scope);
                for (AST_Stmt_DefFunc.AST_Func_Param param : c.params) {
                    typename += ", "+AST_Typename.EvalTypename(param.type, scope);
                }
                typename += ">";
                fields.add(new ClassFile.Field(c.name, mod, typename));
            } else if (m instanceof AST_Stmt_DefVar) {
                AST_Stmt_DefVar c = (AST_Stmt_DefVar)m;

                fields.add(new ClassFile.Field(c.name, mod, AST_Typename.EvalTypename(c.type, scope)));
            } else
                throw new IllegalStateException("Unsupported member: "+m);
        }



        ClassFile f = new ClassFile(CLASS_VERSION,
                constantpool,
                thisclass, superclasses.toArray(new String[0]),
                fields.toArray(new ClassFile.Field[0])
                );
        System.out.println("Compiled ClassFile: "+f);
        return f;
    }

    public static void compilePkgStmtBlock(AST_Stmt_Block a) {
        // no sub scope.

        for (AST_Stmt stmt : a.stmts) {
            compilePkgStmt(stmt);
        }
    }

    private static Symbol evalSymbalQuery(AST_Expr a, Symtab scope) {
        if (a instanceof AST_Expr_PrimaryVariableName) {
            return scope.resolve(a.varname());
        } else if (a instanceof AST_Expr_OperBi) {
            AST_Expr_OperBi c = (AST_Expr_OperBi)a;
            Validate.isTrue(c.operator.equals("."));
            Symbol left = evalSymbalQuery(c.left, scope);
            return left.subsymbols.get(c.right.varname());
        } else
            throw new IllegalStateException();
    }

    public static void compilePkgStmt(AST_Stmt a) {
        if (a instanceof AST_Stmt_DefClass)
        {
//            ClassFile f = compileClass((AST_Stmt_DefClass)a);
        }
        else if (a instanceof AST_Stmt_Package)
        {
//            AST_Stmt_Package c = (AST_Stmt_Package)a;
//            String pkgname = LxParser._ExpandQualifiedName(c.name);
//
//            scope.setClassnamePrefix(pkgname);
        }
        else if (a instanceof AST_Stmt_Using)
        {
//            AST_Stmt_Using c = (AST_Stmt_Using)a;
//            String fullname = LxParser._ExpandQualifiedName(c.used);
//            String tailname = LxParser._PeakQualifiedName(c.used, false);
//
//
//            Scope s = scope;
//            for (String p : StringUtils.explode(fullname, ".")) {
//                GObject o = s.tryaccess(p);
//                if (o != null) {
//                    s = (Scope)o.value;
//                } else {
//                    Scope ss = new Scope(s==scope? null : s);
//                    ss.setClassnamePrefix(p);
//
//                    s.declare(p, new GObject(ss));
//                    s = ss;
//                }
//            }
//
//            scope.declare(tailname, new GObject("typedef", s));
        }
        else
        {
            throw new IllegalStateException("Unsupported "+a);
        }
    }


}