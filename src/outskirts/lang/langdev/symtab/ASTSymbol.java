//package outskirts.lang.langdev.symtab;
//
//import outskirts.lang.langdev.ast.*;
//import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
//import outskirts.lang.langdev.ast.AST__Typename;
//import outskirts.lang.langdev.ast.AST_Stmt_Using;
//import outskirts.lang.langdev.parser.LxParser;
//import outskirts.util.StringUtils;
//import outskirts.util.Validate;
//
//import java.util.Arrays;
//
//public class ASTSymbol {
//
//    public static void idenExpr(AST_Expr a, Scope scope) {
//        if (a instanceof AST_Expr_PrimaryIdentifier) {
//            a.evaltype= ((SymbolVariable)scope.resolve(a.varname())).type;
//        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
//            a.evaltype= SymbolBuiltinType._string;
//        } else if (a instanceof AST_Expr_PrimaryLiteralInt) {
//            a.evaltype= SymbolBuiltinType._int;
//        } else if (a instanceof AST_Expr_FuncCall) {
//            idenExprFuncCall((AST_Expr_FuncCall)a, scope);
//        } else if (a instanceof AST_Expr_Lambda) {
//            throw new UnsupportedOperationException();
//        } else if (a instanceof AST_Expr_OperBi) {
//            idenExprOperBin((AST_Expr_OperBi)a, scope);
//        } else if (a instanceof AST_Expr_OperNew) {
//            idenExprOperNew((AST_Expr_OperNew)a, scope);
//        } else if (a instanceof AST_Expr_OperTriCon) {
//            idenExprOperTriCon((AST_Expr_OperTriCon)a, scope);
//        } else if (a instanceof AST_Expr_OperUnaryPost) {
//            idenExprOperUnaryPost((AST_Expr_OperUnaryPost)a, scope);
//        } else if (a instanceof AST_Expr_OperUnaryPre) {
//            idenExprOperUnaryPre((AST_Expr_OperUnaryPre)a, scope);
//        } else
//            throw new IllegalStateException();
//    }
//
//    private static void idenExprFuncCall(AST_Expr_FuncCall a, Scope scope) {
//        idenExpr(a.funcptr, scope);
//        SymbolFunction sf = (SymbolFunction)a.funcptr.evaltype;
//        a.evaltype = sf.returntype;
//    }
//
//    private static void idenExprOperBin(AST_Expr_OperBi a, Scope scope) {
//        if (a.operator.equals(".")) {
//            idenExpr(a.left, scope);
//            Symbol l = scope.resolveMAStr(a.left.evaltype.getName());
//            a.evaltype = ((SymbolVariable)l.resolveMember(a.right.varname())).type;
//            a.right.evaltype = a.evaltype;
//        } else {
//            idenExpr(a.left, scope);
//            idenExpr(a.right, scope);
//            if (a.left.evaltype.getName().equals(a.right.evaltype.getName()))
//                a.evaltype = a.left.evaltype;  // return commonBaseType(e1, e2);
//            else throw new UnsupportedOperationException("Incpompactble OperBin "+a.left.evaltype.getName()+", "+a.right.evaltype.getName());
//        }
//    }
//
//    private static void idenExprOperNew(AST_Expr_OperNew a, Scope scope) {
//        idenTypename(a.typeptr, scope);
//        a.evaltype = a.typeptr.sym;
//    }
//
//    private static void idenExprOperTriCon(AST_Expr_OperTriCon a, Scope scope) {
//        idenExpr(a.exprthen, scope);
//        idenExpr(a.exprelse, scope);
//        Validate.isTrue(a.exprthen.evaltype.getName().equals(a.exprelse.evaltype.getName()));
//        a.evaltype = a.exprthen.evaltype;  // CommonBase.
//    }
//
//    private static void idenExprOperUnaryPost(AST_Expr_OperUnaryPost a, Scope scope) {
//        Validate.isTrue(a.operator.equals("++") || a.operator.equals("--"));
//        idenExpr(a.expr, scope);
//        a.evaltype = a.expr.evaltype;
//    }
//    private static void idenExprOperUnaryPre(AST_Expr_OperUnaryPre a, Scope scope) {
//        Validate.isTrue(Arrays.asList("++","--","+","-","!","~").contains(a.operator));
//        idenExpr(a.expr, scope);
//        a.evaltype = a.expr.evaltype;
//    }
//
//
//
//
//    public static void idenStmt(AST_Stmt a, Scope scope) {
//        if (a instanceof AST_Stmt_Block) {
//            idenStmtBlock((AST_Stmt_Block)a, scope);
//        } else if (a instanceof AST_Stmt_DefFunc) {
//            idenStmtDefFunc((AST_Stmt_DefFunc)a, scope);
//        } else if (a instanceof AST_Stmt_DefVar) {
//            idenStmtDefVar((AST_Stmt_DefVar)a, scope);
//        } else if (a instanceof AST_Stmt_Expr) {
//            idenExpr(((AST_Stmt_Expr)a).expr, scope);
//        } else if (a instanceof AST_Stmt_Return) {
//            idenStmtReturn((AST_Stmt_Return)a, scope);
//        } else if (a instanceof AST_Stmt_If) {
//            idenStmtIf((AST_Stmt_If)a, scope);
//        } else if (a instanceof AST_Stmt_While) {
//            idenStmtWhile((AST_Stmt_While)a, scope);
//        } else if (a instanceof AST_Stmt_Using) {
//            idenStmtUsing((AST_Stmt_Using)a, scope);
//        } else if (a instanceof AST_Stmt_Namespace) {
//            idenStmtNamespace((AST_Stmt_Namespace)a, scope);
//        } else if (a instanceof AST_Stmt_DefClass) {
//            idenStmtDefClass((AST_Stmt_DefClass)a, scope);
//        } else if (!(a instanceof AST_Stmt_Blank))
//            throw new IllegalStateException();
//    }
//
//    public static void idenStmtBlock(AST_Stmt_Block a, Scope scope) {
//        Scope bls = new Scope(scope);
//
//        idenStmtBlockStmts(a, bls);
//    }
//
//    public static void idenStmtBlockStmts(AST_Stmt_Block a, Scope scope) {
//        for (AST_Stmt stmt : a.stmts) {
//            idenStmt(stmt, scope);
//        }
//    }
//
//
//    public static void idenStmtReturn(AST_Stmt_Return a, Scope scope) {
//        if (a.expr != null) {
//            idenExpr(a.expr, scope);
//        }
//    }
//    public static void idenStmtIf(AST_Stmt_If a, Scope scope) {
//        idenExpr(a.condition, scope);
//        idenStmt(a.thenb, scope);
//        if (a.elseb != null) {
//            idenStmt(a.elseb, scope);
//        }
//    }
//    public static void idenStmtWhile(AST_Stmt_While a, Scope scope) {
//        idenExpr(a.condition, scope);
//        idenStmt(a.then, scope);
//    }
//
//    public static void idenTypename(AST__Typename a, Scope scope) {
//        a.sym = scope.resolveMAExpr(a.nameptr);
//    }
//
//    public static void idenStmtDefFunc(AST_Stmt_DefFunc a, Scope scope) {
//        Scope fns = new Scope(scope);
//
//        // return-type.
//        idenTypename(a.returntype, scope);
//        // params.
//        for (AST_Stmt_DefFunc.AST_Func_Param p : a.params) {
//            idenTypename(p.type, scope);
//
//            fns.define(new SymbolVariable(p.name, p.type.sym));  // TODO: update AST_Func_Param -> AST_DefVar.
//        }
//        // body.
//        idenStmtBlockStmts(a.body, fns);
//
//        scope.define(new SymbolVariable(a.name, new SymbolFunction(a.name, a.returntype.sym)));  // TODO: Builtin-Type *function
//    }
//
//    public static void idenStmtDefVar(AST_Stmt_DefVar a, Scope scope) {
//        idenTypename(a.type, scope);
//
//        scope.define(new SymbolVariable(a.name, a.type.sym));
//
//        if (a.initexpr != null) {
//            idenExpr(a.initexpr, scope);
//        }
//    }
//
//
//    public static void idenStmtDefClass(AST_Stmt_DefClass a, Scope scope) {
//        SymbolClass sclass = new SymbolClass(a.name, scope);
//        a.thisclass = sclass;
//
//        scope.define(sclass);
//
////        System.out.println("DefClas:"+sclass.name+" on "+scope);
//
//        for (AST__Typename sup : a.superclasses) {
//            idenTypename(sup, scope);
//        }
//
//        for (AST_Stmt_DefClass.AST_Class_Member mb : a.members) {
//            AST_Stmt m = mb.member;
//            mb.isStatic();
//            // isPrivate()
//
//            if (m instanceof AST_Stmt_DefVar) {
//                idenStmtDefVar((AST_Stmt_DefVar)m, sclass);
//            } else if (m instanceof AST_Stmt_DefFunc) {
//                idenStmtDefFunc((AST_Stmt_DefFunc)m, sclass);
//            } else if (m instanceof AST_Stmt_DefClass) {
//                idenStmtDefClass((AST_Stmt_DefClass)m, sclass);
//            } else
//                throw new IllegalStateException("Illegal member type");
//        }
//    }
//
//    public static void idenStmtUsing(AST_Stmt_Using a, Scope scope) {
//        Symbol r = scope.resolveMAExpr(a.used);
//
//        if (a.isStatic) Validate.isTrue(r instanceof SymbolVariable || r instanceof SymbolFunction);
//        else            Validate.isTrue(r instanceof SymbolClass);
//
////        System.out.println("Using "+r.name);
//        scope.define(new SymbolUsingAliasProxy(a.asname, r));
//    }
//
//    public static void idenStmtNamespace(AST_Stmt_Namespace a, Scope enclosingscope) {
//        Scope sp = enclosingscope;
//
//        for (String ns : StringUtils.explode(LxParser._ExpandQualifiedName(a.name), "."))
//        {
//            Symbol nsExixted = sp.getSymbol(ns);
//            if (nsExixted != null) {
//                sp = nsExixted;
//            } else {
//                SymbolNamespace nw = new SymbolNamespace(ns, sp);
//                sp.define(nw);
//                sp = nw;
//            }
//        }
//
//        for (AST_Stmt stmt : a.stmts)
//        {
//            idenStmt(stmt, sp);
//        }
//    }
//
////    // package, used at before root.
////    public static Symtab _Heading_idenStmtPackage(AST_Stmt_Package a, Symtab scope) {
////        Symtab sp = scope;
////
////        for (String pkg : StringUtils.explode(LxParser._ExpandQualifiedName(a.name), "."))
////        {
////            Symbol existedPkgNode =  sp.getSymbol(pkg);
////            if (existedPkgNode == null) {
////                sp.define((SymbolPackage)(sp=new SymbolPackage(pkg,sp)));  // chaos
////            } else {
////                sp = existedPkgNode;
////            }
////        }
////
////        return sp;
////    }
////
////    public static void _Iden_Packages(AST_Stmt_Block a, Symtab glob) {
////        Symtab pkgScp = glob;
////        for (AST_Stmt stmt : a.stmts) {
////            if (stmt instanceof AST_Stmt_Package)
////                pkgScp = ASTSymbol._Heading_idenStmtPackage((AST_Stmt_Package)stmt, glob);
////            else
////                ASTSymbol.idenStmt(stmt, pkgScp);
////        }
////    }
//
//}
