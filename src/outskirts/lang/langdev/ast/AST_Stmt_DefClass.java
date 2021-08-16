package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.symtab.SymbolClass;

import java.util.List;
// Things about Perfection. 21w32.
public class AST_Stmt_DefClass extends AST_Stmt { // really? a stmt?

    public final String name;
    public SymbolClass thisclass;

    public final List<AST__Typename> superclasses;  // nullable.
    public final List<AST_Class_Member> members;
    public final List<AST__Annotation> annotations;


    public AST_Stmt_DefClass(List<AST__Annotation> annotations, String name, List<AST_Expr_PrimaryVariableName> genericParams, List<AST__Typename> superclasses, List<AST_Class_Member> members) {
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

    public static class AST_Class_Member extends AST {

        public final List<AST__Annotation> annotations;
        public final List<String> modifiers;
        public final AST_Stmt member;

        public AST_Class_Member(List<AST__Annotation> annotations, List<String> modifiers, AST_Stmt member) {
            this.annotations = annotations;
            this.modifiers = modifiers;
            this.member = member;
        }

        public boolean isStatic() {
            return modifiers.contains("static");
        }

    }

}
