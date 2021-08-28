package outskirts.lang.langdev.compiler;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.AST__Typename;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.CodeGen;

import java.util.ArrayList;
import java.util.List;

public class ASTCompiler {

    public static List<CodeBuf> _COMPILED = new ArrayList<>();

    private static final int CLASS_VERSION = 1;

    public static void compileClass(AST_Stmt_DefClass a) {
        ConstantPool constantpool = new ConstantPool();

        String thisclass = a.sym.getQualifiedName();

        List<String> superclasses = new ArrayList<>();
        for (AST__Typename sup : a.superclasses) {
            superclasses.add(
                    sup.sym.getQualifiedName()
            );
        }

        List<ClassFile.Field> fields = new ArrayList<>();
        for (AST_Stmt m : a.members) {
            AST__Modifiers modif = ((AST.Modifierable)m).getModifiers();

            boolean isStatic = modif.isStatic();

            short modc = 0;
            if (isStatic)
                modc |= ClassFile.Field.MASK_STATIC;

            if (m instanceof AST_Stmt_DefClass) {
                compileClass((AST_Stmt_DefClass)m);
//                throw new UnsupportedOperationException("Inner Class Not Supported yet.");
            } else if (m instanceof AST_Stmt_DefFunc) {
                AST_Stmt_DefFunc c = (AST_Stmt_DefFunc)m;

                CodeBuf codebuf = new CodeBuf(constantpool);
                if (!isStatic)
                    codebuf.defvar("this");
                for (AST_Stmt_DefVar param : c.params)
                    codebuf.defvar(param.name);
                CodeGen.compileStmtBlock(c.body, codebuf);
                System.out.println("Compiled Function: "+codebuf);
                _COMPILED.add(codebuf);

                String typename = "function<"+c.returntype.sym.getQualifiedName();
                for (AST_Stmt_DefVar param : c.params) {
                    typename += ", "+param.type.sym.getQualifiedName();
                }
                typename += ">";
                fields.add(new ClassFile.Field(c.name, modc, typename));
            } else if (m instanceof AST_Stmt_DefVar) {
                AST_Stmt_DefVar c = (AST_Stmt_DefVar)m;

                fields.add(new ClassFile.Field(c.name, modc, c.type.sym.getQualifiedName()));
            } else
                throw new IllegalStateException("Unsupported member: "+m);
        }



        ClassFile f = new ClassFile(CLASS_VERSION,
                constantpool,
                thisclass, superclasses.toArray(new String[0]),
                fields.toArray(new ClassFile.Field[0])
                );
        System.out.println("Compiled ClassFile: "+f);

        ClassFile._CLASSPATH.put(thisclass, f);
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
            compileStmtBlockStmts(((AST_Stmt_Namespace) a).stmts);
        }
    }


}