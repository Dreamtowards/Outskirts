package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AST_Stmt_Block extends AST_Stmt {

    public final List<AST_Stmt> stmts;

    public AST_Stmt_Block(List<AST_Stmt> stmts) {
        this.stmts = stmts;
    }

//    public AST_Stmt_Block(List<AST> ls) {
//        for (AST a : ls) {
//            if (!(a instanceof AST_Stmt))
//                throw new RuntimeException("Bad Element, not Stmt: "+a);
//        }
//        this.stmts = (List)ls;
////        this(Arrays.asList(ls.toArray()).toArray(new AST_Stmt[0]));
//    }

    @Override
    public String toString() {
        return "ast_stmt_block{"+ stmts +'}';
    }
}
