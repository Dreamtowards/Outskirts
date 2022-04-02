package outskirts.lang.langdev.symtab;


import outskirts.util.Pair;

import java.util.HashMap;
import java.util.Map;

public interface TypeSymbol extends Symbol {

    int getTypesize();

    // int getBaseType();

    default SymbolVariable rvalue() { return valsym(false); }
    default SymbolVariable lvalue() { return valsym(true);  }

    static Map<Pair<TypeSymbol, Boolean>, SymbolVariable> _CACHED = new HashMap<>();

    default SymbolVariable valsym(boolean hasAddr) {
        var k = new Pair<>(this, hasAddr);
        SymbolVariable v = _CACHED.get(k);
        if (v != null)
            return v;

        v = new SymbolVariable(null, this, (short)0, hasAddr); // todo: Modifiers Loses???
        _CACHED.put(k, v);
        return v;
    }

}
