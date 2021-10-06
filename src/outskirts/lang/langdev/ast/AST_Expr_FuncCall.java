package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolFunction;
import outskirts.lang.langdev.symtab.TypeSymbol;

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

}
