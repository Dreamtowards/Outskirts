package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.symtab.Symbol;
import outskirts.lang.langdev.symtab.SymbolVariable;
import outskirts.lang.langdev.symtab.TypeSymbol;

/**
 * for AST_Expr, the 'Oper' might can be reduced. (AST_Expr_OperNew -> AST_Expr_New)
 */
public abstract class AST_Expr extends AST {

    // Return-Type Symbol.
    // public Symbol sym;

    private Symbol exprsym;
    // Not Type anymore.  (not only SymbolClass, SymbolBuiltinType, but also included SymbolNamespace, SymbolFunction, SymbolVariable

    // Re-Consider. really needs 'evaltype_symbol'.? or just 'expr_symbol'.?
    // Problem: how handle return-symbol from func-call.  what's differnece of symbol between '2+4' vs. 'int'.?
    //   they are both SymbolBuiltinType, and may isn't a SymbolVariable because its not a var-name.


    public final Symbol getSymbol() {
        return exprsym;
    }
    public final void setSymbol(Symbol s) {
        exprsym = s;
    }

    public final Symbol getExprSymbol() {
        return exprsym;
    }
    public final void setExprSymbol(Symbol sym) {
        exprsym = sym;
    }


    // just utility.
    public final TypeSymbol getTypeSymbol() {
        return (TypeSymbol)getExprSymbol();
    }
    public final TypeSymbol getVarTypeSymbol() { return getVarSymbol().getType(); }
    public final SymbolVariable getVarSymbol() {
        return (SymbolVariable)getSymbol();
    }
}
