package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;

import java.util.Arrays;
import java.util.List;

public class AST_Stmt_DefClass extends AST_Stmt { // really? a stmt?

    public final String name;
    public final AST_Typename[] superclasses;  // nullable.
    public final AST_Class_Member[] members;
    public final AST_Annotation[] annotations;

    public AST_Stmt_DefClass(AST_Annotation[] annotations, String name, ASTls genericParams, AST_Typename[] superclasses, AST_Class_Member[] members) {
        this.annotations = annotations;
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    public AST_Stmt_DefClass(List<AST> ls) {
        this(//ls.get(0)==null?null : ((ASTls)ls.get(0)).toArrayt(AST_Annotation[]::new),
                null,
                ls.get(0).tokentext(),
                (ASTls)ls.get(1),
                ls.get(2) == null ? null : ((ASTls)ls.get(2)).toArrayt(AST_Typename[]::new),
                ((ASTls)ls.get(3)).toArrayt(AST_Class_Member[]::new));
    }

    public AST_Expr hasInternalHandler() {
        if (annotations != null)
            for (AST_Annotation ann : annotations) {
                if (ann.type.nameptrExpanded().equals("internal_handler")) {
                    return ann.args[0];
                }
            }
        return null;
    }

    @Override
    public String toString() {
        return "(class "+ name +")";
    }
}
