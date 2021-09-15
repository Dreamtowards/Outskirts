package outskirts.lang.langdev.symtab;

import java.util.List;

public interface ModifierSymbol {

    short getModifierCode();

    default boolean isStatic() {
        return Modifiers.isStatic(getModifierCode());
    }

}
