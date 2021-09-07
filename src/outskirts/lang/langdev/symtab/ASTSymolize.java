package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTSymolize implements ASTVisitor<Scope> {

    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, Scope p) {
//        idenExpr(a.funcptr, scope);
//        SymbolFunction sf = (SymbolFunction)a.funcptr.evaltype;
//        a.evaltype = sf.returntype;

        AST_Expr fnexpr = a.getExpression();

        fnexpr.accept(this, p);

        TypeSymbol s = fnexpr.getEvalTypeSymbol();
        a.calleesym = s;

//        System.out.println("Found Function Invokation, ExprType: "+s+"/"+s.getQualifiedName());

        if (s instanceof SymbolFunction) {  // Originally 'Exact' Function Calling.  expr_primary.iden(..) | iden(..)
            Validate.isTrue(fnexpr instanceof AST_Expr_PrimaryIdentifier ||
                                 fnexpr instanceof AST_Expr_MemberAccess);

            // when a function is static, dont allows it been called from instance context. like: instExpr.stfunc();
            // this may not a good way to check.
            if (((SymbolFunction)s).isStaticFunction && fnexpr instanceof AST_Expr_MemberAccess) {
                // pkg.to.Class.Innr.stFunc();
                AST_Expr prev = ((AST_Expr_MemberAccess)fnexpr).getExpression();
                Validate.isTrue(prev.getEvalTypeSymbol() instanceof SymbolNamespace ||
                                prev.getEvalTypeSymbol() instanceof SymbolClass);  // unavailable check.
            }

            a.setEvalTypeSymbol(((SymbolFunction)s).returntype);
        } else if (s instanceof SymbolClass){  // Construction of Stack-Alloc-Object-Creation.  type(..).

            a.setEvalTypeSymbol(s);
        } else {  // otherwise expr. Invoke "Invokation ()" operator to that object. or not.  // but still shoud in case of return-type SymbolClass.?
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, Scope p) {
        a.getExpression().accept(this, p);

        ScopedTypeSymbol l = (ScopedTypeSymbol)a.getExpression().getEvalTypeSymbol();
        Symbol v = l.getTable().resolveMember(a.getIdentifier());  // Var / Func. / Namespace..

        a.setEvalTypeSymbol(v instanceof SymbolVariable ? ((SymbolVariable)v).type : (TypeSymbol)v);
    }

    @Override
    public void visitExprOperNew(AST_Expr_OperNew a, Scope p) {
        a.getTypename().accept(this, p);
        a.setEvalTypeSymbol(a.getTypename().sym);
    }

    @Override
    public void visitExprOperConditional(AST_Expr_OperConditional a, Scope p) {
        a.getCondition().accept(this, p);
        a.getTrueExpression().accept(this, p);
        a.getFalseExpression().accept(this, p);

        Validate.isTrue(a.getTrueExpression().getEvalTypeSymbol() == a.getFalseExpression().getEvalTypeSymbol());
    }

    @Override
    public void visitExprOperUnary(AST_Expr_OperUnary a, Scope p) {
        a.getExpression().accept(this, p);

        if (a.getUnaryKind() == AST_Expr_OperUnary.UnaryKind.NOT) {
            a.setEvalTypeSymbol(SymbolBuiltinType._bool);
        } else {
            a.setEvalTypeSymbol(a.getExpression().getEvalTypeSymbol());
        }
    }

    @Override
    public void visitExprOperBinary(AST_Expr_OperBinary a, Scope p) {
        a.getLeftOperand().accept(this, p);
        a.getRightOperand().accept(this, p);

        if (a.getLeftOperand().getEvalTypeSymbol() != a.getRightOperand().getEvalTypeSymbol())
            throw new UnsupportedOperationException("Incpompactble OperBin "+a.getLeftOperand().getEvalTypeSymbol().getQualifiedName()+", "+a.getRightOperand().getEvalTypeSymbol().getQualifiedName());

        switch (a.getBinaryKind()) {
            case LT: case LTEQ: case GT: case GTEQ: case IS:
            case EQ: case NEQ:
                a.setEvalTypeSymbol(SymbolBuiltinType._bool);
                break;
            default:
                a.setEvalTypeSymbol(a.getLeftOperand().getEvalTypeSymbol());  // return commonBaseType(e1, e2);
                break;
        }
    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, Scope p) {
        a.getTypename().accept(this, p);
        a.setEvalTypeSymbol(SymbolBuiltinType._int);
    }

    @Override
    public void visitExprTmpDereference(AST_Expr_TemporaryDereference a, Scope p) {
        a.getTypename().accept(this, p);
        a.getExpression().accept(this, p);
        Validate.isTrue(a.getExpression().getEvalTypeSymbol() == SymbolBuiltinType._int);

        a.setEvalTypeSymbol(a.getTypename().sym);
    }

    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, Scope p) {
        Symbol s = p.resolve(a.getName());
        a.sym = s;

        if (s instanceof SymbolVariable)
            a.setEvalTypeSymbol(((SymbolVariable)s).type);
        else
            a.setEvalTypeSymbol((TypeSymbol)s);  // SymbolClass or SymbolNamespace.
    }

    @Override
    public void visitExprPrimaryLiteral(AST_Expr_PrimaryLiteral a, Scope p) {
        TypeSymbol s;
        switch (a.getLiteralKind()) {
            case INT32: s = SymbolBuiltinType._int;  break;
            case BOOL:  s = SymbolBuiltinType._bool; break;
            default:
                throw new IllegalStateException("unsupported literal.");
        }
        a.setEvalTypeSymbol(s);
    }

    @Override
    public void visitStmtBlock(AST_Stmt_Block a, Scope _p) {
        Scope blp = new Scope(_p);

        for (AST_Stmt stmt : a.getStatements()) {
            stmt.accept(this, blp);
        }
    }

    @Override
    public void visitStmtDefClass(AST_Stmt_DefClass a, Scope _p) {

        for (AST__Typename sup : a.getSuperTypenames()) {
            sup.accept(this, _p);
        }

        Scope clp = new Scope(_p);
        SymbolClass clsym = new SymbolClass(a.getSimpleName(), clp);
        clp.symbolAssociated = clsym;   // before member. members need link the owner_class.
        _p.define(clsym);  // before member. member should can lookup enclosing class symbol.
        a.sym = clsym;

        for (AST_Stmt clstmt : a.getMembers()) {

            clstmt.accept(this, clp);
        }

    }

    @Override
    public void visitStmtDefFunc(AST_Stmt_DefFunc a, Scope _p) {

        a.getReturnTypename().accept(this, _p);

        Scope fnp = new Scope(_p);

        List<TypeSymbol> paramsyms = new ArrayList<>(a.getParameters().size());
        for (AST_Stmt_DefVar param : a.getParameters()) {
            param.accept(this, fnp);
            paramsyms.add(param.getTypename().sym);
        }

        SymbolFunction sf = new SymbolFunction(a.getName(), paramsyms, a.getReturnTypename().sym, (SymbolClass)_p.symbolAssociated);
        sf.isStaticFunction = a.getModifiers().isStatic();
        fnp.symbolAssociated = sf;  // before body. return_stmt needs lookupEnclosingFunction to validates return-type.
        a.symf = sf;
        _p.define(sf);  // before body. recursive funcCall should can resolve self-calling.

        a.getBody().accept(this, fnp);

//        SymbolVariable sym = new SymbolVariable(a.getName(), sf);
//        _p.define(sym);
    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, Scope p) {
        a.getTypename().accept(this, p);

        SymbolVariable sym = new SymbolVariable(a.getName(), a.getTypename().sym);
        p.define(sym);

        if (a.getInitializer() != null) {
            a.getInitializer().accept(this, p);

            Validate.isTrue(a.getTypename().sym == a.getInitializer().getEvalTypeSymbol(), "bad init type");  // might should let Semantic do.
        }
    }

    @Override
    public void visitStmtExpr(AST_Stmt_Expr a, Scope p) {
        a.getExpression().accept(this, p);
    }

    @Override
    public void visitStmtIf(AST_Stmt_If a, Scope p) {
        a.getCondition().accept(this, p);
        Validate.isTrue(a.getCondition().getEvalTypeSymbol() == SymbolBuiltinType._bool);  // early.

        a.getThenStatement().accept(this, p);

        if (a.getElseStatement() != null) {
            a.getElseStatement().accept(this, p);
        }
    }

    @Override
    public void visitStmtNamespace(AST_Stmt_Namespace a, Scope _p) {
        Scope lp = _p;

        for (String nm : StringUtils.explode(LxParser._ExpandQualifiedName(a.getNameExpression()), ".")) {
            SymbolNamespace ns = (SymbolNamespace)lp.findLocalSymbol(nm);
            if (ns != null) {
                lp = ns.getTable();
            } else {
                Scope np = new Scope(lp);
                ns = new SymbolNamespace(nm, np);
                np.symbolAssociated = ns;

                lp.define(ns);
                lp = np;
            }
        }

        for (AST_Stmt stmt : a.getStatements()) {
            stmt.accept(this, lp);
        }
    }

    @Override
    public void visitStmtReturn(AST_Stmt_Return a, Scope p) {
        SymbolFunction sf = p.lookupEnclosingFuncction();

        AST_Expr retexpr = a.getReturnExpression();
        if (retexpr != null) {
            retexpr.accept(this, p);

            Validate.isTrue(sf.getReturnType() == retexpr.getEvalTypeSymbol(), "Expected returning type: "+sf.getReturnType()+", actual returning: "+retexpr.getEvalTypeSymbol());
        } else {
            Validate.isTrue(sf.getReturnType() == SymbolBuiltinType._void, "function return-type is not void, required returning: "+sf.getReturnType());
        }
    }

    @Override
    public void visitStmtUsing(AST_Stmt_Using a, Scope p) {
        Symbol used = p.resolveQualifiedExpr(a.getQualifiedExpression());

        Validate.isTrue(a.isStatic() ? (used instanceof SymbolVariable) : (used instanceof SymbolClass));

        p.defineAsCustomName(a.getDeclaredName(), used);
    }

    @Override
    public void visitStmtWhile(AST_Stmt_While a, Scope p) {
        a.getCondition().accept(this, p);

        a.getStatement().accept(this, p);
    }

    @Override
    public void visit_Annotation(AST__Annotation a, Scope p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit_Typename(AST__Typename a, Scope p) {
        a.sym = p.resolveQualifiedExpr(a.getType());  // GenericVariable
    }

    @Override
    public void visit_CompilationUnit(AST__CompilationUnit a, Scope p) {

        ASTVisitor._VisitStmts(this, a.getDeclrations(), p);

    }
}
