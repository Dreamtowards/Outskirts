package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.*;
import outskirts.util.Validate;

import java.util.List;
import java.util.function.IntConsumer;

public class CodeGen implements ASTVisitor<CodeBuf> {


    @Override
    public void visitExprPrimaryLiteral(AST_Expr_PrimaryLiteral a, CodeBuf buf) {

        switch (a.getLiteralKind()) {
            case INT32:
                buf._ldv_i(a.getInt32());
                break;
            case CHAR:
                buf._ldv_i(a.getChar());
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, CodeBuf buf) {

        // LocalVar, Typename, Out-Var.

        buf._lloadp(a.getName());
    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, CodeBuf buf) {
        int sz = a.getTypeExpression().getTypeSymbol().getTypesize();
        buf._ldv_i(sz);
    }

//    @Override
//    public void visitExprTmpDereference(AST_Expr_TmpDereference a, CodeBuf buf) {
//        // exec ptr.
//        AST_Expr expr = a.getExpression();
//        expr.accept(this, buf);
//
//        if (expr.getVarSymbol().hasAddress()) {
//            buf._loadv(4);  // loadup actual addr value.
//        }
////        System.out.println("Deref: "+a.getVarSymbol().hasAddress());
//    }
//
//    @Override
//    public void visitExprTmpReference(AST_Expr_TmpReference a, CodeBuf buf) {
//        // push the variable addr into the stack.
//        a.getExpression().accept(this, buf);
//    }

    @Override
    public void visitExprOperUnary(AST_Expr_OperUnary a, CodeBuf buf) {
        AST_Expr expr = a.getExpression();
        expr.accept(this, buf);

        switch (a.getUnaryKind()) {
            case REF:
                // nothing.
                break;
            case DEREF:
                lvalue2rvalue(buf, expr);
                break;
            case POST_INC:
                // i++
                // ptr
                // dup ptr
                // loadv
                //
                //   dup ptr
                //   loadv
                //   push 1
                //   iadd
                //   pstore
                //

//                Validate.isTrue(expr.getVarSymbol().hasAddress());
//                lvalue_loadv(buf, expr);
//                buf._dup(SymbolBuiltinTypePointer.PTR_SIZE);

//                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void visitExprTypeCast(AST_Expr_TypeCast a, CodeBuf buf) {
        a.getExpression().accept(this, buf);  // currently cast only symbol cast. then just pass.
    }

    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, CodeBuf buf) {
        // std.lang.Class.innr.fld.
        // fncall().rsdat

        AST_Expr expr = a.getExpression();
        expr.accept(this, buf);

        Symbol s = expr.getExprSymbol();

        if (s instanceof SymbolClass) {
            Symbol ms = ((SymbolClass)s).getSymbolTable().resolveMember(a.getIdentifier());

            if (ms instanceof SymbolVariable) {

                throw new IllegalStateException("Unsupported get static member variable");
            } else {
                // SymbolFunction, ignore.
                throw new IllegalStateException("Unsupported get static function address member.");
            }
        } else if (s instanceof SymbolVariable) {
            SymbolVariable sv = (SymbolVariable)s;
            SymbolClass typ = (SymbolClass)sv.getType();
            Symbol ms = typ.getSymbolTable().resolveMember(a.getIdentifier());

            if (ms instanceof SymbolVariable) {
                int off = typ.memoffset(a.getIdentifier());
                if (sv.hasAddress()) {  // ptr offset.
                    buf._ldv_i(off);
                    buf._i32add();
                } else {  // silce.
                    int sz = ((SymbolVariable)ms).getType().getTypesize();

                    buf._stkptroff(-typ.getTypesize());
                    buf._stkptroff(-typ.getTypesize()+off-4);
                    buf._ptrcpy(sz);

                    buf._pop(typ.getTypesize()-sz);
                }
            } else {
                // SymbolFunction, ignore.
                throw new IllegalStateException("Unsupported get function address member.");
            }
        } else if (!(s instanceof SymbolNamespace)) {  // SymbolNamespace can be ignore.
            throw new IllegalStateException();
        }

    }

    private static void _visitFuncArguments(List<AST_Expr> args, CodeGen comp, CodeBuf buf) {
        for (AST_Expr e : args) {
            e.accept(comp, buf);
            lvalue2rvalue(buf, e);
        }
    }

    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, CodeBuf buf) {
        AST_Expr expr = a.getExpression();
        Symbol s = expr.getExprSymbol();

        // series().of.exprs().funcName(arg1);
        // funcName(exprs(series().of), arg1)

        // Invoke of Oridinary Function.
        // if (!is_static) compile(fncall.instexpr)
        // push args.
        // invoke "func"
        if (s instanceof SymbolFunction) {
            SymbolFunction sf = (SymbolFunction)s;
            String sfname = sf.getQualifiedName();

            // only non-static function needs executes expr.
            // while an expr is static, 'call by instance-expr' is not allowed.
            if (!sf.isStatic()) {
                if (expr instanceof AST_Expr_MemberAccess) {
                    ((AST_Expr_MemberAccess)expr).getExpression()
                            .accept(this, buf);
                }
            }

            _visitFuncArguments(a.getArguments(), this, buf);

            buf._invokefunc(sfname);

        } else if (s instanceof SymbolClass) {  // stack object alloc.
            // string s = string();  // alloc mem, call init.  then assign to var.
            SymbolClass cl = (SymbolClass)s;
            String clname = cl.getQualifiedName();

            buf._stackalloc(clname);

        } else  // or a variable.?
            throw new IllegalStateException();
    }


    @Override
    public void visitStmtReturn(AST_Stmt_Return a, CodeBuf buf) {
        AST_Expr expr = a.getReturnExpression();
        if (expr != null) {
            expr.accept(this, buf);
            lvalue2rvalue(buf, expr);

            buf._ret(expr.getVarTypeSymbol().getTypesize());
        } else {
            buf._ret(0);
        }
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

        SymbolVariable sv = expr.getVarSymbol();
        if (sv.getType() != SymbolBuiltinType._void) {  // doSthReturnVoid();  // not ret val.
            if (sv.hasAddress()) {
                buf._pop(SymbolBuiltinTypePointer.PTR_SIZE);
            } else {
                buf._pop(sv.getType().getTypesize());  // erases the expression return value.
            }
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
        buf.localdef(a.getName(), a.getTypeExpression().getTypeSymbol());

        AST_Expr initexpr = a.getInitializer();
        if (initexpr != null) {
            buf._lloadp(a.getName());

            initexpr.accept(this, buf);

            int sz = a.getTypeExpression().getTypeSymbol().getTypesize();
            if (initexpr.getVarSymbol().hasAddress()) {
                buf._ptrcpy(sz);
            } else {
                buf._popcpy(sz);
            }
        }
    }


    // lvalue to rvalue.
    // currently only for int (binary-op, dereference-addr). will it working for composied types.?
    private static void lvalue2rvalue(CodeBuf buf, AST_Expr expr) {
        if (expr.getVarSymbol().hasAddress()) {
            buf._loadv(expr.getVarTypeSymbol().getTypesize());
        }
    }


    @Override
    public void visitExprOperBinary(AST_Expr_OperBinary a, CodeBuf buf) {
        AST_Expr lhs = a.getLeftOperand();
        AST_Expr rhs = a.getRightOperand();

        if (a.getBinaryKind() == AST_Expr_OperBinary.BinaryKind.ASSIGN) {
            Validate.isTrue(lhs.getVarSymbol().hasAddress());

            lhs.accept(this, buf);  // put lhs addr.
            buf._dup(SymbolBuiltinTypePointer.PTR_SIZE);

            rhs.accept(this, buf);  // put rhs addr(lval)/value(rval)

            int sz = lhs.getVarTypeSymbol().getTypesize();
            if (rhs.getVarSymbol().hasAddress()) {  // rhs: lvalue
                buf._ptrcpy(sz);
            } else {  // rhs: rvalue
                buf._popcpy(sz);
            }

//            if (lhs instanceof AST_Expr_PrimaryIdentifier) {
//                // exec val.
//                rhs.accept(this, buf);
//                buf._dup(rhs.getVarTypeSymbol().getTypesize());  // the eval-expr really returns a TypeLiteral.? think there should a instance-variable thing.
//
//                buf._store(((AST_Expr_PrimaryIdentifier)lhs).getName());
//
//            } else if (lhs instanceof AST_Expr_TmpDereference) {
//                AST_Expr_TmpDereference lc = (AST_Expr_TmpDereference)lhs;
//
//                rhs.accept(this, buf);
//                buf._dup(rhs.getTypeSymbol().getTypesize());  // SymVar.?
//
//                lc.getExpression().accept(this, buf);
//
//                buf._stptr(lc.getTypeExpression().getTypeSymbol().getTypesize());  // rhs.getTypeSymbol().typesize() should be same as prev dup.
//
//                Validate.isTrue(rhs.getTypeSymbol().getTypesize() == lc.getTypeExpression().getTypeSymbol().getTypesize());  // should already validated in Attr phase.
//            } else if (lhs instanceof AST_Expr_MemberAccess) {
//                AST_Expr_MemberAccess lhs_ = (AST_Expr_MemberAccess)lhs;
//                AST_Expr lhs_lexpr = lhs_.getExpression();
//
//                // load lhs addr
//                // load val
//                // storefield lhsType::fld
//
//                // this.a.b.c = 2+3;
//                // ldloc $this
//                // ldfld $a
//                // ldfld $b
//                // ldfld $c
//
//                // addr?
//                // lhs_lexpr.accept(this, buf);
////                if (lhs_lexpr instanceof AST_Expr_PrimaryIdentifier) {
////                    String locname = ((AST_Expr_PrimaryIdentifier)lhs_lexpr).getName();
////                    buf._lloadaddr(buf.loca)
////                } else
////                    throw new IllegalStateException();
////
////                // val.
////                rhs.accept(this, buf);
////
////                buf._putfield(lhs_lexpr.getEvalTypeSymbol().getQualifiedName()+"."+lhs_.getIdentifier());
//                throw new UnsupportedOperationException();
//            } else {
//                throw new UnsupportedOperationException();
//            }
        } else {
            lhs.accept(this, buf);
            lvalue2rvalue(buf, lhs);

            rhs.accept(this, buf);
            lvalue2rvalue(buf, rhs);

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
                case NEQ: {
                    buf._icmp();
                    buf._cmpne();
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
