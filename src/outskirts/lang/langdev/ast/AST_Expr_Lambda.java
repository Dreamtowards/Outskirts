package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Validate;

import java.util.List;

public class AST_Expr_Lambda extends AST {

    private ASTls params;
    private AST body;  // stmt_block or exprbase.

    public AST_Expr_Lambda(ASTls params, AST body) {
        this.params = params;
        this.body = body;
    }

    public AST_Expr_Lambda(List<AST> ls) {
        this((ASTls)ls.get(0), ls.get(1));
    }

    @Override
    public GObject eval(Scope scope) {  // may duplicated with AST_Stmt_DefFunc.
        return new GObject((FuncPtr) args -> {
            Validate.isTrue(args.length == params.size());

            Scope funcSc = new Scope(scope);
            for (int i = 0;i < params.size();i++) {
                String name = params.get(i).tokentext();
                funcSc.declare(name, args[i]);
            }

            if (body instanceof AST_Stmt_Block) {
                try {
                    body.eval(funcSc);
                    return GObject.VOID;
                } catch (AST_Stmt_FuncReturn.Return rv) {
                    return rv.expr.eval(funcSc);
                }
            } else {
                // if return-type not is void.
                return body.eval(funcSc);
            }
        });
    }

    @Override
    public String toString() {
        return "ast_expr_lambda{"+params+"}";
    }
}
