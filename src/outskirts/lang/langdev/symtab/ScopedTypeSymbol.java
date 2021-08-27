package outskirts.lang.langdev.symtab;

// TypeSoped != ScopedSymbol, the primative type, its type but no inner-member.
public interface ScopedTypeSymbol extends TypeSymbol {

    Scope getTable();

    // ?ScopedTypeSymbol


    @Override
    default String getQualifiedName() {

    }
}
