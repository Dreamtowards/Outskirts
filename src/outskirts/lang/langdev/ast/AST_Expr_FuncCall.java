package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolBuiltinTypePointer;
import outskirts.lang.langdev.symtab.SymbolFunction;
import outskirts.lang.langdev.symtab.SymbolVariable;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.CollectionUtils;
import outskirts.util.StringUtils;

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

    // corespounding to SymbolFunction::getParameterSignature
    public String getParameterSignature() {
        String opticalThis = null;

        // Dont forget, the instance-call, need plus the 'this' parameter.
        if (getExpression() instanceof AST_Expr_MemberAccess) {
            AST_Expr_MemberAccess macc = (AST_Expr_MemberAccess)getExpression();
            // a.func(1);
            if (macc.getSymbol() instanceof SymbolFunction &&
                macc.getExpression().getSymbol() instanceof SymbolVariable) {  // enough! why so many patches there?
                TypeSymbol instType = macc.getExpression().getVarTypeSymbol();
                TypeSymbol thisType = macc.isArrow() ? instType : SymbolBuiltinTypePointer.of(instType);  // arraw: left side already is the pointer
                opticalThis = thisType.getQualifiedName();
            }
        }
        return StringUtils.concat(opticalThis, ",",
                CollectionUtils.toString(getArguments(), ",", e -> e.getVarTypeSymbol().getQualifiedName())
        );
    }
}
