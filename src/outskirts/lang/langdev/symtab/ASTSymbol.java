package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Package;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Using;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.Arrays;

public class ASTSymbol {

    public static final String
        BUILTIN_TYPE_STRING = "stl.lang.string",
        BUILTIN_TYPE_INT = "stl.lang.int",
        BUILTIN_TYPE_FUNCTION = "stl.lang.function";  // Generic.?

    public static Symbol idenExpr(AST_Expr a, Symtab scope) {
        if (a instanceof AST_Expr_PrimaryVariableName) {
            return a.sym= scope.resolveMAExpr(a);
        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
            return a.sym= scope.resolveMAStr(BUILTIN_TYPE_STRING);
        } else if (a instanceof AST_Expr_PrimaryLiteralNumber) {
            return a.sym= scope.resolveMAStr(BUILTIN_TYPE_INT);
        } else if (a instanceof AST_Expr_FuncCall) {
            AST_Expr_FuncCall c = (AST_Expr_FuncCall)a;
            idenExpr(c.funcptr, scope);
//            System.out.println("FuncRetType: "+((SymbolFunction)c.funcptr.sym).returntype);
            return a.sym= ((SymbolFunction)c.funcptr.sym).returntype;
        } else if (a instanceof AST_Expr_Lambda) {
            throw new UnsupportedOperationException();
        } else if (a instanceof AST_Expr_OperBi) {
            AST_Expr_OperBi c = (AST_Expr_OperBi)a;
            if (c.operator.equals(".")) {
                Symbol l = idenExpr(c.left, scope);
                return a.sym= l.resolveMember(c.right.varname());
            } else {
                Symbol  l = idenExpr(c.left, scope),
                        r = idenExpr(c.right, scope);
                if (l == r) return a.sym = l;
                else throw new UnsupportedOperationException("Incpompactble OperBin "+l+", "+r);
            }
        } else if (a instanceof AST_Expr_OperNew) {
            AST_Expr_OperNew c = (AST_Expr_OperNew)a;
            idenTypename(c.typeptr, scope);
//            System.out.println("New Instance: "+c.typeptr.sym);
            return a.sym= c.typeptr.sym;
        } else if (a instanceof AST_Expr_OperTriCon) {
            AST_Expr_OperTriCon c = (AST_Expr_OperTriCon)a;
            Symbol e1 = idenExpr(c.exprthen, scope);
            Symbol e2 = idenExpr(c.exprelse, scope);
            // return commonBaseType(e1, e2);
            Validate.isTrue(e1 == e2);
            return a.sym= e1;
        } else if (a instanceof AST_Expr_OperUnaryPost) {
            AST_Expr_OperUnaryPost c = (AST_Expr_OperUnaryPost)a;
            Validate.isTrue(c.operator.equals("++") || c.operator.equals("--"));
            return a.sym= idenExpr(c.expr, scope);
        } else if (a instanceof AST_Expr_OperUnaryPre) {
            AST_Expr_OperUnaryPre c = (AST_Expr_OperUnaryPre)a;
            Validate.isTrue(Arrays.asList("++","--","+","-","!","~").contains(c.operator));
            return a.sym= idenExpr(c.expr, scope);
        } else
            throw new IllegalStateException();
    }

    public static void idenStmt(AST_Stmt a, Symtab scope) {
        if (a instanceof AST_Stmt_Block) {
            idenStmtBlock((AST_Stmt_Block)a, scope);
        } else if (a instanceof AST_Stmt_DefFunc) {
            idenStmtDefFunc((AST_Stmt_DefFunc)a, scope);
        } else if (a instanceof AST_Stmt_DefVar) {
            idenStmtDefVar((AST_Stmt_DefVar)a, scope);
        } else if (a instanceof AST_Stmt_Expr) {
            idenExpr(((AST_Stmt_Expr)a).expr, scope);
        } else if (a instanceof AST_Stmt_Return) {
            idenStmtReturn((AST_Stmt_Return)a, scope);
        } else if (a instanceof AST_Stmt_If) {
            idenStmtIf((AST_Stmt_If)a, scope);
        } else if (a instanceof AST_Stmt_While) {
            idenStmtWhile((AST_Stmt_While)a, scope);
        } else if (a instanceof AST_Stmt_Using) {
            idenStmtUsing((AST_Stmt_Using)a, scope);
        } else if (a instanceof AST_Stmt_Package) {
            throw new IllegalStateException();
        } else if (a instanceof AST_Stmt_DefClass) {
            idenStmtDefClass((AST_Stmt_DefClass)a, scope);
        } else if (!(a instanceof AST_Stmt_Blank))
            throw new IllegalStateException();
    }

    public static void idenStmtBlock(AST_Stmt_Block a, Symtab scope) {
        Symtab bls = new Symtab(scope);

        idenStmtBlockStmts(a, bls);
    }

    public static void idenStmtBlockStmts(AST_Stmt_Block a, Symtab scope) {
        for (AST_Stmt stmt : a.stmts) {
            idenStmt(stmt, scope);
        }
    }


    public static void idenStmtReturn(AST_Stmt_Return a, Symtab scope) {
        if (a.expr != null) {
            idenExpr(a.expr, scope);
        }
    }
    public static void idenStmtIf(AST_Stmt_If a, Symtab scope) {
        idenExpr(a.condition, scope);
        idenStmt(a.thenb, scope);
        if (a.elseb != null) {
            idenStmt(a.elseb, scope);
        }
    }
    public static void idenStmtWhile(AST_Stmt_While a, Symtab scope) {
        idenExpr(a.condition, scope);
        idenStmt(a.then, scope);
    }

    public static void idenTypename(AST_Typename a, Symtab scope) {
        a.sym = scope.resolveMAExpr(a.nameptr);
    }

    public static void idenStmtDefFunc(AST_Stmt_DefFunc a, Symtab scope) {
        Symtab fns = new Symtab(scope);

        // return-type.
        idenTypename(a.returntype, scope);
        // params.
        for (AST_Stmt_DefFunc.AST_Func_Param p : a.params) {
            idenTypename(p.type, scope);

            fns.define(new SymbolVariable(p.name, null, p.type.sym));  // TODO: update AST_Func_Param -> AST_DefVar.
        }
        // body.
        idenStmtBlockStmts(a.body, fns);

        scope.define(new SymbolFunction(a.name, a.returntype.sym));  // TODO: Builtin-Type *function
    }

    public static void idenStmtDefVar(AST_Stmt_DefVar a, Symtab scope) {
        idenTypename(a.type, scope);

        scope.define(new SymbolVariable(a.name, null, a.type_s));

        if (a.initexpr != null) {
            idenExpr(a.initexpr, scope);
        }
    }


    public static void idenStmtDefClass(AST_Stmt_DefClass a, Symtab scope) {
        SymbolClass sclass = new SymbolClass(a.name, scope);
        a.thisclass = sclass;

        scope.define(sclass);

//        System.out.println("DefClas:"+sclass.name+" on "+scope);

        for (AST_Typename sup : a.superclasses) {
            idenTypename(sup, scope);
        }

        for (AST_Class_Member mb : a.members) {
            AST_Stmt m = mb.member;
            mb.isStatic();
            // isPrivate()

            if (m instanceof AST_Stmt_DefVar) {
                idenStmtDefVar((AST_Stmt_DefVar)m, sclass);
            } else if (m instanceof AST_Stmt_DefFunc) {
                idenStmtDefFunc((AST_Stmt_DefFunc)m, sclass);
            } else if (m instanceof AST_Stmt_DefClass) {
                idenStmtDefClass((AST_Stmt_DefClass)m, sclass);
            } else
                throw new IllegalStateException("Illegal member type");
        }
    }

    public static void idenStmtUsing(AST_Stmt_Using a, Symtab scope) {
        Symbol r = scope.resolveMAExpr(a.used);

        if (a.isStatic) Validate.isTrue(r instanceof SymbolVariable || r instanceof SymbolFunction);
        else            Validate.isTrue(r instanceof SymbolClass);

//        if (r instanceof SymbolClass) {
//            ((SymbolClass) r).isUsing = true;
//        }

//        System.out.println("Using "+r.name);
        scope.define(r);  // or "new SymbolUsing(name, r)" .?
    }
    // package, used at before root.
    public static Symtab _Heading_idenStmtPackage(AST_Stmt_Package a, Symtab scope) {
        Symtab sp = scope;

        for (String pkg : StringUtils.explode(LxParser._ExpandQualifiedName(a.name), "."))
        {
            Symbol existedPkgNode =  sp.getSymbol(pkg);
            if (existedPkgNode == null) {
                sp.define((SymbolPackage)(sp=new SymbolPackage(pkg,sp)));  // chaos
            } else {
                sp = existedPkgNode;
            }
        }

        return sp;
    }

    public static void _Iden_Packages(AST_Stmt_Block a, Symtab glob) {
        Symtab pkgScp = glob;
        for (AST_Stmt stmt : a.stmts) {
            if (stmt instanceof AST_Stmt_Package)
                pkgScp = ASTSymbol._Heading_idenStmtPackage((AST_Stmt_Package)stmt, glob);
            else
                ASTSymbol.idenStmt(stmt, pkgScp);
        }
    }

}
