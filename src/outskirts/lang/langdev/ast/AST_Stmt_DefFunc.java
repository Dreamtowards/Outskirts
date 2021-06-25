package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.util.Val;
import outskirts.util.Validate;

import java.util.List;

public class AST_Stmt_DefFunc extends AST {

    private AST type;
    private String name;
    private ASTls params;
    private AST body;

    public AST_Stmt_DefFunc(AST type, String name, ASTls params, AST body) {
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public AST_Stmt_DefFunc(List<AST> ls) {
        this(ls.get(0), ls.get(1).tokentext(), (ASTls)ls.get(2), ls.get(3));
    }

    @Override
    public GObject eval(Scope scope) {

        scope.declare(name, new GObject((FuncPtr) args -> {
            Validate.isTrue(args.length == params.size());

            Scope funcSc = new Scope(scope);
            for (int i = 0;i < params.size();i++) {
                String name = ((ASTls)params.get(i)).get(1).tokentext();
                funcSc.declare(name, args[i]);
            }

            try {
                body.eval(funcSc);
            } catch (AST_Stmt_FuncReturn.Return rv) {
                return rv.expr.eval(funcSc);
            }

            return GObject.VOID;
        }));

        return GObject.VOID;
    }

    @Override
    public String toString() {
        return "ast_stmt_funcdef{"+type+" "+name+"("+params+")"+body+"}";
    }
}
