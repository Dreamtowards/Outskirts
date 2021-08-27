package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.ast.*;

import java.util.function.Consumer;

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
            compileStmtExpr((AST_Stmt_Expr)a, buf);
        } else if (a instanceof AST_Stmt_Return) {
            compileStmtReturn((AST_Stmt_Return)a, buf);
        } else if (a instanceof AST_Stmt_While) {
            compileStmtWhile((AST_Stmt_While)a, buf);
        } else if (a instanceof AST_Stmt_If) {
            compileStmtIf((AST_Stmt_If)a, buf);
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

    public static void compileStmtExpr(AST_Stmt_Expr a, CodeBuf buf) {
        compileExpr(a.expr, buf);
//        buf._pop();  // do waste.
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

    public static void compileStmtIf(AST_Stmt_If a, CodeBuf buf) {
        compileExpr(a.condition, buf);
        var ef = buf._jmpifn_delay();  // if not, goto else/endif

        compileStmt(a.thenb, buf);
        Consumer<Integer> efe = null;
        if (a.elseb != null) {  // if has else, go endelse.
            efe = buf._jmp_delay();
        }

        ef.accept(buf.idx());
        if (a.elseb != null) {
            compileStmt(a.elseb, buf);

            efe.accept(buf.idx());
        }
    }





    /*
     * ============ EXPR ============
     */

    public static void compileExpr(AST_Expr a, CodeBuf buf) {
        if (a instanceof AST_Expr_PrimaryLiteralInt) {
            compileExprPrimaryLiteralInt((AST_Expr_PrimaryLiteralInt)a, buf);
        } else if (a instanceof AST_Expr_PrimaryLiteralString) {
            compileExprPrimaryLiteralString((AST_Expr_PrimaryLiteralString)a, buf);
        } else if (a instanceof AST_Expr_PrimaryIdentifier) {
            compileExprPrimaryVariableName((AST_Expr_PrimaryIdentifier)a, buf);
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
    public static void compileExprPrimaryLiteralInt(AST_Expr_PrimaryLiteralInt a, CodeBuf buf) {
        buf._ldc(buf.constantpool.ensureInt32((int)a.numInt));
    }
    public static void compileExprPrimaryVariableName(AST_Expr_PrimaryIdentifier a, CodeBuf buf) {
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
        if (a.operator.equals("=")) {
            if (a.left instanceof AST_Expr_PrimaryIdentifier) {
                compileExpr(a.right, buf);
                buf._dup();
                buf._store(a.left.varname());
            } else {
                throw new IllegalStateException();
            }
            return;
        }

        compileExpr(a.left, buf);
        compileExpr(a.right, buf);

        switch (a.operator) {
            case "+": {
                buf._i32add();
                break;
            }
            case "<": {
                buf._icmp();
                buf._cmplt();
                break;
            }
            case "==": {
                buf._icmp();
                buf._cmpeq();
                break;
            }
            default:
                throw new IllegalStateException("Bad opr "+a.operator);
        }
    }

}
