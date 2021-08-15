package outskirts.lang.langdev.symtab;

import java.util.List;

public class SymbolUsing extends Symbol {

    // this is Weak.
    public SymbolUsing(String name, Symtab parent, List<Symbol> symbols) {
        super(name, parent);
    }
}
