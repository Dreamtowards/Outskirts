package outskirts.lang.langdev.ast;

import java.util.Arrays;
import java.util.List;

public final class ASTls extends AST {

    private AST[] list;

    public ASTls(List<AST> ls) {
        this.list = ls.toArray(new AST[0]);
    }

    public AST[] toArray() {
        return list;
    }

    @Override
    public String toString() {
        return "astls"+ Arrays.toString(list);
    }
}
