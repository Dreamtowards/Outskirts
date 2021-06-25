package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.ArrayList;
import java.util.List;

public class AST_Stmt_Block extends AST {

    private final List<AST> stmts = new ArrayList<>();

    public AST_Stmt_Block(List<AST> ls) {
        stmts.addAll(ls);
    }

    @Override
    public GObject eval(Scope scope) {
        Scope scblock = new Scope(scope);
        for (AST stmt : stmts) {
            stmt.eval(scblock);
        }
        return GObject.VOID;
    }

    @Override
    public String toString() {
        return "ast_stmt_block{"+stmts+'}';
    }
}
