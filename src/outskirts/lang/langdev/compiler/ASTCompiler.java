package outskirts.lang.langdev.compiler;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.compiler.codegen.CodeBuf;
import outskirts.lang.langdev.compiler.codegen.CodeGen;

import java.util.ArrayList;
import java.util.List;

public class ASTCompiler {

    public static List<CodeBuf> _COMPILED = new ArrayList<>();

    private static final int CLASS_VERSION = 1;

    public static void compileClass(AST_Stmt_DefClass a) {
        ConstantPool constantpool = new ConstantPool();

        String thisclass = a.thisclass.parNam();

        List<String> superclasses = new ArrayList<>();
        for (AST_Typename sup : a.superclasses) {
            superclasses.add(
                    sup.sym.parNam()
            );
        }

        List<ClassFile.Field> fields = new ArrayList<>();
        for (AST_Class_Member mb : a.members) {
            short mod = 0;
            if (mb.isStatic()) mod |= ClassFile.Field.MASK_STATIC;

            AST_Stmt m = mb.member;
            if (m instanceof AST_Stmt_DefClass) {
                compileClass((AST_Stmt_DefClass)m);
//                throw new UnsupportedOperationException("Inner Class Not Supported yet.");
            } else if (m instanceof AST_Stmt_DefFunc) {
                AST_Stmt_DefFunc c = (AST_Stmt_DefFunc)m;

                CodeBuf codebuf = new CodeBuf(constantpool);
                if (!mb.isStatic())
                    codebuf.defvar("this");
                for (AST_Stmt_DefFunc.AST_Func_Param param : c.params)
                    codebuf.defvar(param.name);
                CodeGen.compileStmtBlock(c.body, codebuf);
                System.out.println("Compiled Function: "+codebuf);
                _COMPILED.add(codebuf);

                String typename = "function<"+c.returntype.sym.parNam();
                for (AST_Stmt_DefFunc.AST_Func_Param param : c.params) {
                    typename += ", "+param.type.sym.parNam();
                }
                typename += ">";
                fields.add(new ClassFile.Field(c.name, mod, typename));
            } else if (m instanceof AST_Stmt_DefVar) {
                AST_Stmt_DefVar c = (AST_Stmt_DefVar)m;

                fields.add(new ClassFile.Field(c.name, mod, c.type.sym.parNam()));
            } else
                throw new IllegalStateException("Unsupported member: "+m);
        }



        ClassFile f = new ClassFile(CLASS_VERSION,
                constantpool,
                thisclass, superclasses.toArray(new String[0]),
                fields.toArray(new ClassFile.Field[0])
                );
        // System.out.println("Compiled ClassFile: "+f);

        ClassFile._CLASSPATH.put(thisclass, f);
    }

    public static void compilePkgStmtBlock(AST_Stmt_Block a) {
        // no sub scope.

        for (AST_Stmt stmt : a.stmts) {
            compilePkgClassStmt(stmt);
        }
    }

    public static void compilePkgClassStmt(AST_Stmt a)
    {
        if (a instanceof AST_Stmt_DefClass)
        {
            compileClass((AST_Stmt_DefClass)a);
        }
    }


}