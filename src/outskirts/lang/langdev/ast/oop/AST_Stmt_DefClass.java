package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;

import java.util.Arrays;
import java.util.List;

public class AST_Stmt_DefClass extends AST_Stmt { // really? a stmt?

    public final String name;
    public final List<AST_Typename> superclasses;  // nullable.
    public final List<AST_Class_Member> members;
    public final List<AST_Annotation> annotations;

    public AST_Stmt_DefClass(List<AST_Annotation> annotations, String name, List<AST_Expr_PrimaryVariableName> genericParams, List<AST_Typename> superclasses, List<AST_Class_Member> members) {
        this.annotations = annotations;
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    @Override
    public String toString() {
        return "(class "+ name +")";
    }
}
