package outskirts.lang.langdev.symtab;

import outskirts.lang.langdev.lexer.TokenType;

import java.util.List;

public final class Modifiers {

    public static final short MASK_STATIC = 1;

    public static short of(List<TokenType> ms) {
        short mod = 0;
        for (TokenType t : ms) {
            switch (t) {
            case STATIC: mod |= MASK_STATIC; break;
            default: throw new IllegalStateException();
            }
        }
        return mod;
    }

    public static boolean isStatic(short modifiercode) {
        return (modifiercode & MASK_STATIC) != 0;
    }

}
