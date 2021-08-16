//package outskirts.lang.langdev.interpreter;
//
//import outskirts.lang.langdev.ast.*;
//import outskirts.lang.langdev.interpreter.ex.FuncPtr;
//import outskirts.lang.langdev.interpreter.ex.FuncReturnEx;
//import outskirts.lang.langdev.ast.oop.AST_Class_Member;
//import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
//import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Package;
//import outskirts.lang.langdev.ast.AST_Stmt_Using;
//import outskirts.lang.langdev.interpreter.RuntimeExec;
//import outskirts.lang.langdev.parser.LxParser;
//import outskirts.lang.langdev.symtab.SymbolVariable;
//import outskirts.lang.langdev.symtab.Symtab;
//import outskirts.util.Validate;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//import static outskirts.util.FileUtils.MB;
//
//// ASTEvalutor instead of Evalutor: more specifica, more classifi, but may limited, and like an AST-subclass?.
//// however modify is available.
//public class ASTEvaluator {
//
//    public static byte[] HeapSpace = new byte[10 * MB];
//
//
//    public static boolean isConditionPass(AST_Expr cond, Symtab scope) {
//        return toint(evalExpr(cond, scope).value) != 0;
//    }
//    /**
//     * @param funcBody AST_Stmt_Block or AST_Expr.
//     */
//    public static FuncPtr defineFuncptr(Symtab funcOuterScope, AST funcBody, String[] paramsNames) {
//        // Validate.isTrue(funcBody instanceof AST_Stmt_Block || funcBody instanceof AST_Expr);  // will check.
//
//        return args -> {
//            Validate.isTrue(args.length == paramsNames.length);
//
//            Symtab funcscope = new Symtab(funcOuterScope);
//
//            // init args
//            for (int i = 0;i < args.length;i++) {
//                String paramname = paramsNames[i];
//                funcscope.define(new SymbolVariable(paramname, ), args[i]);
//            }
//
//            // execution
//            if (funcBody instanceof AST_Stmt_Block) {
//                try {
//                    evalStmtBlock((AST_Stmt_Block)funcBody, funcscope);
//                    return GObject.VOID;
//                } catch (FuncReturnEx rv) {
//                    return rv.retval;
//                }
//            } else if (funcBody instanceof AST_Expr) {
//                return evalExpr((AST_Expr)funcBody, funcscope);
//            } else
//                throw new IllegalStateException();
//        };
//    }
//
//
//
//    public GObject eval(AST a, Symtab scope)
//    {
//        if (a instanceof AST_Expr)
//        {
//            return evalExpr((AST_Expr)a, scope);
//        }
//        else if (a instanceof AST_Stmt)
//        {
//            evalStmt((AST_Stmt)a, scope);
//            return GObject.VOID;
//        }
//        else
//        {
//            throw new IllegalStateException();
//        }
//    }
//
//
//
//
//    /**
//     * ============= AST_EXPR =============
//     */
//
//    private static GObject evalExprPrimaryLiteralString(AST_Expr_PrimaryLiteralString a) {
//        return a.str;
//    }
//
//    public static GObject evalExprPrimaryLiteralNumber(AST_Expr_PrimaryLiteralNumber a) {
//        return a.num;
//    }
//
//    public static GObject evalExprPrimaryVariableName(AST_Expr_PrimaryVariableName a, Scope scope) {
//        return scope.access(a.name);
//    }
//
//    public static GObject evalExprOperBi(AST_Expr_OperBi a, Scope scope) {
//        GObject l = evalExpr(a.left, scope);
//        if (a.operator.equals(".")) {
//            return Objects.requireNonNull((Scope)l.value).access(a.right.varname());
//        }
//
//        GObject r = evalExpr(a.right, scope);
//
//        switch (a.operator) {
//            case "+": return l.value instanceof String ?
//                    new GObject((String)l.value + r.value) :
//                    new GObject((float)l.value + (float)r.value);
//            case "-": return new GObject((float)l.value - (float)r.value);
//            case "*": return new GObject((float)l.value * (float)r.value);
//            case "/": return new GObject((float)l.value / (float)r.value);
//            case "=": {
//                l.value = r.value;  // not actually working.
//                return l;
//            }
//            case "<": return  new GObject((float)l.value <  (float)r.value ? 1f : 0f);
//            case "<=": return new GObject((float)l.value <= (float)r.value ? 1f : 0f);
//            case ">": return  new GObject((float)l.value >  (float)r.value ? 1f : 0f);
//            case ">=": return new GObject((float)l.value >= (float)r.value ? 1f : 0f);
//            case "==": return new GObject((float)l.value == (float)r.value ? 1f : 0f);
//            case ">>": return new GObject(toint(l.value) >> toint(r.value));
//            case ">>>": return new GObject(toint(l.value) >>> toint(r.value));
//            default:
//                throw new IllegalStateException("Unsupported Operator '"+a.operator+"'.");
//        }
//    }
//
//    public static int toint(Object o) {
//        return o instanceof Integer ? (int)o :
//                ((Float)o).intValue();
//    }
//
//    public static GObject evalExprOperUnaryPre(AST_Expr_OperUnaryPre a, Scope scope) {
//        GObject o = evalExpr(a.expr, scope);
//
//        switch (a.operator) {
//            case "++": o.value = (float)o.value+1f; return o;
//            case "--": o.value = (float)o.value-1f; return o;
//            case "+": return o;
//            case "-": return new GObject(-(float)o.value);
//            case "!": return new GObject(toint(o.value) == 0 ? 1 : 0);
//            default:
//                throw new IllegalStateException();
//        }
//    }
//
//    public static GObject evalExprOperUnaryPost(AST_Expr_OperUnaryPost a, Scope scope) {
//        GObject o = evalExpr(a.expr, scope);
//
//        switch (a.operator) {
//            case "++": {
//                float tmp = (float)o.value;
//                o.value = tmp+1f;
//                return new GObject(tmp);
//            }
//            case "--": {
//                float tmp = (float)o.value;
//                o.value = tmp-1f;
//                return new GObject(tmp);
//            }
//            default:
//                throw new IllegalStateException();
//        }
//    }
//
//    public static GObject evalExprOperTriCon(AST_Expr_OperTriCon a, Scope scope) {
//        if (isConditionPass(a.condition, scope))
//            return evalExpr(a.exprthen, scope);
//        else
//            return evalExpr(a.exprelse, scope);
//    }
//
//
//    public static GObject evalExprLambda(AST_Expr_Lambda a, Scope scope) {
//        String[] pnames = new String[a.params.size()];
//        for (int i = 0;i < pnames.length;i++) {
//            pnames[i] = ((AST_Expr_PrimaryVariableName)a.params.get(i)).name;
//        }
//
//        return new GObject(defineFuncptr(scope, a.body, pnames));
//    }
//
//    public static GObject evalExprFuncCall(AST_Expr_FuncCall a, Scope scope) {
//        FuncPtr fnptr = (FuncPtr)evalExpr(a.funcptr, scope).value;
//
//        // eval args.
//        GObject[] args = new GObject[a.args.size()];
//        for (int i = 0;i < args.length;i++) {
//            args[i] = evalExpr(a.args.get(i), scope);  // curr scope.?  not in-func scope.?
//        }
//
//        return fnptr.invoke(args);
//    }
//
//    public static GObject evalExprOperNew(AST_Expr_OperNew a, Scope scope) {
//        AST_Stmt_DefClass c = ((Scope)evalExpr(a.typeptr.nameptr, scope).value).clxdef; // (AST_Stmt_DefClass)scope.access(a.type.name).value;
//        Validate.isTrue(c != null);
//
//        return _NewInstance(c, scope);
//    }
//    public static GObject _NewInstance(AST_Stmt_DefClass c, Symtab parentscope) {
//        Scope clsScope = new Scope(parentscope);
//        for (AST_Class_Member s : c.members) {
//            if (!s.isStatic()) {
//                evalStmt(s.member, clsScope);
//            }
//        }
//        return new GObject(clsScope);  // DANGER! uh... return a scope...
//    }
//
//    public static GObject evalExpr(AST_Expr a, Symtab scope) {
//        if (a instanceof AST_Expr_OperBi) {
//            return evalExprOperBi((AST_Expr_OperBi)a, scope);
//        } else if (a instanceof AST_Expr_OperUnaryPre) {
//            return evalExprOperUnaryPre((AST_Expr_OperUnaryPre)a, scope);
//        } else if (a instanceof AST_Expr_OperUnaryPost) {
//            return evalExprOperUnaryPost((AST_Expr_OperUnaryPost)a, scope);
//        } else if (a instanceof AST_Expr_OperTriCon) {
//            return evalExprOperTriCon((AST_Expr_OperTriCon)a, scope);
//        } else if (a instanceof AST_Expr_Lambda) {
//            return evalExprLambda((AST_Expr_Lambda)a, scope);
//        } else if (a instanceof AST_Expr_FuncCall) {
//            return evalExprFuncCall((AST_Expr_FuncCall)a, scope);
//        } else if (a instanceof AST_Expr_PrimaryLiteralNumber) {
//            return evalExprPrimaryLiteralNumber((AST_Expr_PrimaryLiteralNumber)a);
//        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
//            return evalExprPrimaryLiteralString((AST_Expr_PrimaryLiteralString)a);
//        } else if (a instanceof AST_Expr_PrimaryVariableName) {
//            return evalExprPrimaryVariableName((AST_Expr_PrimaryVariableName)a, scope);
//        } else if (a instanceof AST_Expr_OperNew) {
//            return evalExprOperNew((AST_Expr_OperNew)a, scope);
//        } else
//            throw new IllegalStateException();
//    }
//
//
//
//    /**
//     * ============= AST_STMT =============
//     */
//
//    public static void evalStmtBlockStmts(AST_Stmt_Block a, Scope scope) {
//        for (AST_Stmt s : a.stmts) {
//            evalStmt(s, scope);
//        }
//    }
//    public static void evalStmtBlock(AST_Stmt_Block a, Scope scope) {
//        Scope blscope = new Scope(scope);
//
//        evalStmtBlockStmts(a, blscope);
//    }
//
//    public static void evalStmtDefFunc(AST_Stmt_DefFunc a, Scope scope) {
//        String[] pnames = new String[a.params.size()];
//        for (int i = 0;i < pnames.length;i++) {
//             pnames[i] = a.params.get(i).name;
//        }
//        // scope.define();
//        scope.declare(a.name, new GObject(defineFuncptr(scope, a.body, pnames)));
//    }
//
//    public static void evalStmtDefVar(AST_Stmt_DefVar a, Scope scope) {
//
//        scope.declare(a.name, a.initexpr != null ? evalExpr(a.initexpr, scope) : new GObject(null));
//    }
//
//    public static void evalStmtFuncReturn(AST_Stmt_FuncReturn a, Scope scope) {
//
//        throw new FuncReturnEx(evalExpr(a.expr, scope));
//    }
//
//    public static void evalStmtStrmIf(AST_Stmt_Strm_If a, Scope scope) {
//
//        if (isConditionPass(a.condition, scope)) {
//            evalStmt(a.thenb, scope);
//        } else if (a.elseb != null){
//            evalStmt(a.elseb, scope);
//        }
//    }
//
//    public static void evalStmtStrmWhile(AST_Stmt_Strm_While a, Scope scope) {
//        while (true) {
//            if (!isConditionPass(a.condition, scope))
//                break;
//
//            evalStmt(a.then, scope);
//        }
//    }
//
//    public static void evalStmtExpr(AST_Stmt_Expr a, Scope scope) {
//
//        evalExpr(a.expr, scope);
//    }
//
//    public static Map<String, GObject> loadedClasses = new HashMap<>();
//
//    public static void evalStmtDefClass(AST_Stmt_DefClass a, Scope scope) {
//        Scope clxdefScope = new Scope(scope);
//        clxdefScope.clxdef = a;
//
//        // Set Classname Prefix  (before member.
//        clxdefScope.setClassnamePrefix(a.name);
//
//        for (AST_Class_Member m : a.members) {
//            if (m.isStatic()) {
//                evalStmt(m.member, clxdefScope);
//            }
//        }
//        System.out.println("Define Class: "+scope.currentClassnamePrefix()+"."+a.name);
//
//        GObject clx = new GObject(clxdefScope);
//        loadedClasses.put(scope.currentClassnamePrefix()+"."+a.name, clx);
//        scope.declare(a.name, clx);
//    }
//
//    public static void evalStmt(AST_Stmt a, Scope scope) {
//        if (a instanceof AST_Stmt_Block) {
//            evalStmtBlock((AST_Stmt_Block)a, scope);
//        } else if (a instanceof AST_Stmt_DefFunc) {
//            evalStmtDefFunc((AST_Stmt_DefFunc)a, scope);
//        } else if (a instanceof AST_Stmt_DefVar) {
//            evalStmtDefVar((AST_Stmt_DefVar)a, scope);
//        } else if (a instanceof AST_Stmt_FuncReturn) {
//            evalStmtFuncReturn((AST_Stmt_FuncReturn)a, scope);
//        } else if (a instanceof AST_Stmt_Strm_If) {
//            evalStmtStrmIf((AST_Stmt_Strm_If)a, scope);
//        } else if (a instanceof AST_Stmt_Strm_While) {
//            evalStmtStrmWhile((AST_Stmt_Strm_While)a, scope);
//        } else if (a instanceof AST_Stmt_Expr) {
//            evalStmtExpr((AST_Stmt_Expr)a, scope);
//        } else if (a instanceof AST_Stmt_DefClass) {
//            evalStmtDefClass((AST_Stmt_DefClass)a, scope);
//        } else if (a instanceof AST_Stmt_Using) {
//            evalStmtUsing((AST_Stmt_Using) a, scope);
//        } else if (a instanceof AST_Stmt_Package) {
//            evalStmtPackage((AST_Stmt_Package)a, scope);
//        } else if (a instanceof AST_Stmt_Blank) {
//            // just ignored.
//        } else
//            throw new IllegalStateException("Illegal Stmt Type: "+a);
//    }
//
//    public static void evalStmtUsing(AST_Stmt_Using a, Scope scope) {
//        String rfname = LxParser._PeakQualifiedName(a.used, false);
//        String fullname = LxParser._ExpandQualifiedName(a.used);
//
//        scope.declare(rfname, _EnsureLoadClass(fullname));
//    }
//
//    public static void evalStmtPackage(AST_Stmt_Package a, Scope scope) {
//        Validate.isTrue(scope.parent == RuntimeExec.RootScope, "package stmt only allowed on -root scope.");
//
//        scope.setClassnamePrefix(LxParser._ExpandQualifiedName(a.name));
//    }
//
//    public static GObject _EnsureLoadClass(String fullname) {
//        if (!loadedClasses.containsKey(fullname)) {
//            RuntimeExec.imports(RuntimeExec.classnameToFilename(fullname));
//        }
//        return Objects.requireNonNull(loadedClasses.get(fullname), "Not Found Class "+fullname);
//    }
//
//
//
//
//
//
//
//
////    /**
////     * ============= AST_SR =============
////     */
////
////    public void evalSrStmtLs(AST_SR_StmtLs a, Scope scope) {
////
////        for (AST stmt : a.elements) {
////
////            eval(stmt, scope);
////        }
////    }
//
////    private static String _ExpandPackageName(AST_Expr a) {
////        if (a instanceof AST_Expr_OperBi) {
////            return _ExpandPackageName(((AST_Expr_OperBi)a).left) + "." + _ExpandPackageName(((AST_Expr_OperBi)a).right);
////        } else if (a instanceof AST_Expr_PrimaryVariableName) {
////            return ((AST_Expr_PrimaryVariableName) a).name;
////        } else
////            throw new IllegalStateException();
////    }
//
////    public void evalSrStmtUsing(AST_SR_Stmt_Using a, Scope scope) {
//
////        scope.declare(((AST_Expr_PrimaryVariableName)a.used.right).name, new GObject(new UnsupportedOperationException()));
//
////    }
//
////    public void evalSrStmtPackage(AST_SR_Stmt_Package a, Scope scope) {
//
////        scope.setScopeCurrentClassnamePrefix(_ExpandPackageName(a.name));
//
////    }
//
////    public void evalSourceRoot(AST_SR a, Scope scope) {
////        if (a instanceof AST_SR_Stmt_Using)
////            evalSrStmtUsing((AST_SR_Stmt_Using)a, scope);
////        else if (a instanceof AST_SR_Stmt_Package)
////            evalSrStmtPackage((AST_SR_Stmt_Package)a, scope);
////        else if (a instanceof AST_SR_StmtLs)
////            evalSrStmtLs((AST_SR_StmtLs)a, scope);
////        else
////            throw new IllegalStateException();
////    }
//
//
//}
