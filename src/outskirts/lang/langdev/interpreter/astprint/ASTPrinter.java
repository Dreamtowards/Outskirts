package outskirts.lang.langdev.interpreter.astprint;

import outskirts.lang.langdev.ast.*;
import outskirts.util.StringUtils;

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

    public String indent = "  ";
    public StringBuffer buf = new StringBuffer();

    private void appendln(int dp, String s) {
        buf.append(indent.repeat(dp))
           .append(s)
           .append('\n');
    }
    private String quote(String s) {
        return "'" + s + "'";
    }

    public void printExprPrimaryLiteralNumber(AST_Expr_PrimaryLiteralNumber a, int dp) {
        appendln(dp, "NUM{ "+a.num.value+" }");
    }
    public void printExprPrimaryLiteralString(AST_Expr_PrimaryLiteralString a, int dp) {
        appendln(dp, "STR{\""+a.str.value+"\"}");
    }
    public void printExprPrimaryVariableName(AST_Expr_PrimaryVariableName a, int dp) {
        appendln(dp, "VAR{ "+a.name+" }");
    }

    public void printExprOperBi(AST_Expr_OperBi a, int dp) {
        appendln(dp, "EXPR_OPER_BI {");
            printExpr(a.left, dp+1);
            appendln(dp+1, quote(a.operator));
            printExpr(a.right, dp+1);
        appendln(dp, "}");
    }

    public void printExprOperUnaryPre(AST_Expr_OperUnaryPre a, int dp) {
        appendln(dp, "EXPR_OPER_UPRE {");
            appendln(dp+1, quote(a.operator));
            printExpr(a.expr, dp+1);
        appendln(dp, "}");
    }
    public void printExprOperUnaryPost(AST_Expr_OperUnaryPost a, int dp) {
        appendln(dp, "EXPR_OPER_UPOST {");
            printExpr(a.expr, dp+1);
            appendln(dp+1, quote(a.operator));
        appendln(dp, "}");
    }

    public void printExpr(AST_Expr a, int dp) {
        if (a instanceof AST_Expr_OperBi) {
            printExprOperBi((AST_Expr_OperBi)a, dp);
        } else if (a instanceof AST_Expr_OperUnaryPre) {
            printExprOperUnaryPre((AST_Expr_OperUnaryPre)a, dp);
        } else if (a instanceof AST_Expr_OperUnaryPost) {
            printExprOperUnaryPost((AST_Expr_OperUnaryPost)a, dp);
        } else if (a instanceof AST_Expr_PrimaryLiteralNumber) {
            printExprPrimaryLiteralNumber((AST_Expr_PrimaryLiteralNumber)a, dp);
        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
            printExprPrimaryLiteralString((AST_Expr_PrimaryLiteralString)a, dp);
        } else if (a instanceof AST_Expr_PrimaryVariableName) {
            printExprPrimaryVariableName((AST_Expr_PrimaryVariableName)a, dp);
        } else
            throw new IllegalStateException();
    }



}
