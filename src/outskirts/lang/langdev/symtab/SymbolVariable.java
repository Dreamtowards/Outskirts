package outskirts.lang.langdev.symtab;

import outskirts.util.Validate;

import java.util.HashMap;
import java.util.Map;

// Needs Scope? for Instance Member access.
public class SymbolVariable extends BaseSymbol implements ModifierSymbol {

    public final TypeSymbol type;
//    public final Scope enclosingScope;  needs.? for getQualifiedName()  ClassLocatedIn variableName: variableType

    private final short modifiercode;

    private final boolean hasAddr;

    public int staticVarOffset = -1;
    public static int nextStaticVarOffset = 0;

    public SymbolVariable(String name, TypeSymbol type, short modifiercode, boolean hasAddr) {
        super(name);
        this.type = type;
        this.modifiercode = modifiercode;
        this.hasAddr = hasAddr;

        if (name!=null && !hasAddr)
            throw new IllegalStateException("Named-Variable mush has address.");
    }

    // private static Map<TypeSymbol, SymbolVariable> cahcedNNInstances = new HashMap<>();

    // optim: not new everytime, but just TypeSymbol.getTemporaryInstanceVariableSymbol();
    // x ForcedRequirement: Make sure for every NonName-Instance, per TypeSymbol, per VariableSymbol instance.
    // x because in case of Binary-Oper-Operand-TypeCheck, two operand should have same symbol.
    // x nono, variable symbol can be different, the var-type be samed just ok.
//    static SymbolVariable buildRvalue(TypeSymbol type) {  // nn: NotNamed
//        return new SymbolVariable(null, type, (short)0, false);
//    }
//    static SymbolVariable buildLvalue(TypeSymbol type) {
//        return new SymbolVariable(null, type, (short)0, true);
//    }

    public TypeSymbol getType() {
        return type;
    }

    @Override
    public String getQualifiedName() {
        // EnclosingType.variable:Type ..?
        return type.getQualifiedName() + " " + getSimpleName();
    }

    /**
     * meant. a lvalue.
     * only 2 type of VarSym hasAddress currently:
     *  1. VarDecl declrated variable.
     *  2. returning symbol of dereference(ptr).
     */
    public boolean hasAddress() {
        return hasAddr;
    }

    @Override
    public short getModifierCode() {
        return modifiercode;
    }
}
