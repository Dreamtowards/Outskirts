package outskirts.lang.langdev.interpreter.astprint;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Package;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Using;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.Symtab;
import outskirts.util.StringUtils;

import java.util.stream.Collectors;

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
public class ASTPrinter {

    public static final String indent = "  ";

    private static void appendln(StringBuffer buf, int dp, String s) {
        buf.append(indent.repeat(dp))
           .append(s)
           .append('\n');
    }
    private static String quote(String s) {
        return "'" + s + "'";
    }

    public static void printExprPrimaryLiteralNumber(AST_Expr_PrimaryLiteralNumber a, int dp, StringBuffer buf) {
        appendln(buf, dp, "NUM{ "+a.num.value+" }");
    }
    public static void printExprPrimaryLiteralString(AST_Expr_PrimaryLiteralString a, int dp, StringBuffer buf) {
        appendln(buf, dp, "STR{\""+a.str.value+"\"}");
    }
    public static void printExprPrimaryVariableName(AST_Expr_PrimaryVariableName a, int dp, StringBuffer buf) {
        appendln(buf, dp, "VAR{ "+a.name+" }");
    }

    public static void printExprOperBi(AST_Expr_OperBi a, int dp, StringBuffer buf) {
        appendln(buf, dp, "ExprOperBin {");
            printExpr(a.left, dp+1, buf);
            appendln(buf, dp+1, quote(a.operator));
            printExpr(a.right, dp+1, buf);
        appendln(buf, dp, "}");
    }

    public static void printExprOperUnaryPre(AST_Expr_OperUnaryPre a, int dp, StringBuffer buf) {
        appendln(buf, dp, "ExprOperUPre {");
            appendln(buf, dp+1, quote(a.operator));
            printExpr(a.expr, dp+1, buf);
        appendln(buf, dp, "}");
    }
    public static void printExprOperUnaryPost(AST_Expr_OperUnaryPost a, int dp, StringBuffer buf) {
        appendln(buf, dp, "ExprOperUPost {");
            printExpr(a.expr, dp+1, buf);
            appendln(buf, dp+1, quote(a.operator));
        appendln(buf, dp, "}");
    }
    public static void printExprFuncCall(AST_Expr_FuncCall a, int dp, StringBuffer buf) {
        appendln(buf, dp, "ExprFuncCall {");
            appendln(buf, dp+1, "funcptr:");
                printExpr(a.funcptr, dp+2, buf);
            appendln(buf, dp+1, "args: [");
            for (AST_Expr e : a.args) {
                printExpr(e, dp+2, buf);
            }
            appendln(buf, dp+1, "]");
        appendln(buf, dp, "}");
    }
    public static void printExprOperNew(AST_Expr_OperNew a, int dp, StringBuffer buf) {
        appendln(buf, dp, "ExprOperNew {");
            appendln(buf, dp+1, "type: "+ AST_Typename.SimpleExpand(a.typeptr));
            appendln(buf, dp+1, "args: [");
            for (AST_Expr e : a.args) {
                printExpr(e, dp+2, buf);
            }
            appendln(buf, dp+1, "]");
        appendln(buf, dp, "}");
    }

    public static void printExpr(AST_Expr a, int dp, StringBuffer buf) {
        buf.append("{T: "+a.sym+"}");
        if (a instanceof AST_Expr_OperBi) {
            printExprOperBi((AST_Expr_OperBi)a, dp, buf);
        } else if (a instanceof AST_Expr_OperUnaryPre) {
            printExprOperUnaryPre((AST_Expr_OperUnaryPre)a, dp, buf);
        } else if (a instanceof AST_Expr_OperUnaryPost) {
            printExprOperUnaryPost((AST_Expr_OperUnaryPost)a, dp, buf);
        } else if (a instanceof AST_Expr_PrimaryLiteralNumber) {
            printExprPrimaryLiteralNumber((AST_Expr_PrimaryLiteralNumber)a, dp, buf);
        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
            printExprPrimaryLiteralString((AST_Expr_PrimaryLiteralString)a, dp, buf);
        } else if (a instanceof AST_Expr_PrimaryVariableName) {
            printExprPrimaryVariableName((AST_Expr_PrimaryVariableName)a, dp, buf);
        } else if (a instanceof AST_Expr_FuncCall) {
            printExprFuncCall((AST_Expr_FuncCall) a, dp, buf);
        } else if (a instanceof AST_Expr_OperNew) {
            printExprOperNew((AST_Expr_OperNew) a, dp, buf);
        } else
            throw new IllegalStateException(a.toString());
    }


    public static void printStmt(AST_Stmt a, int dp, StringBuffer buf) {
        if (a instanceof AST_Stmt_Block) {
            printStmtBlock((AST_Stmt_Block)a, dp, buf);
        } else if (a instanceof AST_Stmt_Blank) {
            appendln(buf, dp, ";");
        } else if (a instanceof AST_Stmt_Expr) {
            printStmtExpr((AST_Stmt_Expr)a, dp, buf);
        } else if (a instanceof AST_Stmt_Package) {
            printStmtPacakge((AST_Stmt_Package)a, dp, buf);
        } else if (a instanceof AST_Stmt_Using) {
            printStmtUsing((AST_Stmt_Using)a, dp, buf);
        } else if (a instanceof AST_Stmt_DefClass) {
            printStmtDefClass((AST_Stmt_DefClass)a, dp, buf);
        } else if (a instanceof AST_Stmt_DefVar) {
            printStmtDefVar((AST_Stmt_DefVar)a, dp, buf);
        } else if (a instanceof AST_Stmt_DefFunc) {
            printStmtDefFunc((AST_Stmt_DefFunc)a, dp, buf);
        } else
            throw new IllegalStateException(a.toString());
    }

    public static void printStmtBlock(AST_Stmt_Block a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtBlock [");
        for (AST_Stmt s : a.stmts) {
            printStmt(s, dp+1, buf);
        }
        appendln(buf, dp, "]");
    }

    public static void printStmtExpr(AST_Stmt_Expr a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtExpr {");
            printExpr(a.expr, dp+1, buf);
        appendln(buf, dp, "}");
    }

    public static void printStmtPacakge(AST_Stmt_Package a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtPackage { "+ LxParser._ExpandQualifiedName(a.name) +" }");
    }
    public static void printStmtUsing(AST_Stmt_Using a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtUsing { "+(a.isStatic?"static ":"")+ LxParser._ExpandQualifiedName(a.used) +" }");
    }

    public static void printStmtDefClass(AST_Stmt_DefClass a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtDefClass {");
            appendln(buf, dp+1, "name: "+a.name);
            appendln(buf, dp+1, "superclasses: "+a.superclasses.stream().map(AST_Typename::SimpleExpand).collect(Collectors.toList()));
            appendln(buf, dp+1, "members: [");
            for (AST_Class_Member mb : a.members) {
                appendln(buf, dp+2, "M {");
                    appendln(buf, dp+3, "annotations: "+mb.annotations.stream().map(e -> LxParser._ExpandQualifiedName(e.type)).collect(Collectors.toList()));
                    appendln(buf, dp+3, "is_static: "+mb.isStatic());
                    printStmt(mb.member, dp+3, buf);
                appendln(buf, dp+2, "}");
            }
            appendln(buf, dp+1, "]");
        appendln(buf, dp, "}");
    }

    public static void printStmtDefVar(AST_Stmt_DefVar a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtDefVar {");
            appendln(buf, dp+1, "type: "+AST_Typename.SimpleExpand(a.type));
            appendln(buf, dp+1, "name: "+a.name);
            if (a.initexpr != null) {
                appendln(buf, dp+1, "initexpr:");
                printExpr(a.initexpr, dp+2, buf);
            }
        appendln(buf, dp, "}");
    }

    public static void printStmtDefFunc(AST_Stmt_DefFunc a, int dp, StringBuffer buf) {
        appendln(buf, dp, "StmtDefFunc {");
            appendln(buf, dp+1, "type: "+AST_Typename.SimpleExpand(a.returntype));
            appendln(buf, dp+1, "name: "+a.name);
            appendln(buf, dp+1, "params: "+a.params.stream().map(p -> AST_Typename.SimpleExpand(p.type) + " " + p.name).collect(Collectors.toList()));
            appendln(buf, dp+1, "body:");
            printStmtBlock((AST_Stmt_Block)a.body, dp+2, buf);
        appendln(buf, dp, "}");
    }
}
