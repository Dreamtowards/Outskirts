package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.Main;
import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.compiler.ConstantPool;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.CodeGen;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.util.CollectionUtils;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static outskirts.lang.langdev.symtab.SymbolBuiltinType.*;

public class ASTSymolize implements ASTVisitor<Scope> {

    public static ASTSymolize INSTANCE = new ASTSymolize();

    private ASTSymolize() {}


    @Override
    public void visitExprFuncCall(AST_Expr_FuncCall a, Scope p) {
        AST_Expr fexpr = a.getExpression();
        fexpr.acceptvisit(this, p);

        Symbol s = fexpr.getExprSymbol();
//        System.out.println("Found Function Invokation, ExprType: "+s+"/"+s.getQualifiedName());

        for (AST_Expr arg : a.getArguments()) {
            arg.acceptvisit(this, p);
        }

        if (s instanceof SymbolFunction) {  // Originally 'Exact' Function Calling.  expr_primary.iden(..) | iden(..)
            // Validate.isTrue(fexpr instanceof AST_Expr_PrimaryIdentifier || fexpr instanceof AST_Expr_MemberAccess);  // meaningless.
            SymbolFunction sf = (SymbolFunction)s;
            sf = sf.findOverwriteFunc(a.getParameterSignature());  // findout the Correct Overwrite Function.
            // Validate.isTrue(sf.getParametersSignature().equals(a.getParameterSignature()));
            fexpr.setExprSymbol(sf); // Reset Update the Overwritten Symbol

            // check args types.
            List<SymbolVariable> params = sf.getDeclaredParameters();
            Validate.isTrue(a.getArguments().size() == params.size(), "argument size dismatched.");
            for (int i = 0;i < a.getArguments().size();i++) {
                TypeSymbol arg_typ = a.getArguments().get(i).getVarTypeSymbol(), prm_typ = params.get(i).getType();
                Validate.isTrue(arg_typ == prm_typ, "argument "+(i+1)+" type dismatched. required: "+prm_typ+", actual: "+arg_typ);
            }

            // TODO: this check is responsbility of MemberAccess. (static member access check)
            // when a function is static, dont allows it been called from instance context. like: instExpr.stfunc();
            // this may not a good way to check.
            if (sf.isStatic() && fexpr instanceof AST_Expr_MemberAccess) {
                // pkg.to.Class.Innr.stFunc();
                AST_Expr left = ((AST_Expr_MemberAccess)fexpr).getExpression();
                Validate.isTrue(left.getExprSymbol() instanceof SymbolNamespace ||
                        left.getExprSymbol() instanceof SymbolClass);  // unavailable check.
            }
            if (!sf.isStatic()) {
                Validate.isTrue(fexpr instanceof AST_Expr_MemberAccess);
            }

            a.setExprSymbol(sf.getReturnType().rvalue());
        } else if (s instanceof SymbolClass) {  // Construction of Stack-Alloc-Object-Creation.  type(..).
            SymbolClass c = (SymbolClass)s;


            a.setExprSymbol(c.rvalue());
        } else if (s instanceof SymbolVariable) {
            // otherwise expr. Invoke "Invokation ()" operator to that object. or not.  // but still shoud in case of return-type SymbolClass.?


            TypeSymbol rettype = null;// ((SymbolVariable)s).getType().getOperatorFunction().getReturnType();
            a.setExprSymbol(rettype.rvalue());
            throw new UnsupportedOperationException("Unsupported invocation operator.");
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExprGenericsArgumented(AST_Expr_GenericsArgumented a, Scope p) {
        a.getTypeExpression().acceptvisit(this, p);

        List<TypeSymbol> sGenericsArguments = new ArrayList<>();
        for (AST_Expr arg : a.getGenericsArguments()) {
            arg.acceptvisit(this, p);
            sGenericsArguments.add(arg.getTypeSymbol());
        }

        SymbolClass sGenericsPrototype = (SymbolClass)a.getTypeExpression().getTypeSymbol();

        SymbolClass sGenericsInstanced = sGenericsPrototype.lookupInstancedGenerics(sGenericsArguments, this);
        a.setSymbol(sGenericsInstanced);
    }

    /**
     * ns.to.Class.InnerCls.instanceVal.funcSth()
     */
    @Override
    public void visitExprMemberAccess(AST_Expr_MemberAccess a, Scope p) {
        AST_Expr expr = a.getExpression();
        expr.acceptvisit(this, p);

        Symbol ls = expr.getSymbol();
        ScopedSymbol lp;
        if (a.isArrow()) {
            SymbolBuiltinTypePointer ptr = (SymbolBuiltinTypePointer)((SymbolVariable)ls).getType();
            lp = (ScopedSymbol)ptr.getPointerType();
        } else if (ls instanceof SymbolVariable) {  // access from Instance.
            lp = (ScopedSymbol)((SymbolVariable)ls).getType();
        } else {  // SymbolNamespace, SymbolClass
            lp = (ScopedSymbol)ls;
        }

        Symbol ms = lp.getSymbolTable().resolveMember(a.getIdentifier());  // inheritance search.
        Objects.requireNonNull(ms);
        if (ms instanceof SymbolVariable && ls instanceof SymbolVariable) {
            TypeSymbol typ = ((SymbolVariable)ms).getType();
            if (a.isArrow()) {
                a.setExprSymbol(typ.lvalue());
            } else {
                // val.memb;  when left expr is rval, access expr are rval.
                a.setExprSymbol(typ.valsym(((SymbolVariable)ls).hasAddress()));
            }
        } else {
            // Namespace.. or expr.func(), expr->func().
            a.setExprSymbol(ms);
        }

        // validate static access.
        if (ls instanceof SymbolVariable) {  // access from Instance. required non-static access.
            Validate.isTrue(!((ModifierSymbol)a.getExprSymbol()).isStatic());
        } else if (ls instanceof SymbolClass) {  // access from LiteralType.  required static-access.
            Validate.isTrue(((ModifierSymbol)a.getExprSymbol()).isStatic());
        }
    }

    @Override
    public void visitExprOperNew(AST_Expr_OperNew a, Scope p) {
        AST_Expr type = a.getTypeExpression();
        type.acceptvisit(this, p);

        Validate.isTrue(type.getTypeSymbol() instanceof SymbolClass, "OperNew Required SymbolClass.");
        a.setExprSymbol(type.getTypeSymbol().rvalue());  // SymVar.? TODO: nono.. should be PTR
    }

    @Override
    public void visitExprOperNewMalloc(AST_Expr_OperNewMalloc a, Scope p) {
        AST_Expr szexpr = a.getSizeExpression();
        szexpr.acceptvisit(this, p);

        Validate.isTrue(szexpr.getVarTypeSymbol() == _i32);
        a.setExprSymbol(SymbolBuiltinTypePointer.of(_i8).rvalue());
    }

    @Override
    public void visitExprOperConditional(AST_Expr_OperConditional a, Scope p) {
        a.getCondition().acceptvisit(this, p);
        a.getTrueExpression().acceptvisit(this, p);
        a.getFalseExpression().acceptvisit(this, p);

        Validate.isTrue(a.getTrueExpression().getExprSymbol() == a.getFalseExpression().getExprSymbol());
        a.setExprSymbol(a.getTrueExpression().getTypeSymbol().rvalue());
    }

    @Override
    public void visitExprOperUnary(AST_Expr_OperUnary a, Scope p) {
        AST_Expr expr = a.getExpression();
        expr.acceptvisit(this, p);

        Symbol s;
        switch (a.getUnaryKind()) {
            case REF:
                Validate.isTrue(expr.getVarSymbol().hasAddress());  // make sure lvalue in.

                s = SymbolBuiltinTypePointer.of(expr.getVarTypeSymbol()).rvalue();
                break;
            case DEREF:
                SymbolBuiltinTypePointer ptype = (SymbolBuiltinTypePointer)expr.getVarTypeSymbol();

                s = ptype.getPointerType().lvalue();
                break;
            case NOT:
                s = _bool.rvalue();
                break;
            case PTR_TYP:
                s = SymbolBuiltinTypePointer.of(expr.getTypeSymbol());
                break;
            case POST_INC:
            case POST_DEC:
                Validate.isTrue(expr.getVarSymbol().hasAddress());  // makesure lvalue

                s = expr.getVarTypeSymbol().rvalue();
                break;
            case PRE_INC:
            case PRE_DEC:
                Validate.isTrue(expr.getVarSymbol().hasAddress());  // makesure lvalue

                s = expr.getVarTypeSymbol().lvalue();
                break;
            case NEG:
                s = expr.getVarTypeSymbol().rvalue();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        a.setExprSymbol(s);
    }

    @Override
    public void visitExprOperBinary(AST_Expr_OperBinary a, Scope p) {
        a.getLeftOperand().acceptvisit(this, p);
        a.getRightOperand().acceptvisit(this, p);

        if (a.getLeftOperand().getVarTypeSymbol() != a.getRightOperand().getVarTypeSymbol())
            throw new UnsupportedOperationException("Incpompactble OperBin: "+a.getLeftOperand().getExprSymbol().getQualifiedName()+", "+a.getRightOperand().getExprSymbol().getQualifiedName());

        switch (a.getBinaryKind()) {
            case LT: case LTEQ: case GT: case GTEQ: case IS:
            case EQ: case NEQ:
                a.setSymbol(_bool.rvalue());
                break;
            case ASSIGN:
                Validate.isTrue(a.getLeftOperand().getVarSymbol().hasAddress());
                a.setExprSymbol(a.getLeftOperand().getVarSymbol());
                break;
            default:
                a.setExprSymbol(a.getLeftOperand().getVarTypeSymbol().rvalue());  // return commonBaseType(e1, e2);
                break;
        }
    }

    @Override
    public void visitExprSizeOf(AST_Expr_OperSizeOf a, Scope p) {
        a.getTypeExpression().acceptvisit(this, p);

        a.setExprSymbol(_i32.rvalue());
    }

//    @Override
//    public void visitExprTmpDereference(AST_Expr_TmpDereference a, Scope p) {
//        a.getTypeExpression().accept(this, p);
//        a.getExpression().accept(this, p);
//
//        Validate.isTrue(a.getExpression().getVarTypeSymbol() == SymbolBuiltinType._int);
//
//        a.setExprSymbol(a.getTypeExpression().getTypeSymbol().lvalue());
//        System.out.println("Deref-SYM: "+a.getVarSymbol().hasAddress()+" "+a.getVarTypeSymbol());
//    }

//    @Override
//    public void visitExprTmpReference(AST_Expr_TmpReference a, Scope p) {
//        AST_Expr expr = a.getExpression();
//        expr.accept(this, p);
//
//        Validate.isTrue(expr.getVarSymbol().hasAddress(), "Reference operation can only applies on lvalues.");
//
//        a.setExprSymbol(SymbolBuiltinType._int.rvalue());
//    }



    @Override
    public void visitExprPrimaryIdentifier(AST_Expr_PrimaryIdentifier a, Scope p) {
        Symbol s = p.resolve(a.getName());

        // Problem: while attributing a variable  var1, which Symbol should it owns.?  SymVar or SymType.?

        a.setExprSymbol(s);

//        // This is a real problem, How treat the Variable or Non-Name rvalues SymVar.?
//        if (s instanceof SymbolVariable)
//            a.setExprSymbol(((SymbolVariable)s).getType());
//        else
//            a.setExprSymbol(s);  // SymbolClass, SymbolBuiltinType, SymbolNamespace, SymbolFunction
    }

    @Override
    public void visitExprPrimaryLiteral(AST_Expr_PrimaryLiteral a, Scope p) {
        TypeSymbol s;
        switch (a.getLiteralKind()) {
            case UINT16:  // char -> int is temporary.
            case INT32:  s = _i32;  break;
            case BOOL:   s = _bool; break;
            case STRING: s = SymbolBuiltinTypePointer.of(_i8); break;
            default:
                throw new IllegalStateException("unsupported literal.");
        }
        a.setExprSymbol(s.rvalue());
    }

    @Override
    public void visitExprTypeCast(AST_Expr_TypeCast a, Scope p) {
        a.getExpression().acceptvisit(this, p);
        a.getType().acceptvisit(this, p);

        // assert isCapabile()
        TypeSymbol dst = a.getType().getTypeSymbol();
//        SymbolVariable sv = a.getExpression().getVarSymbol();

        // cast_i8_i32.  identical size not total required.
//        Validate.isTrue(dst.getTypesize() == sv.getType().getTypesize(), "Cast typesize dismatch.");

//        a.setExprSymbol(dst.valsym(sv.hasAddress()));
        a.setExprSymbol(dst.rvalue());  // after cast, its been rvalues. e.g. (byte)i
    }

    @Override
    public void visitStmtBlock(AST_Stmt_Block a, Scope _p) {
        Scope blp = new Scope(_p);

        for (AST_Stmt stmt : a.getStatements()) {
            stmt.acceptvisit(this, blp);

        }
    }

    @Override
    public void visitStmtBreak(AST_Stmt_Break a, Scope p) {
        // enclosing_loop check will be done in codengen.
        // Validate.isTrue(p.findEnclosingLoop() != null);  // label / while / for / foreach / do-while
    }

    @Override
    public void visitStmtContinue(AST_Stmt_Continue a, Scope p) {
        // Validate.isTrue(p.findEnclosingLoop() != null);  // almost same as stmt_break.
    }

//    public static String strGenericsArguments(List<TypeSymbol> sGenericsArguments) {
//        return "<"+ CollectionUtils.toString(sGenericsArguments, ",", Symbol::getQualifiedName)+">";
//    }

    @Override
    public void visitStmtDefClass(AST_Stmt_DefClass a, Scope _p) {
        //
        // Validate.isTrue(a.getGenericsParameters().size() == a.tmpGenericsArguments.size());

        boolean instancingGenerics = !a.getGenericsParameters().isEmpty() && a.tmpGenericsArguments != null;

        Scope stClass = new Scope(_p);
        SymbolClass sc = new SymbolClass(a.getSimpleName(), stClass);
        stClass.setAssociatedSymbol(sc);   // before member. members need link the owner_class.
        a.sym = sc;
        if (!instancingGenerics)  // don't define instanced generics.
            _p.define(sc);  // before member. member should be able lookup enclosing class symbol.

        // if there is a generics-prototype, just save AST, not analysis. since it's not been filled yet.
        boolean isGenericsPrototype = !a.getGenericsParameters().isEmpty() && a.tmpGenericsArguments == null;
        if (isGenericsPrototype) {
            sc.theGenericsPrototypeAST = a;
            return;
        }

        if (instancingGenerics) {
            for (int i = 0;i < a.getGenericsParameters().size();i++) {
                stClass.defineAsCustomName(a.getGenericsParameters().get(i).getName(), a.tmpGenericsArguments.get(i));
            }
            Validate.isTrue(a.tmpGenericsArguments != null);
            a.tmpGenericsArguments = null;  // consumed.
            a.tmpGenericsInstance = sc;
        }

        for (AST_Expr supTyp : a.getSuperTypeExpressions()) {
            supTyp.acceptvisit(this, stClass);
            sc.superClasses.add((SymbolClass)supTyp.getTypeSymbol());
        }

        for (AST_Stmt clStmt : a.getMembers()) {

            clStmt.acceptvisit(this, stClass);
        }

    }

    @Override
    public void visitStmtDefFunc(AST_Stmt_DefFunc a, Scope _p) {
        SymbolClass ownerclass = (SymbolClass)_p.symbolAssociated;

        a.getReturnTypeExpression().acceptvisit(this, _p);

        Scope fnp = new Scope(_p);

        List<SymbolVariable> param_syms = new ArrayList<>();
        // manually add "this" ptr for non-static function.
        if (!Modifiers.isStatic(a.getModifiers().getModifierCode())) {
            SymbolVariable sv = new SymbolVariable("this", SymbolBuiltinTypePointer.of(ownerclass), (short)0, true);
            fnp.define(sv);
            param_syms.add(sv);
        }
        for (AST_Stmt_DefVar prm : a.getParameters()) {
            prm.acceptvisit(this, fnp);  // define.
            param_syms.add(prm.sym);
        }

        SymbolFunction sf = new SymbolFunction(a.getName(), param_syms, a.getReturnTypeExpression().getTypeSymbol(),
                ownerclass, a.getModifiers().getModifierCode(), fnp);
        fnp.symbolAssociated = sf;  // before body. return_stmt needs lookupEnclosingFunction to validates return-type.
        a.symf = sf;
        // define before body. recursive funcCall should be able to resolve self-calling.
        SymbolFunction alreadyExistedFunc;
        if ((alreadyExistedFunc=(SymbolFunction)_p.findLocalSymbol(a.getName())) == null) {
            _p.define(sf);
        } else {
            alreadyExistedFunc.defineOverwriteFunc(sf);
        }

        sf.genericsTmpASTForCompile = a;

        a.getBody().acceptvisit(this, fnp);

        CodeBuf codeBuf = new CodeBuf(new ConstantPool(), param_syms);
        a.getBody().acceptvisit(new CodeGen(), codeBuf);
        sf.codebuf = codeBuf;
        Main.compiledfuncs.put(sf.getQualifiedName(), sf);

//        SymbolVariable sym = new SymbolVariable(a.getName(), sf); _p.define(sym);
    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, Scope p) {
        a.getTypeExpression().acceptvisit(this, p);

//        // check function-inside variable name unique.
//        SymbolFunction sf = p.lookupEnclosingFuncction();
//        if (sf != null) {  // declrating var in a function.
//            sf.fnscope.resolveInside(a.getName());
//        }

        SymbolVariable sv = new SymbolVariable(a.getName(), a.getTypeExpression().getTypeSymbol(),
                                                 a.getModifiers().getModifierCode(), true);
        a.sym = sv;
        p.define(sv);

        if (sv.isStatic()) {
            Validate.isTrue(p.getAssociatedSymbol() instanceof SymbolClass);

            sv.staticVarOffset = SymbolVariable.nextStaticVarOffset;
            SymbolVariable.nextStaticVarOffset += sv.getType().getTypesize();
        }

        if (a.getInitializer() != null) {
            a.getInitializer().acceptvisit(this, p);

            Validate.isTrue(a.getTypeExpression().getTypeSymbol() == a.getInitializer().getVarTypeSymbol(), "Initializer type dismatch.");
        }
    }

    @Override
    public void visitStmtExpr(AST_Stmt_Expr a, Scope p) {
        a.getExpression().acceptvisit(this, p);
    }

    @Override
    public void visitStmtIf(AST_Stmt_If a, Scope p) {
        a.getCondition().acceptvisit(this, p);
        Validate.isTrue(a.getCondition().getVarTypeSymbol() == _bool);  // early.

        a.getThenStatement().acceptvisit(this, p);

        if (a.getElseStatement() != null) {
            a.getElseStatement().acceptvisit(this, p);
        }
    }

    @Override
    public void visitStmtNamespace(AST_Stmt_Namespace a, Scope _p) {
        Scope lp = _p;

        for (String nm : LxParser._ExpandQualifiedName(a.getNameExpression(), new ArrayList<>())) {
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
            stmt.acceptvisit(this, lp);
        }
    }

    @Override
    public void visitStmtReturn(AST_Stmt_Return a, Scope p) {
        SymbolFunction sf = p.lookupEnclosingFuncction();
        Objects.requireNonNull(sf);

        AST_Expr retexpr = a.getReturnExpression();
        if (retexpr != null) {
            retexpr.acceptvisit(this, p);

            Validate.isTrue(sf.getReturnType() == retexpr.getVarTypeSymbol(), "Expected returning type: "+sf.getReturnType()+", actual returning: "+retexpr.getExprSymbol());
        } else {
            Validate.isTrue(sf.getReturnType() == SymbolBuiltinType._void, "function return-type is not void, required returning: "+sf.getReturnType());
        }
    }

    @Override
    public void visitStmtUsing(AST_Stmt_Using a, Scope p) {
        a.getQualifiedExpression().acceptvisit(this, p);
        Symbol used = a.getQualifiedExpression().getSymbol();

        // static-using-check temporarely disabled
//        if (a.isStatic()) {
//            Validate.isTrue(used instanceof SymbolVariable || used instanceof SymbolFunction, "static using required variable/function symbol.");
//        } else {
//            Validate.isTrue(used instanceof SymbolClass, "non-static using required class symbol.");
//        }

//        System.out.println("Define Using "+a.getDeclaredName() +" for "+used.getQualifiedName() + " in "+p.getMemberSymbols());
        p.defineAsCustomName(a.getDeclaredName(), used);
    }

    @Override
    public void visitStmtWhile(AST_Stmt_While a, Scope p) {
        a.getCondition().acceptvisit(this, p);
        Validate.isTrue(a.getCondition().getVarTypeSymbol() == _bool);

        a.getStatement().acceptvisit(this, p);
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
            stmt.acceptvisit(this, p);
        }

    }
}
