package outskirts.lang.langdev.symtab;

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
public abstract class BaseSymbol implements Symbol {

    private final String name;

    public BaseSymbol(String name) {
        this.name = name;
    }

    @Override
    public final String getSimpleName() {
        return this.name;
    }




    @Override
    public String toString() {
        return getClass().getName();
    }
}
