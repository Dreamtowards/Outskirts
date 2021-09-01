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

    private final AST__Typename returntype;
    private final String name;
    private final List<AST_Stmt_DefVar> params; // unclear
    private final AST_Stmt_Block body;     // ?? block ??or expr?

    public AST__Modifiers modifiers;

    public SymbolFunction symf;

    public AST_Stmt_DefFunc(AST__Typename returntype, String name, List<AST_Stmt_DefVar> params, AST_Stmt_Block body) {
        this.returntype = returntype;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public AST__Typename getReturnTypename() {
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
    public <P> void accept(ASTVisitor<P> visitor, P p) {
        visitor.visitStmtDefFunc(this, p);
    }

    @Override
    public String toString() {
        return "ast_stmt_funcdef{"+returntype+" "+name+"("+params+")"+body+"}";
    }

}
