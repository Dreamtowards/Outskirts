package outskirts.lang.langdev.ast.srcroot;

import outskirts.lang.langdev.ast.AST;

import java.util.Arrays;
import java.util.List;

public class AST_SR_StmtLs extends AST_SR {

    public final AST[] elements;  // SR_StmtUsing, SR_StmtPackage, Stmt

    public AST_SR_StmtLs(AST[] elements) {
        this.elements = elements;
    }

    public AST_SR_StmtLs(List<AST> ls) {
        this(ls.toArray(AST[]::new));
    }

    @Override
    public String toString() {
        return "AST_SR_StmtLs{"+Arrays.toString(elements)+'}';
    }
}
