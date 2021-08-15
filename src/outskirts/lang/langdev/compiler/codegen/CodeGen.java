package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.ast.*;

public class CodeGen {


    /*
     * ============ STMT ============
     */

    public static void compileStmt(AST_Stmt a, CodeBuf buf) {

        if (a instanceof AST_Stmt_Block) {
            compileStmtBlock((AST_Stmt_Block)a, buf);
        } else if (a instanceof AST_Stmt_DefVar) {
            compileStmtDefVar((AST_Stmt_DefVar)a, buf);
        } else if (a instanceof AST_Stmt_Expr) {
            compileExpr(((AST_Stmt_Expr)a).expr, buf);
        } else if (a instanceof AST_Stmt_Return) {
            compileStmtReturn((AST_Stmt_Return)a, buf);
        } else
            throw new IllegalStateException(a.toString());
    }

    public static void compileStmtBlock(AST_Stmt_Block a, CodeBuf buf) {
        for (AST_Stmt stmt : a.stmts) {
            compileStmt(stmt, buf);
        }
    }

    public static void compileStmtDefVar(AST_Stmt_DefVar a, CodeBuf buf) {
        buf.defvar(a.name);  // todo: AND TYPE?
        if (a.initexpr != null) {
            compileExpr(a.initexpr, buf);
            buf._store(a.name);
        }
    }

    public static void compileStmtReturn(AST_Stmt_Return a, CodeBuf buf) {
        throw new IllegalStateException();
    }






    /*
     * ============ EXPR ============
     */

    public static void compileExpr(AST_Expr a, CodeBuf buf) {
        if (a instanceof AST_Expr_PrimaryLiteralString) {
            compileExprPrimaryLiteralString((AST_Expr_PrimaryLiteralString)a, buf);
        } else if (a instanceof AST_Expr_FuncCall) {
            compileExprFuncCall((AST_Expr_FuncCall)a, buf);
        } else
            throw new IllegalStateException(a.toString());
    }

    public static void compileExprPrimaryLiteralString(AST_Expr_PrimaryLiteralString a, CodeBuf buf) {
        buf._ldc((short)buf.constantpool.ensureUtf8(a.strRaw));
    }
    public static void compileExprFuncCall(AST_Expr_FuncCall a, CodeBuf buf) {
        compileExpr(a.funcptr, buf);
        for (AST_Expr arg : a.args) {
            compileExpr(arg, buf);
        }
        buf._invokefunc();  // instance to static ? auto put 'this'?
    }

}
