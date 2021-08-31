package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.TokenType;

import java.util.Collections;
import java.util.List;

public class AST__Modifiers extends AST {

    private final List<AST__Annotation> annotations;
    private final List<TokenType> modifiers;

    public AST__Modifiers(List<AST__Annotation> annotations, List<TokenType> modifiers) {
        this.annotations = annotations;
        this.modifiers = modifiers;
    }

    public final boolean isStatic() {
        return modifiers.contains(TokenType.STATIC);
    }

    public static AST__Modifiers DEFAULT = new AST__Modifiers(Collections.emptyList(), Collections.emptyList());

}
