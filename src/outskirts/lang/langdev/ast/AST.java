package outskirts.lang.langdev.ast;

public abstract class AST {

    public String tokentext() {
        return ((AST_Token)this).text();
    }

    public String varname() {
        return ((AST_Expr_PrimaryVariableName)this).name;
    }

    @Override
    public String toString() {
        return "base_ast"+getClass();
    }

}
