package outskirts.lang.langdev.symtab;

import java.util.HashMap;
import java.util.Map;

public class SymbolBuiltinTypePointer extends SymbolBuiltinType {

    public static final int PTR_SIZE = 4;

    private final TypeSymbol ptrtype;

    public SymbolBuiltinTypePointer(TypeSymbol ptrtype) {
        super(null);  // this name ..?  the name seems meaningless. wouldn't be used.  // edit: into null.

        this.ptrtype = ptrtype;
    }

    private static final Map<TypeSymbol, SymbolBuiltinTypePointer> _CACHED_PTR_TYPES = new HashMap<>();
    public static SymbolBuiltinTypePointer of(TypeSymbol ptrtype) {
        SymbolBuiltinTypePointer s = _CACHED_PTR_TYPES.get(ptrtype);
        if (s != null)
            return s;
        s = new SymbolBuiltinTypePointer(ptrtype);
        _CACHED_PTR_TYPES.put(ptrtype, s);
        return s;
    }

    // name.. PointingType
    public TypeSymbol getPointerType() {
        return ptrtype;
    }

    @Override
    public int getTypesize() {
        return PTR_SIZE;
    }
}
