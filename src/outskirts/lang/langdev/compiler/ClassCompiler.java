package outskirts.lang.langdev.compiler;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.CodeGen;
import outskirts.lang.langdev.symtab.Modifiers;
import outskirts.lang.langdev.symtab.SymbolBuiltinType;
import outskirts.lang.langdev.symtab.SymbolFunction;
import outskirts.lang.langdev.symtab.SymbolVariable;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.List;

public final class ClassCompiler {

    private static final int CLASS_VERSION = 1;


    public static void _CompileFunction(AST_Stmt_DefFunc c, ConstantPool constantpool) {
        // Problem: One Template AST can't use for Multiply FilledInstance.
        // since AST have some Symbol, these Symbols shouldn't been shared between diff refied-instances. they are different symbols.

//        CodeBuf codebuf = new CodeBuf(constantpool, c.symf.getParameters());

//        c.getBody().accept(new CodeGen(), codebuf);
//
//        if (c.symf.getReturnType() == SymbolBuiltinType._void) {
//            codebuf._ret(0);
//        }
//
////                System.out.println("Compiled Function: "+codebuf);
//        sf_codebuf_out.codebuf = codebuf;
//        System.out.println("CompFuncSym "+sf_codebuf_out.getQualifiedName()+"::"+c.symf.hashCode());
    }

    public static void compileClass(AST_Stmt_DefClass a) {
        if (a.getGenericsParameters().size() > 0) {
            System.out.println("Not compiling class \""+a.sym.getQualifiedName()+"\": Generics Class.");
            return; }
        ConstantPool constantpool = new ConstantPool();

//        String thisclass = a.sym.getQualifiedName();
//        List<String> superclasses = new ArrayList<>();
//        for (AST_Expr sup_typ : a.getSuperTypeExpressions()) {
//            superclasses.add(sup_typ.getTypeSymbol().getQualifiedName());
//        }

//        List<ClassFile.Field> fields = new ArrayList<>();
        for (AST_Stmt m : a.getMembers()) {
//            AST__Modifiers mdf = ((AST.Modifierable)m).getModifiers();
//            boolean isStatic = Modifiers.isStatic(mdf.getModifierCode());

            if (m instanceof AST_Stmt_DefClass) {
                compileClass((AST_Stmt_DefClass)m);
//                throw new UnsupportedOperationException("Inner Class Not Supported yet.");
            } else if (m instanceof AST_Stmt_DefFunc) {
                AST_Stmt_DefFunc c = (AST_Stmt_DefFunc)m;

                _CompileFunction(c, constantpool);

//                String typename = "function<"+c.getReturnTypeExpression().getTypeSymbol().getQualifiedName();
//                for (AST_Stmt_DefVar param : c.getParameters()) {
//                    typename += ", "+param.getTypeExpression().getTypeSymbol().getQualifiedName();
//                }
//                typename += ">";
//                String typename = c.symf.getQualifiedName();
//                ClassFile.Field fld = new ClassFile.Field(c.getName(), mdf.getModifierCode(), typename);
//                fld._codebuf = c.symf.codebuf;
//                fields.add(fld);
            } else if (m instanceof AST_Stmt_DefVar) {
//                AST_Stmt_DefVar c = (AST_Stmt_DefVar)m;

//                fields.add(new ClassFile.Field(c.getName(), mdf.getModifierCode(), c.getTypeExpression().getTypeSymbol().getQualifiedName()));
            } else
                throw new IllegalStateException("Unsupported member: "+m);
        }

//        ClassFile f = new ClassFile(CLASS_VERSION,
//                constantpool,
//                thisclass, superclasses.toArray(new String[0]),
//                fields.toArray(new ClassFile.Field[0])
//                );
//        System.out.println("Compiled ClassFile: "+f);

//        a.sym.compiledclfile = f;
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