package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class ASTSymolize implements ASTVisitor<Scope> {

    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, Scope p) {
        AST_Expr fexpr = a.getExpression();
        fexpr.accept(this, p);

        Symbol s = fexpr.getExprSymbol();
//        System.out.println("Found Function Invokation, ExprType: "+s+"/"+s.getQualifiedName());

        if (s instanceof SymbolFunction) {  // Originally 'Exact' Function Calling.  expr_primary.iden(..) | iden(..)
            Validate.isTrue(fexpr instanceof AST_Expr_PrimaryIdentifier || fexpr instanceof AST_Expr_MemberAccess);
            SymbolFunction sf = (SymbolFunction)s;

            // when a function is static, dont allows it been called from instance context. like: instExpr.stfunc();
            // this may not a good way to check.
            if (sf.isStaticFunction && fexpr instanceof AST_Expr_MemberAccess) {
                // pkg.to.Class.Innr.stFunc();
                AST_Expr prev = ((AST_Expr_MemberAccess)fexpr).getExpression();
                Validate.isTrue(prev.getExprSymbol() instanceof SymbolNamespace ||
                                    prev.getExprSymbol() instanceof SymbolClass);  // unavailable check.
            }

            a.setExprSymbol(sf.returntype);
        } else if (s instanceof SymbolClass){  // Construction of Stack-Alloc-Object-Creation.  type(..).
            if (fexpr.isLiteralTypeExpr) {

            }

            a.setExprSymbol((SymbolClass)s); // ? s.getInstanceSymbol());
        } else {  // otherwise expr. Invoke "Invokation ()" operator to that object. or not.  // but still shoud in case of return-type SymbolClass.?
            throw new IllegalStateException();
        }
    }

    /**
     * ns.to.Class.InnerCls.instanceVal.funcSth()
     */
    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, Scope p) {
        AST_Expr lexpr = a.getExpression();
        lexpr.accept(this, p);
        ScopedSymbol lsym = (ScopedSymbol)lexpr.getExprSymbol();

        Symbol rsym = lsym.getSymbolTable().resolveMember(a.getIdentifier());  // Var / Func. / Namespace..

        // SymVar.?
        a.setExprSymbol(rsym instanceof SymbolVariable ? ((SymbolVariable)rsym).type : rsym);

    }

    @Override
    public void visitExprOperNew(AST_Expr_OperNew a, Scope p) {
        AST_Expr type = a.getTypeExpression();
        type.accept(this, p);

        Validate.isTrue(type.getTypeSymbol() instanceof SymbolClass, "OperNew Required SymbolClass.");
        a.setExprSymbol(type.getExprSymbol());  // SymVar.?
    }

    @Override
    public void visitExprOperConditional(AST_Expr_OperConditional a, Scope p) {
        a.getCondition().accept(this, p);
        a.getTrueExpression().accept(this, p);
        a.getFalseExpression().accept(this, p);

        Validate.isTrue(a.getTrueExpression().getExprSymbol() == a.getFalseExpression().getExprSymbol());
    }

    @Override
    public void visitExprOperUnary(AST_Expr_OperUnary a, Scope p) {
        a.getExpression().accept(this, p);

        if (a.getUnaryKind() == AST_Expr_OperUnary.UnaryKind.NOT) {
            a.setExprSymbol(SymbolBuiltinType._bool);
        } else {
            a.setExprSymbol(a.getExpression().getExprSymbol());
        }
    }

    @Override
    public void visitExprOperBinary(AST_Expr_OperBinary a, Scope p) {
        a.getLeftOperand().accept(this, p);
        a.getRightOperand().accept(this, p);

        if (a.getLeftOperand().getExprSymbol() != a.getRightOperand().getExprSymbol())
            throw new UnsupportedOperationException("Incpompactble OperBin "+a.getLeftOperand().getTypeSymbol().getQualifiedName()+", "+a.getRightOperand().getTypeSymbol().getQualifiedName());

        switch (a.getBinaryKind()) {
            case LT: case LTEQ: case GT: case GTEQ: case IS:
            case EQ: case NEQ:
                a.setExprSymbol(SymbolBuiltinType._bool);
                break;
            default:
                a.setExprSymbol(a.getLeftOperand().getExprSymbol());  // return commonBaseType(e1, e2);
                break;
        }
    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, Scope p) {
        a.getTypeExpression().accept(this, p);

        a.setExprSymbol(SymbolBuiltinType._int);
    }

    @Override
    public void visitExprTmpDereference(AST_Expr_TmpDereference a, Scope p) {
        a.getTypeExpression().accept(this, p);
        a.getExpression().accept(this, p);
        Validate.isTrue(a.getExpression().getTypeSymbol() == SymbolBuiltinType._int);

        a.setExprSymbol(a.getTypeExpression().getExprSymbol());
    }

    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, Scope p) {
        Symbol s = p.resolve(a.getName());

        // Problem: while attributing a variable  var1, which Symbol should it owns.?  SymVar or SymType.?


        // This is a real problem, How treat the Variable or Non-Name rvalues SymVar.?
        if (s instanceof SymbolVariable)
            a.setExprSymbol(((SymbolVariable)s).getType());
        else
            a.setExprSymbol(s);  // SymbolClass, SymbolBuiltinType, SymbolNamespace, SymbolFunction
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
        a.setExprSymbol(s);
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

        for (AST_Expr sup_type : a.getSuperTypeExpressions()) {
            sup_type.accept(this, _p);
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

        a.getReturnTypeExpression().accept(this, _p);

        Scope fnp = new Scope(_p);

        List<SymbolVariable> param_syms = new ArrayList<>(a.getParameters().size());
        if (!a.getModifiers().isStatic()) {
            SymbolVariable thisvar = new SymbolVariable("this", SymbolBuiltinType._int);
            fnp.define(thisvar);
            param_syms.add(thisvar);
        }
        for (AST_Stmt_DefVar prm : a.getParameters()) {
            prm.accept(this, fnp);

            param_syms.add(prm.sym);
        }

        SymbolFunction sf = new SymbolFunction(a.getName(), param_syms, a.getReturnTypeExpression().getTypeSymbol(), (SymbolClass)_p.symbolAssociated);
        sf.isStaticFunction = a.getModifiers().isStatic();
        fnp.symbolAssociated = sf;  // before body. return_stmt needs lookupEnclosingFunction to validates return-type.
        a.symf = sf;
        _p.define(sf);  // before body. recursive funcCall should can resolve self-calling.

        a.getBody().accept(this, fnp);

//        SymbolVariable sym = new SymbolVariable(a.getName(), sf); _p.define(sym);
    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, Scope p) {
        a.getTypeExpression().accept(this, p);

        SymbolVariable sym = new SymbolVariable(a.getName(), a.getTypeExpression().getTypeSymbol());
        a.sym = sym;
        p.define(sym);

        if (a.getInitializer() != null) {
            a.getInitializer().accept(this, p);

            /* may not SymVar.? */
            Validate.isTrue(a.getTypeExpression().getTypeSymbol() == a.getInitializer().getTypeSymbol(), "bad init type");  // might should let Semantic do.
        }
    }

    @Override
    public void visitStmtExpr(AST_Stmt_Expr a, Scope p) {
        a.getExpression().accept(this, p);
    }

    @Override
    public void visitStmtIf(AST_Stmt_If a, Scope p) {
        a.getCondition().accept(this, p);
        Validate.isTrue(a.getCondition().getExprSymbol() /* SymVar.? */ == SymbolBuiltinType._bool);  // early.

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
                lp = ns.getSymbolTable();
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

            Validate.isTrue(sf.getReturnType() == retexpr.getTypeSymbol() /* SymVar.? insteadof directly TypeLiteral */, "Expected returning type: "+sf.getReturnType()+", actual returning: "+retexpr.getExprSymbol());
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

    // Reduced. Not Conscious with Usual-Expression. should be identical way with usual Expression. "ns.to.sth.Clas"
//    @Override
//    public void visit_Typename(AST__Typename a, Scope p) {
//        a.sym = p.resolveQualifiedExpr(a.getType());  // GenericVariable
//    }

    @Override
    public void visit_CompilationUnit(AST__CompilationUnit a, Scope p) {

        for (AST_Stmt stmt : a.getDeclrations()) {
            stmt.accept(this, p);
        }

    }
}
