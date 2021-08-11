package outskirts.lang.langdev.compiler.symtab;

import java.util.Map;

public abstract class Symbol {

    public String name;

    // var1.field, Class1.StaticMember, pkg.innr.
    public Map<String, Symbol> subsymbols;

}
