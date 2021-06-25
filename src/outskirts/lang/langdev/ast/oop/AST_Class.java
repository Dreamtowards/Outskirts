package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.ASTls;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.Arrays;
import java.util.List;

public class AST_Class extends AST {

    private final String name;
    private final AST[] superclasses;
    private final AST[] members;

    public AST_Class(String name, AST[] superclasses, AST[] members) {
        this.name = name;
        this.superclasses = superclasses;
        this.members = members;
        System.out.println("ClassDef: "+name+", sups: "+ Arrays.toString(superclasses)+", membs: "+ Arrays.toString(members));
    }

    public AST_Class(List<AST> ls) {
        this(ls.get(0).tokentext(), ((ASTls)ls.get(1)).toArray(), ((ASTls)ls.get(2)).toArray());
    }

    @Override
    public GObject eval(Scope scope) {

        return GObject.VOID;
    }
}
