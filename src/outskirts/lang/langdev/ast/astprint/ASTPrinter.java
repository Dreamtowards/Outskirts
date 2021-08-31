package outskirts.lang.langdev.ast.astprint;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;

/**
 * EXPR_OPER_BI {
 *     VAR{ good }
 *     +
 *     EXPR_OPER_BI {
 *         NUM{10}
 *         -
 *         NUM{10}
 *     }
 * }
 */
public class ASTPrinter implements ASTVisitor<ASTPrinter.PrintStat> {



    public static class PrintStat {
        public final StringBuilder buf = new StringBuilder();
        public final String indent = "  ";
        public int dp = 0;
    }

    private static void appendln(PrintStat ps, String s) {
        ps.buf.append(ps.indent.repeat(ps.dp))
              .append(s)
              .append('\n');
    }
    private static String quote(String s) {
        return "'" + s + "'";
    }


    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, PrintStat p) {
        appendln(p, "ExprFuncCall {");          p.dp++;
            appendln(p, "funcptr:");            p.dp++;
                a.getExpression().accept(this, p);    p.dp--;
            appendln(p, "args: [");             p.dp++;
            for (AST_Expr e : a.getArguments()) {
                e.accept(this, p);
            }                                      p.dp--;
            appendln(p, "]");                   p.dp--;
        appendln(p, "}");
    }

    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, PrintStat printStat) {

    }

    @Override
    public void visitExprOperBin(AST_Expr_OperBi a, PrintStat ps) {
        appendln(ps, "ExprBinary {"); ps.dp++;
            a.getLeftOperand().accept(this, ps);
            appendln(ps, quote(a.getBinaryKind().name()));
            a.getRightOperand().accept(this, ps);  ps.dp--;
        appendln(ps, "}");
    }

    @Override
    public void visitExprOperNew(AST_Expr_OperNew a, PrintStat printStat) {

    }

    @Override
    public void visitExprOperTriCon(AST_Expr_OperConditional a, PrintStat printStat) {

    }

    @Override
    public void visitExprOperUnary(AST_Expr_OperUnary a, PrintStat printStat) {

    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, PrintStat printStat) {

    }

    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, PrintStat printStat) {

    }

    @Override
    public void visitExprPrimaryLiteralInt(AST_Expr_PrimaryLiteralInt a, PrintStat printStat) {

    }

    @Override
    public void visitExprPrimaryLiteralChar(AST_Expr_PrimaryLiteralChar a, PrintStat printStat) {

    }

    @Override
    public void visitStmtBlock(AST_Stmt_Block a, PrintStat p) {

    }

    @Override
    public void visitStmtDefClass(AST_Stmt_DefClass a, PrintStat printStat) {

    }

    @Override
    public void visitStmtDefFunc(AST_Stmt_DefFunc a, PrintStat printStat) {

    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, PrintStat printStat) {

    }

    @Override
    public void visitStmtExpr(AST_Stmt_Expr a, PrintStat printStat) {

    }

    @Override
    public void visitStmtIf(AST_Stmt_If a, PrintStat printStat) {

    }

    @Override
    public void visitStmtNamespace(AST_Stmt_Namespace a, PrintStat printStat) {

    }

    @Override
    public void visitStmtReturn(AST_Stmt_Return a, PrintStat printStat) {

    }

    @Override
    public void visitStmtUsing(AST_Stmt_Using a, PrintStat printStat) {

    }

    @Override
    public void visitStmtWhile(AST_Stmt_While a, PrintStat printStat) {

    }

    @Override
    public void visit_Annotation(AST__Annotation a, PrintStat printStat) {

    }

    @Override
    public void visit_Typename(AST__Typename a, PrintStat printStat) {

    }

    @Override
    public void visit_CompilationUnit(AST__CompilationUnit a, PrintStat printStat) {

    }

    //    public static void printExprPrimaryVariableName(AST_Expr_PrimaryVariableName a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "VAR{ "+a.name+" }");
//    }
//
//    public static void printExprOperBi(AST_Expr_OperBi a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "ExprOperBin {");
//            printExpr(a.left, dp+1, buf);
//            appendln(buf, dp+1, quote(a.operator));
//            printExpr(a.right, dp+1, buf);
//        appendln(buf, dp, "}");
//    }
//
//    public static void printExprOperUnaryPre(AST_Expr_OperUnaryPre a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "ExprOperUPre {");
//            appendln(buf, dp+1, quote(a.operator));
//            printExpr(a.expr, dp+1, buf);
//        appendln(buf, dp, "}");
//    }
//    public static void printExprOperUnaryPost(AST_Expr_OperUnaryPost a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "ExprOperUPost {");
//            printExpr(a.expr, dp+1, buf);
//            appendln(buf, dp+1, quote(a.operator));
//        appendln(buf, dp, "}");
//    }
//    public static void printExprFuncCall(AST_Expr_FuncCall a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "ExprFuncCall {");
//            appendln(buf, dp+1, "funcptr:");
//                printExpr(a.funcptr, dp+2, buf);
//            appendln(buf, dp+1, "args: [");
//            for (AST_Expr e : a.args) {
//                printExpr(e, dp+2, buf);
//            }
//            appendln(buf, dp+1, "]");
//        appendln(buf, dp, "}");
//    }
//    public static void printExprOperNew(AST_Expr_OperNew a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "ExprOperNew {");
//            appendln(buf, dp+1, "type: "+ AST__Typename.SimpleExpand(a.typeptr));
//            appendln(buf, dp+1, "args: [");
//            for (AST_Expr e : a.args) {
//                printExpr(e, dp+2, buf);
//            }
//            appendln(buf, dp+1, "]");
//        appendln(buf, dp, "}");
//    }
//
//    public static void printExpr(AST_Expr a, int dp, StringBuffer buf) {
////        buf.append("{T: "+a.sym+"}");
//        if (a instanceof AST_Expr_OperBi) {
//            printExprOperBi((AST_Expr_OperBi)a, dp, buf);
//        } else if (a instanceof AST_Expr_OperUnaryPre) {
//            printExprOperUnaryPre((AST_Expr_OperUnaryPre)a, dp, buf);
//        } else if (a instanceof AST_Expr_OperUnaryPost) {
//            printExprOperUnaryPost((AST_Expr_OperUnaryPost)a, dp, buf);
//        } else if (a instanceof AST_Expr_PrimaryLiteralInt) {
//            appendln(buf, dp, "INT{ "+((AST_Expr_PrimaryLiteralInt)a).numInt+" }");
//        } else if (a instanceof AST_Expr_PrimaryLiteralFloat) {
//            appendln(buf, dp, "FLOAT{ "+((AST_Expr_PrimaryLiteralFloat)a).numFloat+" }");
//        } else if (a instanceof AST_Expr_PrimaryLiteralChar) {
//            char c = ((AST_Expr_PrimaryLiteralChar)a).numUInt16;
//            appendln(buf, dp, "CHAR{ "+(int)c+" '"+c+"' }");
//        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
//            appendln(buf, dp, "STR{ "+((AST_Expr_PrimaryLiteralString)a).strRaw+" }");
//        } else if (a instanceof AST_Expr_PrimaryVariableName) {
//            printExprPrimaryVariableName((AST_Expr_PrimaryVariableName)a, dp, buf);
//        } else if (a instanceof AST_Expr_FuncCall) {
//            printExprFuncCall((AST_Expr_FuncCall) a, dp, buf);
//        } else if (a instanceof AST_Expr_OperNew) {
//            printExprOperNew((AST_Expr_OperNew) a, dp, buf);
//        } else
//            throw new IllegalStateException(a.toString());
//    }
//
//
//    public static void printStmt(AST_Stmt a, int dp, StringBuffer buf) {
//        if (a instanceof AST_Stmt_Block) {
//            printStmtBlock((AST_Stmt_Block)a, dp, buf);
//        } else if (a instanceof AST_Stmt_Blank) {
//            appendln(buf, dp, ";");
//        } else if (a instanceof AST_Stmt_Expr) {
//            printStmtExpr((AST_Stmt_Expr)a, dp, buf);
//        } else if (a instanceof AST_Stmt_Namespace) {
//            printStmtNamespace((AST_Stmt_Namespace)a, dp, buf);
//        } else if (a instanceof AST_Stmt_Using) {
//            printStmtUsing((AST_Stmt_Using)a, dp, buf);
//        } else if (a instanceof AST_Stmt_DefClass) {
//            printStmtDefClass((AST_Stmt_DefClass)a, dp, buf);
//        } else if (a instanceof AST_Stmt_DefVar) {
//            printStmtDefVar((AST_Stmt_DefVar)a, dp, buf);
//        } else if (a instanceof AST_Stmt_DefFunc) {
//            printStmtDefFunc((AST_Stmt_DefFunc)a, dp, buf);
//        } else if (a instanceof AST_Stmt_While) {
//            printStmtWhile((AST_Stmt_While)a, dp, buf);
//        } else if (a instanceof AST_Stmt_If) {
//            printStmtIf((AST_Stmt_If)a, dp, buf);
//        } else
//            throw new IllegalStateException(a.toString());
//    }
//
//    public static void printStmtBlock(AST_Stmt_Block a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtBlock [");
//        for (AST_Stmt s : a.stmts) {
//            printStmt(s, dp+1, buf);
//        }
//        appendln(buf, dp, "]");
//    }
//
//    public static void printStmtExpr(AST_Stmt_Expr a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtExpr {");
//            printExpr(a.expr, dp+1, buf);
//        appendln(buf, dp, "}");
//    }
//
//    public static void printStmtNamespace(AST_Stmt_Namespace a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtNamespace {");
//            appendln(buf, dp+1, "name: "+LxParser._ExpandQualifiedName(a.name));
//            appendln(buf, dp+1, "stmt: [");
//                for (AST_Stmt stmt : a.stmts) {
//                    printStmt(stmt, dp+2, buf);
//                }
//            appendln(buf, dp+1, "]");
//        appendln(buf, dp, "}");
//    }
//
//    public static void printStmtUsing(AST_Stmt_Using a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtUsing { "+(a.isStatic?"static ":"")+ LxParser._ExpandQualifiedName(a.used) +" }");
//    }
//
//    public static void printStmtDefClass(AST_Stmt_DefClass a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtDefClass {");
//            appendln(buf, dp+1, "name: "+a.name);
//            appendln(buf, dp+1, "superclasses: "+a.superclasses.stream().map(AST__Typename::SimpleExpand).collect(Collectors.toList()));
//            appendln(buf, dp+1, "members: [");
//            for (AST_Stmt_DefClass.AST_Class_Member mb : a.members) {
//                appendln(buf, dp+2, "M {");
//                    appendln(buf, dp+3, "annotations: "+mb.annotations.stream().map(e -> LxParser._ExpandQualifiedName(e.type)).collect(Collectors.toList()));
//                    appendln(buf, dp+3, "is_static: "+mb.isStatic());
//                    printStmt(mb.member, dp+3, buf);
//                appendln(buf, dp+2, "}");
//            }
//            appendln(buf, dp+1, "]");
//        appendln(buf, dp, "}");
//    }
//
//    public static void printStmtDefVar(AST_Stmt_DefVar a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtDefVar {");
//            appendln(buf, dp+1, "type: "+ AST__Typename.SimpleExpand(a.type));
//            appendln(buf, dp+1, "name: "+a.name);
//            if (a.initexpr != null) {
//                appendln(buf, dp+1, "initexpr:");
//                printExpr(a.initexpr, dp+2, buf);
//            }
//        appendln(buf, dp, "}");
//    }
//
//    public static void printStmtDefFunc(AST_Stmt_DefFunc a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtDefFunc {");
//            appendln(buf, dp+1, "type: "+ AST__Typename.SimpleExpand(a.returntype));
//            appendln(buf, dp+1, "name: "+a.name);
//            appendln(buf, dp+1, "params: "+a.params.stream().map(p -> AST__Typename.SimpleExpand(p.type) + " " + p.name).collect(Collectors.toList()));
//            appendln(buf, dp+1, "body:");
//                printStmtBlock(a.body, dp+2, buf);
//        appendln(buf, dp, "}");
//    }
//
//    public static void printStmtWhile(AST_Stmt_While a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtWhile {");
//            appendln(buf, dp+1, "cond:");
//                printExpr(a.condition, dp+2, buf);
//            appendln(buf, dp+1, "then:");
//                printStmt(a.then, dp+2, buf);
//        appendln(buf, dp, "}");
//    }
//
//    public static void printStmtIf(AST_Stmt_If a, int dp, StringBuffer buf) {
//        appendln(buf, dp, "StmtIf {");
//            appendln(buf, dp+1, "cond:");
//                printExpr(a.condition, dp+2, buf);
//            appendln(buf, dp+1, "then:");
//                printStmt(a.thenb, dp+2, buf);
//            if (a.elseb != null) {
//            appendln(buf, dp + 1, "else:");
//                printStmt(a.elseb, dp + 2, buf);
//            }
//        appendln(buf, dp, "}");
//    }
}
