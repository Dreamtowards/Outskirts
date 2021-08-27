package outskirts.lang.langdev.symtab;

// TypeSoped != ScopedSymbol, the primative type, its type but no inner-member.
public interface ScopedTypeSymbol extends TypeSymbol {

    Scope getTable();


    @Override
    default String getQualifiedName() {
        Scope tab = getTable();
        StringBuilder sb = new StringBuilder(tab.symbolAssociated.name);

        while ((tab = tab.getParent()) != null) {
            if (tab.symbolAssociated != null) {
                sb.insert(0, tab.symbolAssociated.name+".");
            }
        }
        return sb.toString();
    }
}
