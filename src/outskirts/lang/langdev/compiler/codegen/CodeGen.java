package outskirts.lang.langdev.compiler.codegen;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
import outskirts.lang.langdev.symtab.SymbolBuiltinType;

public class CodeGen implements ASTVisitor<CodeBuf> {



    @Override
    public void visitStmtBlock(AST_Stmt_Block a, CodeBuf buf) {
        for (AST_Stmt stmt : a.getStatements()) {
            stmt.accept(this, buf);
        }
    }

    @Override
    public void visitStmtDefVar(AST_Stmt_DefVar a, CodeBuf buf) {
        // supp in func scope.
        buf.defvar(a.getName(), a.getTypename().sym);


    }

}
