package outskirts.lang.langdev.ast;

import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.symtab.SymbolFunction;

import java.util.List;

/**
 * the function, can't been simply a variable (tho the beginning(annotations, modifiers, typename, iden) is same),
 * but function still can been a SyntaxSugar. the VariableType is function<ret_type, param_types..>
 */
public class AST_Stmt_DefFunc extends AST_Stmt implements AST.Modifierable {

    private final AST_Expr returntype;
    private final String name;
    private final List<AST_Stmt_DefVar> params; // unclear
    private final AST_Stmt_Block body;     // ?? block ??or expr?

    private final AST__Modifiers modifiers;

    public SymbolFunction symf;

    public AST_Stmt_DefFunc(AST_Expr returntype, String name, List<AST_Stmt_DefVar> params, AST_Stmt_Block body, AST__Modifiers modifiers) {
        this.returntype = returntype;
        this.name = name;
        this.params = params;
        this.body = body;
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public AST_Expr getReturnTypeExpression() {
        return returntype;
    }

    public List<AST_Stmt_DefVar> getParameters() {
        return params;
    }

    public AST_Stmt_Block getBody() {
        return body;
    }

    @Override
    public AST__Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "ast_stmt_funcdef{"+returntype+" "+name+"("+params+")"+body+"}";
    }

}
