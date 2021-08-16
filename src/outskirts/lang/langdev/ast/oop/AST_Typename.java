package outskirts.lang.langdev.ast.oop;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.parser.LxParser;
import outskirts.lang.langdev.symtab.SymbolClass;
import outskirts.lang.langdev.symtab.TypeSymbol;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AST_Typename extends AST {

    // Type Symbol
    public TypeSymbol sym;

    public final AST_Expr nameptr;  // AST_Expr_PrimaryVariableName or AST_Expr_BiOper.
    public final List<AST_Typename> genericArgs;

    public AST_Typename(AST_Expr nameptr, List<AST_Typename> genericArgs) {
        this.nameptr = nameptr;
        this.genericArgs = genericArgs;

    }

    public static String SimpleExpand(AST_Typename a) {
        return LxParser._ExpandQualifiedName(a.nameptr) + (a.genericArgs.isEmpty() ? "" : "<"+ a.genericArgs.stream().map(AST_Typename::SimpleExpand).collect(Collectors.joining(", ")) +">");
    }

//    public static String EvalTypename(AST_Typename a, Scope scope) {
//        Scope t = evalTypename_Name(a.nameptr, scope);
//        String typename = t.currentClassnamePrefix();
//
//        if (!a.genericArgs.isEmpty()) {
//            List<String> gnargs = new ArrayList<>();
//            for (AST_Typename e : a.genericArgs) {
//                gnargs.add(EvalTypename(e, scope));
//            }
//            typename += "<"+ String.join(", ", gnargs) +">";
//        }
//        return typename;
//    }
//    private static Scope evalTypename_Name(AST_Expr a, Scope scope) {
//        if (a instanceof AST_Expr_PrimaryVariableName) {
//            GObject o = scope.access(((AST_Expr_PrimaryVariableName)a).name);
//            Validate.isTrue(o.type.equals("typedef"));
//            return (Scope)o.value;
//        } else if (a instanceof AST_Expr_OperBi) {
//            AST_Expr_OperBi c = (AST_Expr_OperBi)a;
//            Validate.isTrue(c.operator.equals("."));
//            Scope t = evalTypename_Name(c.left, scope);
//
//            GObject o = t.access(c.right.varname());
//            Validate.isTrue(o.type.equals("typedef"));
//            return (Scope)o.value;
//        } else
//            throw new IllegalStateException();
//    }

    @Override
    public String toString() {
        return "T::"+nameptr+"<"+genericArgs+">";
    }
}
