package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;

import java.util.Arrays;
import java.util.List;

public class AST_Stmt_DefClass extends AST_Stmt { // really? a stmt?

    public final String name;
    public final String[] superclasses;
    public final AST_Stmt_Block members;  // kind of "crude"..

    public AST_Stmt_DefClass(String name, String[] superclasses, AST_Stmt_Block members) {
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
//        System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ members);
    }

    public AST_Stmt_DefClass(List<AST> ls) {
        this(ls.get(0).tokentext(),
                Arrays.stream(((ASTls) ls.get(1)).toArray()).map(e -> ((AST_Expr_PrimaryVariableName)e).name).toArray(String[]::new),
                (AST_Stmt_Block)ls.get(2));
    }

    @Override
    public String toString() {
        return "(class "+name+")";
    }
}
