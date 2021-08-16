package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Stmt;
import outskirts.lang.langdev.parser.LxParser;

import java.util.List;

public class AST_Class_Member extends AST {

    public final List<AST_Annotation> annotations;
    public final List<String> modifiers;
    public final AST_Stmt member;

    public AST_Class_Member(List<AST_Annotation> annotations, List<String> modifiers, AST_Stmt member) {
        this.annotations = annotations;
        this.modifiers = modifiers;
        this.member = member;
    }

    public boolean isStatic() {
        return modifiers.contains("static");
    }

}
