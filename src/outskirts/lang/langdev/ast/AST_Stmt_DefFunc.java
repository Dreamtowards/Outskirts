package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_DefFunc extends AST_Stmt {

    public final String type;
    public final String name;
    public final ASTls params; // unclear
    public final AST body;     // ?? block ??or expr?

    public AST_Stmt_DefFunc(String type, String name, ASTls params, AST body) {
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public AST_Stmt_DefFunc(List<AST> ls) {
        this(((AST_Expr_PrimaryVariableName)ls.get(0)).name, ((AST_Expr_PrimaryVariableName)ls.get(1)).name, (ASTls)ls.get(2), ls.get(3));
    }

    @Override
    public String toString() {
        return "ast_stmt_funcdef{"+type+" "+name+"("+params+")"+body+"}";
    }
}
