package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.symtab.SymbolClass;

import java.util.List;

public class AST_Stmt_DefVar extends AST_Stmt {

    public final AST_Typename type;
    public final String name;
    public final AST_Expr initexpr;  // nullable.

    public AST_Stmt_DefVar(AST_Typename type, String name, AST_Expr initexpr) {
        this.type = type;
        this.name = name;
        this.initexpr = initexpr;
    }

    @Override
    public String toString() {
        return String.format("ast_vardef{%s %s = %s}", type, name, initexpr);
    }
}
