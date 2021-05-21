package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.parser.Parserls;
import outskirts.util.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

import static outskirts.lang.langdev.parser.Parserls.pass;
import static outskirts.lang.langdev.parser.Parserls.struct;

public class Main {

    public static void main(String[] args) throws IOException {

        String s = IOUtils.toString(new FileInputStream("main.g"));

        Lexer lex = new Lexer();
        lex.read(s);

        int i = 0;

        var exprbase = pass().initname("expression");
        {
            Parserls varname;
            var primary = pass().or(
                    struct(AST_Token_LiteralNumber::new).number().initname("NumberLiteral"),
                    struct(AST_Token_LiteralString::new).string().initname("StringLiteral"),
                    varname=struct(AST_Token_VariableName::new).name().initname("VariableName")
            ).initname("Primary");

            var fargls = struct(ASTls::new).op(pass().and(exprbase).repeat(pass().id(",").and(exprbase))).initname("FuncArgs");

            var expr1 = pass();
                expr1.or(
                    pass().id("(").and(exprbase).id(")").initname("ExprParentheses"),
                    pass().or(primary).repeat(pass().or(
                            pass().id(".").and(varname).composer(stk -> {
                                var r = stk.pop();
                                var l = stk.pop();
                                stk.push(new AST_Expr_BiOper(l, ".", r));
                            }).initname("MemberAccess"),
                            pass().id("(").and(fargls).id(")").composer(stk -> {
                                ASTls ls = stk.pop();
                                var f = stk.pop();
                                stk.push(new AST_Expr_FuncCall(f, ls.toArray()));
                            }).initname("FuncCall"),
                            pass().id("[").and(exprbase).id("]").composer(stk -> {
                                var r = stk.pop();
                                var l = stk.pop();
                                stk.push(new AST() {
                                    @Override
                                    public String toString() {
                                        return "array_access{"+l+"["+r+"]}";
                                    }
                                });
                            }).initname("ArrayAccess")
                    ))
            ).initname("expr1");

//            int[] arr = {};
//            int in = ((arr)).[0];
//
//            AST l, r;
//            AST_Token o;
//            l = primary.readone(lex);
//            while (true) {
//                o = (AST_Token)primary.readone(lex);
//                if (o.text().equals(".")) {
//                    r = primary.readone(lex);
//                    l = new AST_Expr_BiOper(l, o, r);
//                } else if (o.text().equals("[")){
//                    r = exprbase.readone(lex);
//                    pass().id("]").read(lex);
//                    l = new AST_Expr_ArrAcc(l, r);
//                } else if (o.text().equals("(")) {
//                    r = p_args.read(lex);
//                    l = new FunCall(l, r);
//                }
//            }

            var expr2 = pass().or(
                    struct(AST_Expr_PostInc::new).and(expr1).id("++").markLookahead().initname("PostInc"),
                    expr1
            ).initname("expr2_suffix");

            var expr3 = pass().or(
                    struct(AST_Expr_Neg::new).id("-").and(expr2).initname("Neg"),
                    expr2
            ).initname("expr3_prefix");

            var expr4 = struct(AST_Expr_BiOper::composite).and(expr3).repeat(pass().iden("*", "/").and(expr3));
            var expr5 = struct(AST_Expr_BiOper::composite).and(expr4).repeat(pass().iden("+", "-").and(expr4));

            var expr15 = struct(AST_Expr_BiOper::composite);
                expr15.and(expr5).op(pass().iden("=").and(expr15));

            exprbase.and(expr15);  // init.


            System.out.println(exprbase.readone(lex));
        }




//        {
//            var extls = pass().id(":").name().repeat(pass().id(",").name());
//            var clas = struct().id("class").name().op(extls).id("{").id("}");
//
//
//
//        }
    }

}
