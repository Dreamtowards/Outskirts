package outskirts.lang.langdev.symtab;

// Needs Scope? for Instance Member access.
public class SymbolVariable extends BaseSymbol {

    public final TypeSymbol type;
//    public final Scope enclosingScope;  needs.?

    public SymbolVariable(String name, TypeSymbol type) {
        super(name);
        this.type = type;
    }

    public TypeSymbol getType() {
        return type;
    }

    @Override
    public String getQualifiedName() {
        // EnclosingType.variable:Type ..?
        throw new UnsupportedOperationException();
    }
}
