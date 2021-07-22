package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;

import java.util.List;

public class AST_Annotation extends AST {

    public final AST_Typename type;

    public AST_Annotation(AST_Typename type) {
        this.type = type;
    }

    public AST_Annotation(List<AST> ls) {
        this((AST_Typename)ls.get(0));
    }
}
