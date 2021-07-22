package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Stmt;
import outskirts.lang.langdev.ast.ASTls;

import java.util.List;

public class AST_Class_Member extends AST {

    public final AST_Annotation[] annotations;
    public final AST_Stmt member;

    public AST_Class_Member(AST_Annotation[] annotations, AST_Stmt member) {
        this.annotations = annotations;
        this.member = member;
    }

    public AST_Class_Member(List<AST> ls) {
        this(ls.get(0) == null ? null : ((ASTls)ls.get(0)).toArrayt(AST_Annotation[]::new), (AST_Stmt)ls.get(1));
    }
}
