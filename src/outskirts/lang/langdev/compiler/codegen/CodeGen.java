package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.*;
import outskirts.util.Validate;

import java.util.List;
import java.util.function.IntConsumer;

import static outskirts.lang.langdev.symtab.SymbolBuiltinType.*;

public class CodeGen implements ASTVisitor<CodeBuf> {


    @Override
    public void visitExprPrimaryLiteral(AST_Expr_PrimaryLiteral a, CodeBuf buf) {

        switch (a.getLiteralKind()) {
            case INT32:
                buf._ldc_i(a.getInt32());
                break;
            case CHAR:
                buf._ldc_i(a.getChar());
                break;
            case STRING:
                buf._ldc_str(a.getString());
                break;
//            case BOOL:
//                buf._ldc_b(a.getBool() ? 1 : 0);
//                break;
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
        buf._ldc_i(sz);
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
                // TODO: why..? how about "nothing.?"
                // shouldn't. if do, then ref can't bring it back to lvalue.
                // int* p = &i;  // i: addr=0x4, p: addr=0x8
                // p == &*p  // should true.

                // Edit: there seems no problem. Deref just get the actual-address. rval doing-nothing, lval get-addr.
                lvalue2rvalue(buf, expr);
                break;
            case PRE_INC:
            case PRE_DEC:
                Validate.isTrue(expr.getVarTypeSymbol() == _int);
                buf._dup(SymbolBuiltinTypePointer.PTR_SIZE);
                    buf._dup(SymbolBuiltinTypePointer.PTR_SIZE);
                    buf._loadv(_int.getTypesize());
                    buf._ldc_i(1);
                    if (a.getUnaryKind() == AST_Expr_OperUnary.UnaryKind.PRE_INC)
                        buf._add_i32();
                    else
                        buf._sub_i32();
                buf._popcpy(_int.getTypesize());
                break;
            case POST_INC:
                // pure stack-based & base on one ptr, its seems can't do the post_inc/dec functionality.
                // at beginning there have a lvalue, then it's rvalue have to be pushed firstly, then inc it and update, funally returns the rvalue.
                // but since the rvalue been pushed, the lvalue can't be got anymore. thus, cannot inc.
                buf._inc_i32();
                break;
            case POST_DEC:
                buf._dec_i32();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void visitExprOperNewMalloc(AST_Expr_OperNewMalloc a, CodeBuf buf) {
        a.getSizeExpression().accept(this, buf);

        buf._malloc();
    }

    @Override
    public void visitExprTypeCast(AST_Expr_TypeCast a, CodeBuf buf) {
        AST_Expr expr = a.getExpression();
        expr.accept(this, buf);  // currently, cast only symbol cast. then just pass.

        TypeSymbol srctyp = expr.getVarTypeSymbol();
        TypeSymbol dsttyp = a.getType().getTypeSymbol();
        if (srctyp == _byte && dsttyp == _int) {
            lvalue2rvalue(buf, expr);
            buf._cast_i8_i32();
        } else if (srctyp == _int && dsttyp == _byte) {
            lvalue2rvalue(buf, expr);
            buf._cast_i32_i8();
        } else if (srctyp instanceof SymbolBuiltinTypePointer && dsttyp == _int ||
                   srctyp == _int && dsttyp instanceof SymbolBuiltinTypePointer ||
                   srctyp == _byte && dsttyp == _bool) {
            lvalue2rvalue(buf, expr);
        } else {
            Validate.isTrue(srctyp == dsttyp);
        }
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
            SymbolClass typ;
            if (a.isArrow()) {
                typ = (SymbolClass) ((SymbolBuiltinTypePointer)sv.getType()).getPointerType();
                lvalue2rvalue(buf, expr);
            } else {
                typ = (SymbolClass) sv.getType();
            }
            Symbol ms = typ.getSymbolTable().resolveMember(a.getIdentifier());

            if (ms instanceof SymbolVariable) {
                int off = typ.memoffset(a.getIdentifier());
                if (sv.hasAddress() || a.isArrow()) {  // ptr offset.
                    buf._ldc_i(off);
                    buf._add_i32();
                } else {  // silce.
                    int sz = ((SymbolVariable)ms).getType().getTypesize();

                    buf._stkptroff(-typ.getTypesize());
                    buf._stkptroff(-typ.getTypesize()+off-4);  // -4: prev pushed typesize.
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

            // Prepare Args.
            //   only non-static function needs executes expr.
            //   while an expr is static, 'call by instance-expr' is not allowed.
            if (!sf.isStatic()) {
                if (expr instanceof AST_Expr_MemberAccess) {
                    AST_Expr_MemberAccess m = (AST_Expr_MemberAccess)expr;
                    AST_Expr ivkr = m.getExpression();
                    ivkr.accept(this, buf);
                    if (m.isArrow()) {
                        Validate.isTrue(ivkr.getVarTypeSymbol() instanceof SymbolBuiltinTypePointer);
                        lvalue2rvalue(buf, ivkr);
                    }
                }
            }
            _visitFuncArguments(a.getArguments(), this, buf);

            buf._invokefunc(sfname);

        } else if (s instanceof SymbolClass) {  // stack object alloc.
            // string s = string();  // alloc mem, call init.  then assign to var.
            SymbolClass cl = (SymbolClass)s;

            buf._stackalloc(cl.getTypesize());

        } else  // or a variable.?
            throw new IllegalStateException();
    }


    @Override
    public void visitStmtBlock(AST_Stmt_Block a, CodeBuf buf) {
        for (AST_Stmt stmt : a.getStatements()) {
            stmt.accept(this, buf);
        }
    }


    @Override
    public void visitStmtBreak(AST_Stmt_Break a, CodeBuf buf) {

        CodeBuf.BrLoopInf l = buf.getEnclosingLoopStack().peek();
        Validate.isTrue(l != null, "No enclosing loop.");

        IntConsumer ltr = buf._jmp_delay();
        l.end_ip_onDefined.add(ltr);
    }

    // visitStmtGoto:
    //     String l = a.getLabel();
    //     IntConsumer ltr = buf._jmp_delay();
    //     buf.labels_onDefined.add(pair<>(l, ltr));

    @Override
    public void visitStmtContinue(AST_Stmt_Continue a, CodeBuf buf) {

        CodeBuf.BrLoopInf l = buf.getEnclosingLoopStack().peek();
        Validate.isTrue(l != null, "No enclosing loop.");

        buf._jmp(l.beg_ip);
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
        CodeBuf.BrLoopInf linf = new CodeBuf.BrLoopInf();
        linf.beg_ip = buf.idx();
        buf.getEnclosingLoopStack().push(linf);

        // cond.
        a.getCondition().accept(this, buf);
        IntConsumer endofwhile = buf._jmpifn_delay();  // end of while.
        linf.end_ip_onDefined.add(endofwhile);

        // body.
        a.getStatement().accept(this, buf);
        buf._jmp(linf.beg_ip);

        buf.getEnclosingLoopStack().pop();
        // end_ip onDefined.
        int end_ip = buf.idx();
        for (IntConsumer f : linf.end_ip_onDefined) {
            f.accept(end_ip);
        }
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

            int sz = lhs.getVarTypeSymbol().getTypesize(); Validate.isTrue(sz > 0, "Invalid assignment. zero copies.");
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
            Validate.isTrue(lhs.getVarTypeSymbol() == _int && rhs.getVarTypeSymbol() == _int, "Unsupported type: "+lhs.getVarTypeSymbol());

            switch (a.getBinaryKind()) {
                case ADD: {
                    buf._add_i32();
                    break;
                }
                case MUL: {
                    buf._mul_i32();
                    break;
                }
                case SUB: {
                    buf._sub_i32();
                    break;
                }
                case LT: {
                    buf._icmp();
                    buf._cmplt();
                    break;
                }
                case LTEQ: {
                    buf._icmp();
                    buf._cmple();
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
