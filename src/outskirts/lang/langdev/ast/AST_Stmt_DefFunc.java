package outskirts.lang.langdev.ast;

import java.util.List;

public class AST_Stmt_DefFunc extends AST_Stmt {

    public final AST__Typename returntype;
    public final String name;
    public final List<AST_Func_Param> params; // unclear
    public final AST_Stmt_Block body;     // ?? block ??or expr?

    public AST_Stmt_DefFunc(AST__Typename returntype, String name, List<AST_Func_Param> params, AST_Stmt_Block body) {
        this.returntype = returntype;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public String toString() {
        return "ast_stmt_funcdef{"+returntype+" "+name+"("+params+")"+body+"}";
    }

    public static class AST_Func_Param extends AST {

        public final AST__Typename type;
        public final String name;

        public AST_Func_Param(AST__Typename type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
