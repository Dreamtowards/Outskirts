package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;

import java.util.Arrays;
import java.util.List;

public class AST_Typename extends AST {

    public final AST_Expr nameptr;  // AST_Expr_PrimaryVariableName or AST_Expr_BiOper.
    public final List<AST_Typename> genericArgs;

    public AST_Typename(AST_Expr nameptr, List<AST_Typename> genericArgs) {
        this.nameptr = nameptr;
        this.genericArgs = genericArgs;
    }

//    public AST_Typename(List<AST> ls) {
//        this((AST_Expr)ls.get(0), ls.get(1) == null ? null : ((ASTls)ls.get(1)).toArrayt(AST_Typename[]::new));
//    }

    public String nameptrExpanded() {
        return expandPlainName(nameptr);
    }

    private static String expandPlainName(AST_Expr ex) {
        if (ex instanceof AST_Expr_PrimaryVariableName)
            return ex.varname();
        else if (ex instanceof AST_Expr_OperBi && ((AST_Expr_OperBi) ex).operator.equals("."))
            return expandPlainName(((AST_Expr_OperBi)ex).left) + "." + expandPlainName(((AST_Expr_OperBi)ex).right);
        else
            throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "T::"+nameptr+"<"+genericArgs+">";
    }
}
