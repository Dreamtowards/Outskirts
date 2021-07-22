package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;

import java.util.Arrays;
import java.util.List;

public class AST_Stmt_DefClass extends AST_Stmt { // really? a stmt?

    public final AST_Typename typename;
    public final String[] superclasses;  // nullable.
    public final AST_Class_Member[] members;

    public AST_Stmt_DefClass(AST_Typename typename, String[] superclasses, AST_Class_Member[] members) {
        this.typename = typename;
        this.superclasses = superclasses;
        this.members = members;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    public AST_Stmt_DefClass(List<AST> ls) {
        this((AST_Typename)ls.get(0),
                ls.get(1) != null ? Arrays.stream(((ASTls) ls.get(1)).toArray()).map(e -> ((AST_Expr_PrimaryVariableName)e).name).toArray(String[]::new) : null,
                ((ASTls)ls.get(2)).toArrayt(AST_Class_Member[]::new));
    }

    @Override
    public String toString() {
        return "(class "+ typename +")";
    }
}
