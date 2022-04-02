//package outskirts.lang.langdev.ast;
//
//import outskirts.lang.langdev.ast.astvisit.ASTVisitor;
//import outskirts.lang.langdev.parser.LxParser;
//import outskirts.lang.langdev.symtab.TypeSymbol;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Really has the necessary to have a AST_Typename.?
// * its like a AST_Expr (typename) plus a <AST_Expr, ..> (Generic Arguments) i.e. like AST_TypeAndGenericArgs
// *
// * and, in somewhere like Variable-Type-Decl, there can have a valid AST_Typename place, but in usual Expr, there no AST_Typename anymore!
// * but just basic Chains, series of AST_Expr:AST_Expr_MemberAccess.
// *
// * AST_Expr_OperDiamond { Expr expr, Expr args; }  // expr<arg1, arg2..>
// */
//public class AST__Typename extends AST {
//
//    // Duplicated with Expr.evaltype .?
//    // Real is a TypeSymbol.? SymbolBuiltinType, SymbolClass allowed,
////    public TypeSymbol sym;
//
//    private final AST_Expr nameptr;  // AST_Expr_PrimaryVariableName or AST_Expr_BiOper.
//    private final List<AST__Typename> genericArgs;  // List<GenericArgument : Typename | Const Int | Wildcast> TypeArguments
//
//    public AST__Typename(AST_Expr type, List<AST__Typename> genericArgs) {
//        this.nameptr = type;
//        this.genericArgs = genericArgs;
//    }
//
//    public AST_Expr getType() {
//        return nameptr;
//    }  // getQualifit..or?
//
//
////    @Override
////    public <P> void accept(ASTVisitor<P> visitor, P p) {
////        visitor.visit_Typename(this, p);
////    }
//
//    //    public static String SimpleExpand(AST__Typename a) {
////        return LxParser._ExpandQualifiedName(a.nameptr) + (a.genericArgs.isEmpty() ? "" : "<"+ a.genericArgs.stream().map(AST__Typename::SimpleExpand).collect(Collectors.joining(", ")) +">");
////    }
//
////    public static String EvalTypename(AST_Typename a, Scope scope) {
////        Scope t = evalTypename_Name(a.nameptr, scope);
////        String typename = t.currentClassnamePrefix();
////
////        if (!a.genericArgs.isEmpty()) {
////            List<String> gnargs = new ArrayList<>();
////            for (AST_Typename e : a.genericArgs) {
////                gnargs.add(EvalTypename(e, scope));
////            }
////            typename += "<"+ String.join(", ", gnargs) +">";
////        }
////        return typename;
////    }
////    private static Scope evalTypename_Name(AST_Expr a, Scope scope) {
////        if (a instanceof AST_Expr_PrimaryVariableName) {
////            GObject o = scope.access(((AST_Expr_PrimaryVariableName)a).name);
////            Validate.isTrue(o.type.equals("typedef"));
////            return (Scope)o.value;
////        } else if (a instanceof AST_Expr_OperBi) {
////            AST_Expr_OperBi c = (AST_Expr_OperBi)a;
////            Validate.isTrue(c.operator.equals("."));
////            Scope t = evalTypename_Name(c.left, scope);
////
////            GObject o = t.access(c.right.varname());
////            Validate.isTrue(o.type.equals("typedef"));
////            return (Scope)o.value;
////        } else
////            throw new IllegalStateException();
////    }
//
//    @Override
//    public String toString() {
//        return "T::"+nameptr+"<"+genericArgs+">";
//    }
//}
