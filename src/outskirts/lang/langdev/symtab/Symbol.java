package outskirts.lang.langdev.symtab;

import java.util.Map;

/**
 * AST relation
 *
 * AST_Identitifer insteadof AST_VarName
 * AST_MemberAccess: expression, name
 */

/**
 * relation of Scope, SymbolTable, Symbol.
 *
 * the Scope is Spacial, Range, Container, kind of global.
 *
 * the Symbol is Flexible little Entity, is single little entity but may connected with a scope/SymbolTable just as an attribute.
 */

// is that All Symbol has sub-symbols??

// anyone symbol has not Table.?
public abstract class Symbol {

    public String name;

    public Symbol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
