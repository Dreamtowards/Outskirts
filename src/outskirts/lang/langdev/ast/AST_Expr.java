package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.symtab.BaseSymbol;
import outskirts.lang.langdev.symtab.Symbol;
import outskirts.lang.langdev.symtab.TypeSymbol;

/**
 * for AST_Expr, the 'Oper' might can be reduced. (AST_Expr_OperNew -> AST_Expr_New)
 */
public abstract class AST_Expr extends AST {

    // Return-Type Symbol.
    // public Symbol sym;

    private Symbol evaltype;
    // Not Type anymore.  (not only SymbolClass, SymbolBuiltinType, but also included SymbolNamespace, SymbolFunction, SymbolVariable

    // Re-Consider. really needs 'evaltype_symbol'.? or just 'expr_symbol'.?
    // Problem: how handle return-symbol from func-call.  what's differnece of symbol between '2+4' vs. 'int'.?
    //   they are both SymbolBuiltinType, and may isn't a SymbolVariable because its not a var-name.

    /**
     * Evaluated TypeSymbol
     */
    public final Symbol getExprSymbol() {
        return evaltype;
    }
    public final void setExprSymbol(Symbol sym) {
        evaltype = sym;
    }

    // just utility.
    public final TypeSymbol getTypeSymbol() {
        return (TypeSymbol)getExprSymbol();
    }
}
