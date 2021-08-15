package outskirts.lang.langdev.ast;

import outskirts.util.Validate;

import java.util.List;

public class ASTvoid extends AST {

    public ASTvoid(List<AST> ls) {
        Validate.isTrue(ls.size() == 0);
    }

    @Override
    public String toString() {
        return "ast_void";
    }
}
