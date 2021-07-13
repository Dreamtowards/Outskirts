package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;

import java.util.Arrays;
import java.util.List;

public class AST_Stmt_DefClass extends AST_Stmt { // really? a stmt?

    public final String name;
    public final String[] superclasses;  // nullable.
    public final AST_Stmt[] members;

    public AST_Stmt_DefClass(String name, String[] superclasses, AST_Stmt[] members) {
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        // System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    public AST_Stmt_DefClass(List<AST> ls) {
        this(ls.get(0).tokentext(),
                ls.get(1) != null ? Arrays.stream(((ASTls) ls.get(1)).toArray()).map(e -> ((AST_Expr_PrimaryVariableName)e).name).toArray(String[]::new) : null,
                Arrays.asList(((ASTls)ls.get(2)).toArray()).toArray(new AST_Stmt[0]));
    }

    @Override
    public String toString() {
        return "(class "+name+")";
    }
}
