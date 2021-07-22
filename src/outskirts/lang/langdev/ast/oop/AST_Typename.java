package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.ast.AST_Expr_PrimaryVariableName;
import outskirts.lang.langdev.ast.ASTls;

import java.util.Arrays;
import java.util.List;

public class AST_Typename extends AST {

    public final String name;
    public final AST_Typename[] genericTypes;

    public AST_Typename(String name, AST_Typename[] genericTypes) {
        this.name = name;
        this.genericTypes = genericTypes;
    }

    public AST_Typename(List<AST> ls) {
        this(ls.get(0).tokentext(), ls.get(1) == null ? null : ((ASTls)ls.get(1)).toArrayt(AST_Typename[]::new));
    }

    @Override
    public String toString() {
        return "T::"+name+"<"+Arrays.toString(genericTypes)+">";
    }
}
