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
        } else if (a instanceof AST_Stmt_While) {
            compileStmtWhile((AST_Stmt_While)a, buf);
        } else if (!(a instanceof AST_Stmt_Blank))
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

    public static void compileStmtWhile(AST_Stmt_While a, CodeBuf buf) {
        int begin = buf.idx();
        compileExpr(a.condition, buf);
        var c = buf._jmpifn_delay();  // if condition fail, jmp to tail.

        compileStmt(a.then, buf);
        buf._jmp(begin);  // jmp to begin.

        int tail = buf.idx();
        c.accept(tail);
    }





    /*
     * ============ EXPR ============
     */

    public static void compileExpr(AST_Expr a, CodeBuf buf) {
        if (a instanceof AST_Expr_PrimaryLiteralNumber) {
            compileExprPrimaryLiteralNumber((AST_Expr_PrimaryLiteralNumber)a, buf);
        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
            compileExprPrimaryLiteralString((AST_Expr_PrimaryLiteralString)a, buf);
        } else if (a instanceof AST_Expr_PrimaryVariableName) {
            compileExprPrimaryVariableName((AST_Expr_PrimaryVariableName)a, buf);
        } else if (a instanceof AST_Expr_FuncCall) {
            compileExprFuncCall((AST_Expr_FuncCall)a, buf);
        } else if (a instanceof AST_Expr_OperBi) {
            compileExprOperBin((AST_Expr_OperBi)a, buf);
        } else
            throw new IllegalStateException(a.toString());
    }

    public static void compileExprPrimaryLiteralString(AST_Expr_PrimaryLiteralString a, CodeBuf buf) {
        buf._ldc(buf.constantpool.ensureUtf8(a.strRaw));
    }
    public static void compileExprPrimaryLiteralNumber(AST_Expr_PrimaryLiteralNumber a, CodeBuf buf) {
        buf._ldc(buf.constantpool.ensureInt32((int)a.rawFl));
    }
    public static void compileExprPrimaryVariableName(AST_Expr_PrimaryVariableName a, CodeBuf buf) {
        buf._load(a.name);
    }
    public static void compileExprFuncCall(AST_Expr_FuncCall a, CodeBuf buf) {
        compileExpr(a.funcptr, buf);
        for (AST_Expr arg : a.args) {
            compileExpr(arg, buf);
        }
        buf._invokefunc();  // instance to static ? auto put 'this'?
    }

    public static void compileExprOperBin(AST_Expr_OperBi a, CodeBuf buf) {
        switch (a.operator) {
            case "+": {
                compileExpr(a.left, buf);
                compileExpr(a.right, buf);
//                a.sym.parNam()
                buf._i32add();
                break;
            }
            default:
                throw new IllegalStateException();
        }
    }

}
