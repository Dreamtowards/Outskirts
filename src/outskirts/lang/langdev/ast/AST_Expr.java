package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.symtab.*;
import outskirts.util.Validate;

import java.util.Objects;

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
        if (exprsym == null)
            throw new IllegalStateException("Null Symbol");
        return exprsym;
    }
    public final void setSymbol(Symbol s) {
        Objects.requireNonNull(s);
        exprsym = s;
    }

    public final Symbol getExprSymbol() {
        return getSymbol();
    }
    public final void setExprSymbol(Symbol sym) {
        setSymbol(sym);
    }


    // just utility.
    public final TypeSymbol getTypeSymbol() {
        if (getSymbol() instanceof SymbolGenericsTypeParameter)
            throw new RuntimeException("GetTypeSymbol on GenericsTypeParameter");
        return (TypeSymbol)getExprSymbol();
    }
    public final TypeSymbol getVarTypeSymbol() { return getVarSymbol().getType(); }
    public final SymbolVariable getVarSymbol() {
        return (SymbolVariable)getSymbol();
    }
    public final ModifierSymbol getModifierSymbol() {
        return (ModifierSymbol)getSymbol();
    }
}
