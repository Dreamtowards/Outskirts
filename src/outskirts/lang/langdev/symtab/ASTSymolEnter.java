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
        SymbolFunction sf = (SymbolFunction)a.getExpression().getEvalTypeSymbol();
        a.setEvalTypeSymbol(sf.returntype);
    }

    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, Scope p) {
        a.getExpression().accept(this, p);

        ScopedTypeSymbol l = (ScopedTypeSymbol)a.getExpression().getEvalTypeSymbol();
        SymbolVariable v = (SymbolVariable)l.getTable().resolveMember(a.getIdentifier());
        a.setEvalTypeSymbol(v.type);
    }

    @Override
    public void visitExprOperBin(AST_Expr_OperBi a, Scope p) {
        a.getLeftOperand().accept(this, p);
        a.getRightOperand().accept(this, p);
        if (a.getLeftOperand().getEvalTypeSymbol() == a.getRightOperand().getEvalTypeSymbol())
            a.setEvalTypeSymbol(a.getLeftOperand().getEvalTypeSymbol());  // return commonBaseType(e1, e2);
        else
            throw new UnsupportedOperationException("Incpompactble OperBin "+a.getLeftOperand().getEvalTypeSymbol().getQualifiedName()+", "+a.getRightOperand().getEvalTypeSymbol().getQualifiedName());

    }

    @Override
    public void visitExprOperNew(AST_Expr_OperNew a, Scope p) {
        a.getTypename().accept(this, p);
        a.setEvalTypeSymbol(a.getTypename().sym);
    }

    @Override
    public void visitExprOperTriCon(AST_Expr_OperConditional a, Scope p) {
        a.getCondition().accept(this, p);
        a.getTrueExpression().accept(this, p);
        a.getFalseExpression().accept(this, p);

        Validate.isTrue(a.getTrueExpression().getEvalTypeSymbol() == a.getFalseExpression().getEvalTypeSymbol());
    }

    @Override
    public void visitExprOperUnary(AST_Expr_OperUnary a, Scope p) {
        a.getExpression().accept(this, p);

        a.setEvalTypeSymbol(a.getExpression().getEvalTypeSymbol());
    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, Scope p) {
        a.getTypename().accept(this, p);
        a.setEvalTypeSymbol(SymbolBuiltinType._int);
    }

    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, Scope p) {
        Symbol s = p.resolve(a.getName());

        if (s instanceof SymbolVariable)
            a.evaltype = ((SymbolVariable)s).type;
        else
            a.evaltype = (TypeSymbol)s;  // SymbolClass or SymbolNamespace.
    }

    @Override
    public void visitExprPrimaryLiteralInt(AST_Expr_PrimaryLiteralInt a, Scope scope) {
        a.evaltype = SymbolBuiltinType._int;
    }

    @Override
    public void visitExprPrimaryLiteralChar(AST_Expr_PrimaryLiteralChar a, Scope p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitStmtBlock(AST_Stmt_Block a, Scope _p) {
        Scope blp = new Scope(_p);

        for (AST_Stmt stmt : a.stmts) {
            stmt.accept(this, blp);
        }
    }

    @Override
    public void visitStmtDefClass(AST_Stmt_DefClass a, Scope _p) {
        Scope clp = new Scope(_p);

        for (AST__Typename sup : a.superclasses) {
            sup.accept(this, _p);
        }

        for (AST_Stmt clstmt : a.members) {

            clstmt.accept(this, clp);
        }

        SymbolClass clsym = new SymbolClass(a.name, clp);
        a.sym = clsym;

        _p.define(clsym);
    }

    @Override
    public void visitStmtDefFunc(AST_Stmt_DefFunc a, Scope _p) {
        Scope fnp = new Scope(_p);

        a.returntype.accept(this, _p);

        for (AST_Stmt_DefVar param : a.params) {

            param.accept(this, fnp);

        }
        a.body.accept(this, fnp);

        SymbolVariable sym = new SymbolVariable(a.name, new SymbolFunction(a.name, a.returntype.sym));
        _p.define(sym);
    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, Scope p) {
        a.type.accept(this, p);

        SymbolVariable sym = new SymbolVariable(a.name, a.type.sym);
        p.define(sym);

        if (a.initexpr != null) {
            a.initexpr.accept(this, p);

            Validate.isTrue(a.type.sym == a.initexpr.evaltype, "bad init type");  // might should let Semantic do.
        }
    }

    @Override
    public void visitStmtExpr(AST_Stmt_Expr a, Scope p) {
        a.expr.accept(this, p);
    }

    @Override
    public void visitStmtIf(AST_Stmt_If a, Scope p) {
        a.condition.accept(this, p);

        a.thenb.accept(this, p);

        if (a.elseb != null) {
            a.elseb.accept(this, p);
        }
    }

    @Override
    public void visitStmtNamespace(AST_Stmt_Namespace a, Scope _p) {
        Scope lp = _p;

        for (String nm : StringUtils.explode(LxParser._ExpandQualifiedName(a.name), ".")) {
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

        for (AST_Stmt stmt : a.stmts) {
            stmt.accept(this, lp);
        }
    }

    @Override
    public void visitStmtReturn(AST_Stmt_Return a, Scope p) {
        if (a.expr != null) {
            a.expr.accept(this, p);
        }
    }

    @Override
    public void visitStmtUsing(AST_Stmt_Using a, Scope p) {
        Symbol used = p.resolveQualifiedExpr(a.used);

        Validate.isTrue(a.isStatic ? (used instanceof SymbolVariable) : (used instanceof SymbolClass));

        p.defineAsCustomName(a.asname, used);
    }

    @Override
    public void visitStmtWhile(AST_Stmt_While a, Scope p) {
        a.condition.accept(this, p);

        a.then.accept(this, p);
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
