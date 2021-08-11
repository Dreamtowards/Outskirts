package outskirts.lang.langdev.compiler.symtab;

import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Scope.
public class Symtab {

    private final Symtab parent;

    // lookup by stringName? how about diff type..
    private final Map<String, Symbol> symbols = new HashMap<>();

    public Symtab(Symtab parent) {
        this.parent = parent;
    }

    public Symtab getParent() {
        return parent;
    }

    public void define(Symbol symbol) {
        Validate.isTrue(!symbols.containsKey(symbol.name), "Already defined in the scope.");

        symbols.put(symbol.name, symbol);
    }

    public Symbol resolve(String name) {
        Symbol s = symbols.get(name);
        if (s != null)
            return s;
        if (parent != null)
            return parent.resolve(name);

        throw new IllegalStateException("Couldn't find symbol.");
    }


}
