package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolBuiltinTypePointer;
import outskirts.lang.langdev.symtab.SymbolFunction;
import outskirts.lang.langdev.symtab.SymbolVariable;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.StringUtils;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class AST_Expr_FuncCall extends AST_Expr {

    private final AST_Expr funcptr;
    private final List<AST_Expr> args;  // exprs.

//    public TypeSymbol fsym;  // SymbolFunction (Usually.) or SymbolClass (Stack ObjectCreation)

    public AST_Expr_FuncCall(AST_Expr expr, List<AST_Expr> args) {
        this.funcptr = expr;
        this.args = args;
    }

    // Function Selected
    public AST_Expr getExpression() {
        return funcptr;
    }

    public List<AST_Expr> getArguments() {
        return args;
    }

//    public List<AST_Expr> getArgumentsIncludeInstanceCaller() {
//        if (getExpression() instanceof AST_Expr_MemberAccess ma &&  // member-access
//            ma.getSymbol() instanceof SymbolFunction sf) {  // instance
//            Validate.isTrue(!sf.isStatic());
//            return CollectionUtils.asList(ma.getExpression(), args);
//        }
//        return args;
//    }

    // corespounding to SymbolFunction::getParameterSignature
    public List<TypeSymbol> getParameterSignature() {
        List<TypeSymbol> ls = new ArrayList<>();
        for (AST_Expr a : getArguments()) {
            ls.add(a.getVarTypeSymbol());
        }
        // adding 'this' of Variable-Call, var.func() put the 'var' as 'this' parameter of the func.
        if (getExpression() instanceof AST_Expr_MemberAccess ma &&
            ma.getExpression().getSymbol() instanceof SymbolVariable &&
            ma.getSymbol() instanceof SymbolFunction fn &&
            !fn.isStatic()) {

            TypeSymbol typ = ma.getExpression().getVarTypeSymbol();
            TypeSymbol ptr = ma.isArrow() ? typ : SymbolBuiltinTypePointer.of(typ);  // arraw: left side already is the pointer
            ls.add(0, ptr);
        }
        return ls;
    }
}
