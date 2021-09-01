package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

public class ASTSymolEnter implements ASTVisitor<Scope> {

    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, Scope p) {
//        idenExpr(a.funcptr, scope);
//        SymbolFunction sf = (SymbolFunction)a.funcptr.evaltype;
//        a.evaltype = sf.returntype;

        a.getExpression().accept(this, p);

        TypeSymbol s = a.getExpression().getEvalTypeSymbol();
        a.fsym = s;

        if (s instanceof SymbolFunction) {

            a.setEvalTypeSymbol(((SymbolFunction)s).returntype);
        } else if (s instanceof SymbolClass){

            a.setEvalTypeSymbol(s);  // stack object creation.  type();
        } else {
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
                throw new IllegalStateException(" unsupported literal.");
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
        clp.symbolAssociated = clsym;   // before member.

        for (AST_Stmt clstmt : a.getMembers()) {

            clstmt.accept(this, clp);
        }

        a.sym = clsym;
        _p.define(clsym);
    }

    @Override
    public void visitStmtDefFunc(AST_Stmt_DefFunc a, Scope _p) {

        a.getReturnTypename().accept(this, _p);

        SymbolFunction sf = new SymbolFunction(a.getName(), a.getReturnTypename().sym, (SymbolClass)_p.symbolAssociated);
        sf.isStaticFunction = a.getModifiers().isStatic();
        a.symf = sf;

        Scope fnp = new Scope(_p);
        fnp.symbolAssociated = sf;  // before body.

        for (AST_Stmt_DefVar param : a.getParameters()) {
            param.accept(this, fnp);
        }

        a.getBody().accept(this, fnp);

//        SymbolVariable sym = new SymbolVariable(a.getName(), sf);
//        _p.define(sym);
        _p.define(sf);
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

            Validate.isTrue(sf.returntype == retexpr.getEvalTypeSymbol(), "Expected returning type: "+sf.returntype+", actual returning: "+retexpr.getEvalTypeSymbol());
        } else {
            Validate.isTrue(sf.returntype == SymbolBuiltinType._void, "function return-type is not void, required returning: "+sf.returntype);
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
