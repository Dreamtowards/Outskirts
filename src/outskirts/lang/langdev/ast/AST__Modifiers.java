package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.lexer.TokenType;
import outskirts.lang.langdev.symtab.Modifiers;

import java.util.Collections;
import java.util.List;

public class AST__Modifiers extends AST {

    private final List<AST__Annotation> annotations;
    private final List<TokenType> modifiers;

    public AST__Modifiers(List<AST__Annotation> annotations, List<TokenType> modifiers) {
        this.annotations = annotations;
        this.modifiers = modifiers;
    }

    public List<AST__Annotation> getAnnotations() {
        return annotations;
    }

    public short getModifierCode() {
        return Modifiers.of(modifiers);  // todo: init cache
    }

    public final boolean empty() {
        return annotations.size()==0 && modifiers.size()==0;
    }

}
