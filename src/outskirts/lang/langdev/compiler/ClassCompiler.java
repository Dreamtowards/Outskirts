package outskirts.lang.langdev.compiler;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.CodeGen;
import outskirts.lang.langdev.symtab.Modifiers;
import outskirts.lang.langdev.symtab.SymbolBuiltinType;
import outskirts.lang.langdev.symtab.SymbolVariable;

import java.util.ArrayList;
import java.util.List;

public final class ClassCompiler {

    private static final int CLASS_VERSION = 1;

    public static void compileClass(AST_Stmt_DefClass a) {
        ConstantPool constantpool = new ConstantPool();

        String thisclass = a.sym.getQualifiedName();

        List<String> superclasses = new ArrayList<>();
        for (AST_Expr sup_typ : a.getSuperTypeExpressions()) {
            superclasses.add(sup_typ.getTypeSymbol().getQualifiedName());
        }

        List<ClassFile.Field> fields = new ArrayList<>();
        for (AST_Stmt m : a.getMembers()) {
            AST__Modifiers mdf = ((AST.Modifierable)m).getModifiers();

            boolean isStatic = Modifiers.isStatic(mdf.getModifierCode());

            if (m instanceof AST_Stmt_DefClass) {
                compileClass((AST_Stmt_DefClass)m);
//                throw new UnsupportedOperationException("Inner Class Not Supported yet.");
            } else if (m instanceof AST_Stmt_DefFunc) {
                AST_Stmt_DefFunc c = (AST_Stmt_DefFunc)m;

                CodeBuf codebuf = new CodeBuf(constantpool);
                for (SymbolVariable prm : c.symf.params) {
                    codebuf.localdef(prm.getSimpleName(), prm.getType());
                }

                c.getBody().accept(new CodeGen(), codebuf);

//                System.out.println("Compiled Function: "+codebuf);
                c.symf.codebuf = codebuf;

                String typename = "function<"+c.getReturnTypeExpression().getTypeSymbol().getQualifiedName();
                for (AST_Stmt_DefVar param : c.getParameters()) {
                    typename += ", "+param.getTypeExpression().getTypeSymbol().getQualifiedName();
                }
                typename += ">";

                ClassFile.Field fld = new ClassFile.Field(c.getName(), mdf.getModifierCode(), typename);
                fld._codebuf = codebuf;
                fields.add(fld);
            } else if (m instanceof AST_Stmt_DefVar) {
                AST_Stmt_DefVar c = (AST_Stmt_DefVar)m;

                fields.add(new ClassFile.Field(c.getName(), mdf.getModifierCode(), c.getTypeExpression().getTypeSymbol().getQualifiedName()));
            } else
                throw new IllegalStateException("Unsupported member: "+m);
        }

        ClassFile f = new ClassFile(CLASS_VERSION,
                constantpool,
                thisclass, superclasses.toArray(new String[0]),
                fields.toArray(new ClassFile.Field[0])
                );
//        System.out.println("Compiled ClassFile: "+f);

        a.sym.compiledclfile = f;
    }

    public static void compileStmtBlockStmts(List<AST_Stmt> stmts) {
        // no sub scope.

        for (AST_Stmt stmt : stmts) {
            compileStmt(stmt);
        }
    }

    public static void compileStmt(AST_Stmt a)
    {
        if (a instanceof AST_Stmt_DefClass)
        {
            compileClass((AST_Stmt_DefClass)a);
        }
        else if (a instanceof AST_Stmt_Namespace)
        {
            compileStmtBlockStmts(((AST_Stmt_Namespace)a).getStatements());
        }
    }


}