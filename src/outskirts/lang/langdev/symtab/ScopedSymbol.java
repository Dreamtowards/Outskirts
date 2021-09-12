package outskirts.lang.langdev.symtab;

// standfor MemberAccess  {ScopedSymbol}.getSymbolTable().resolveMember(..);

// TypeSoped != ScopedSymbol, the primative type, its type but no inner-member.

// ScopedTypeSymbol -> ScopedSymbol: the ScopedSymbol not necessary to be a TypeSymbol.
// e.g. SymbolNamespace has scoped, but not a type.
public interface ScopedSymbol extends Symbol {

    Scope getSymbolTable();

    @Override
    default String getQualifiedName() {
        Scope tab = getSymbolTable();
        StringBuilder sb = new StringBuilder(tab.symbolAssociated.getSimpleName());

        while ((tab = tab.getParent()) != null) {
            if (tab.symbolAssociated != null) {
                sb.insert(0, tab.symbolAssociated.getSimpleName()+".");
            }
        }
        return sb.toString();
    }
}
