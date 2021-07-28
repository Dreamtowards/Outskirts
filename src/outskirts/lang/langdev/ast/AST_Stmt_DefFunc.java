package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_DefFunc extends AST_Stmt {

    public final AST_Typename type;
    public final String name;
    public final List<AST_Func_Param> params; // unclear
    public final AST body;     // ?? block ??or expr?

    public AST_Stmt_DefFunc(AST_Typename type, String name, List<AST_Func_Param> params, AST body) {
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public String toString() {
        return "ast_stmt_funcdef{"+type+" "+name+"("+params+")"+body+"}";
    }

    public static class AST_Func_Param extends AST {

        public final AST_Typename type;
        public final String name;

        public AST_Func_Param(AST_Typename type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
