package outskirts.lang.langdev.symtab;

import java.util.Objects;

// Needs Scope? for Instance Member access.
public class SymbolVariable extends Symbol {

    public final TypeSymbol type;

    public SymbolVariable(String name, TypeSymbol type) {
        super(name);
        this.type = type;
    }
}
