package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.Symbol;
import outskirts.lang.langdev.symtab.SymbolClass;
import outskirts.lang.langdev.symtab.TypeSymbol;

/**
 * for AST_Expr, the 'Oper' might can be reduced. (AST_Expr_OperNew -> AST_Expr_New)
 */
public abstract class AST_Expr extends AST {

    // Return-Type Symbol.
    // public Symbol sym;

    public TypeSymbol evaltype;

    /**
     * Evaluated TypeSymbol
     */
    public TypeSymbol getEvalTypeSymbol() {
        return evaltype;
    }
    public void setEvalTypeSymbol(TypeSymbol evalTypeSymbol) {
        evaltype = evalTypeSymbol;
    }

}
