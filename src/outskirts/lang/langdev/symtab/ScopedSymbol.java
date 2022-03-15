package outskirts.lang.langdev.symtab;

// standfor MemberAccess  {ScopedSymbol}.getSymbolTable().resolveMember(..);

// TypeSoped != ScopedSymbol, the primative type, its type but no inner-member.

// ScopedTypeSymbol -> ScopedSymbol: the ScopedSymbol not necessary to be a TypeSymbol.
// e.g. SymbolNamespace has scoped, but not a type.
public interface ScopedSymbol extends Symbol {

    Scope getSymbolTable();

    @Override
    default String getQualifiedName() {
        Scope st = getSymbolTable();
        StringBuilder sb = new StringBuilder(st.getAssociatedSymbol().getSimpleName());

        while ((st = st.getParent()) != null) {
            if (st.getAssociatedSymbol() != null) {
                sb.insert(0, st.getAssociatedSymbol().getSimpleName()+"::");
            }
        }
        return sb.toString();
    }
}
