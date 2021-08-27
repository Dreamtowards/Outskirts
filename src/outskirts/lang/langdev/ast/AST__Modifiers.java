package outskirts.lang.langdev.ast;

import java.util.List;

public class AST__Modifiers extends AST {

    public final List<AST__Annotation> annotations;
    public final List<String> modifiers;

    public AST__Modifiers(List<AST__Annotation> annotations, List<String> modifiers) {
        this.annotations = annotations;
        this.modifiers = modifiers;
    }

    public final boolean isStatic() {
        return modifiers.contains("static");
    }

}
