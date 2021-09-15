package outskirts.lang.langdev.symtab;

import java.util.HashMap;
import java.util.Map;

// Needs Scope? for Instance Member access.
public class SymbolVariable extends BaseSymbol implements ModifierSymbol {

    public final TypeSymbol type;
//    public final Scope enclosingScope;  needs.? for getQualifiedName()  ClassLocatedIn variableName: variableType

    private final short modifiercode;

    public SymbolVariable(String name, TypeSymbol type, short modifiercode) {
        super(name);
        this.type = type;
        this.modifiercode = modifiercode;
    }

    // private static Map<TypeSymbol, SymbolVariable> cahcedNNInstances = new HashMap<>();

    // optim: not new everytime, but just TypeSymbol.getTemporaryInstanceVariableSymbol();
    // x ForcedRequirement: Make sure for every NonName-Instance, per TypeSymbol, per VariableSymbol instance.
    // x because in case of Binary-Oper-Operand-TypeCheck, two operand should have same symbol.
    // x nono, variable symbol can be different, the var-type be samed just ok.
    public static SymbolVariable nnInstance(TypeSymbol type) {  // nn: NotNamed
        return new SymbolVariable(null, type, (short)0);
//        return cahcedNNInstances.computeIfAbsent(type, typ -> new SymbolVariable(null, typ, (short)0));
    }

    public TypeSymbol getType() {
        return type;
    }

    @Override
    public String getQualifiedName() {
        // EnclosingType.variable:Type ..?
        throw new UnsupportedOperationException();
    }

    @Override
    public short getModifierCode() {
        return modifiercode;
    }
}
