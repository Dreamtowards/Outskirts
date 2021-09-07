package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.*;
import outskirts.util.Identifier;
import outskirts.util.Validate;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class CodeGen implements ASTVisitor<CodeBuf> {


    @Override
    public void visitExprPrimaryLiteral(AST_Expr_PrimaryLiteral a, CodeBuf buf) {

        switch (a.getLiteralKind()) {
            case INT32:
                buf._ldc(buf.constantpool.ensureInt32(a.getInt32()));
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, CodeBuf buf) {

        // LocalVar, Typename, Out-Var.

        buf._load(a.getName());
    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, CodeBuf buf) {
        int sz = a.getEvalTypeSymbol().typesize();
        buf._ldc(buf.constantpool.ensureInt32(sz));
    }

    @Override
    public void visitExprTmpDereference(AST_Expr_TemporaryDereference a, CodeBuf buf) {
        // exec ptr.
        a.getExpression().accept(this, buf);

        int sz = a.getTypename().sym.typesize();
        buf._ldptr(sz);
    }

    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, CodeBuf buf) {
        // std.lang.Class.innr.fld.
        // fncall().rsdat
        // a.accept(this, buf);

        AST_Expr expr = a.getExpression();

        expr.accept(this, buf);

        ScopedTypeSymbol exprsym = (ScopedTypeSymbol)expr.getEvalTypeSymbol();  // ScopedTypeSymbol.

        if (exprsym instanceof SymbolClass) {
            Symbol msym = exprsym.getTable().resolveMember(a.getIdentifier());

            if (msym instanceof SymbolVariable) {

//                buf._getfield(a.getIdentifier());
            } else {
                // SymbolFunction, ignore.
            }
        } else {
            // SymbolNamespace, ignore.
        }
    }

    private static void _visitFuncArguments(List<AST_Expr> args, CodeGen comp, CodeBuf buf) {
        for (AST_Expr e : args) {
            e.accept(comp, buf);
        }
    }

    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, CodeBuf buf) {
//        a.getExpression().accept(this, buf);

        // series().of.exprs().funcName(arg1);
        // funcName(exprs(series().of), arg1)

        TypeSymbol s = a.calleesym;

        // is that should be allowed.? for Can search static function defined in super classes, by a instance variable.

        // Invoke of Oridinary Function.
        // if (!is_static) compile(fncall.instexpr)
        // push args.
        // invoke "func"
        if (s instanceof SymbolFunction) {
            SymbolFunction sf = (SymbolFunction)s;
            String sfname = sf.getQualifiedName();

            // while an expr is static, 'call by instance-expr' is not allowed.
            if (!sf.isStaticFunction) {
                a.getExpression().accept(this, buf);
            }

            _visitFuncArguments(a.getArguments(), this, buf);

            buf._invokefunc(sfname);

        } else if (s instanceof SymbolClass) {  // stack object alloc.

            throw new UnsupportedOperationException("Unsupported StackAlloc ObjectCreation yet.");
        } else
            throw new IllegalStateException();
    }




    @Override
    public void visitStmtBlock(AST_Stmt_Block a, CodeBuf buf) {
        for (AST_Stmt stmt : a.getStatements()) {
            stmt.accept(this, buf);
        }
    }

    @Override
    public void visitStmtExpr(AST_Stmt_Expr a, CodeBuf buf) {
        AST_Expr expr = a.getExpression();

        expr.accept(this, buf);

        TypeSymbol rettyp = expr.getEvalTypeSymbol();
        if (rettyp != SymbolBuiltinType._void) {  // doSthReturnVoid();  // not ret val.
            buf._pop(expr.getEvalTypeSymbol().typesize());  // erases the expression return value.
        }
    }

    @Override
    public void visitStmtIf(AST_Stmt_If a, CodeBuf buf) {

        a.getCondition().accept(this, buf);
        IntConsumer eot = buf._jmpifn_delay();  // end of then. if condition false, goto else/end

        a.getThenStatement().accept(this, buf);

        AST_Stmt stmtelse = a.getElseStatement();
        IntConsumer eoe = null;  // end of else. after exec then, should direct jumpover else.
        if (stmtelse != null)
            eoe = buf._jmp_delay();

        eot.accept(buf.idx());  // delaysetup
        if (stmtelse != null) {
            stmtelse.accept(this, buf);

            eoe.accept(buf.idx());  // delaysetup
        }

    }

    @Override
    public void visitStmtWhile(AST_Stmt_While a, CodeBuf buf) {
        int beg = buf.idx();
        a.getCondition().accept(this, buf);
        IntConsumer eow = buf._jmpifn_delay();  // end of while.

        a.getStatement().accept(this, buf);
        buf._jmp(beg);

        eow.accept(buf.idx());  // delaysetup
    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, CodeBuf buf) {
        buf.localdef(a.getName(), a.getTypename().sym);

        AST_Expr initexpr = a.getInitializer();
        if (initexpr != null) {
            initexpr.accept(this, buf);

            buf._store(a.getName());
        }
    }





    @Override
    public void visitExprOperBinary(AST_Expr_OperBinary a, CodeBuf buf) {
        AST_Expr lhs = a.getLeftOperand();
        AST_Expr rhs = a.getRightOperand();

        if (a.getBinaryKind() == AST_Expr_OperBinary.BinaryKind.ASSIGN) {
            if (lhs instanceof AST_Expr_PrimaryIdentifier) {
                // exec val.
                rhs.accept(this, buf);
                buf._dup(rhs.getEvalTypeSymbol().typesize());

                buf._store(((AST_Expr_PrimaryIdentifier)lhs).getName());

            } else if (lhs instanceof AST_Expr_TemporaryDereference) {
                AST_Expr_TemporaryDereference lhsc = (AST_Expr_TemporaryDereference)lhs;

                rhs.accept(this, buf);
                buf._dup(rhs.getEvalTypeSymbol().typesize());

                lhsc.getExpression().accept(this, buf);

                buf._stptr(lhsc.getTypename().sym.typesize());

                Validate.isTrue(rhs.getEvalTypeSymbol().typesize() == lhsc.getTypename().sym.typesize());  // should already validated in Attr phase.
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            lhs.accept(this, buf);
            rhs.accept(this, buf);

            switch (a.getBinaryKind()) {
                case ADD: {
                    buf._i32add();
                    break;
                }
                case MUL: {
                    buf._i32mul();
                    break;
                }
                case LT: {
                    buf._icmp();
                    buf._cmplt();
                    break;
                }
                case EQ: {
                    buf._icmp();
                    buf._cmpeq();
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
