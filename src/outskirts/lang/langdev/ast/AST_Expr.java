package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.symtab.Symbol;
import outskirts.lang.langdev.symtab.SymbolClass;
import outskirts.lang.langdev.symtab.TypeSymbol;

public abstract class AST_Expr extends AST {

    // Return-Type Symbol.
    // public Symbol sym;

    public TypeSymbol evaltype;

}
