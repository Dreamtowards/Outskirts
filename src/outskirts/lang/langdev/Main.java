package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.parser.Parserls;
import outskirts.util.IOUtils;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;

import static outskirts.lang.langdev.parser.Parserls.pass;
import static outskirts.lang.langdev.parser.Parserls.struct;

public class Main {

    public static void main(String[] args) throws IOException {

        String s = IOUtils.toString(new FileInputStream("main.g"));

        Lexer lex = new Lexer();
        lex.read(s);

        var typename = struct(AST_Token_VariableName::new).name().initname("TypeName");

        var varname = struct(AST_Token_VariableName::new).name().initname("VariableName");

        var exprbase = pass().initname("expression");
        {
            var primary = pass().or(
                    struct(AST_Token_LiteralNumber::new).number().initname("NumberLiteral"),
                    struct(AST_Token_LiteralString::new).string().initname("StringLiteral"),
                    varname
            ).initname("Primary");

            var fnargls = struct(ASTls::new).op(pass().and(exprbase).repeat(pass().id(",").and(exprbase))).initname("FuncArgs");

            var expr0 = pass().or(
                    pass().id("(").and(exprbase).id(")").initname("ExprParentheses"),
                    primary
            ).initname("expr0_1");

            var expr1 = pass().and(expr0).repeat(pass().or(
                    pass().iden(".").and(varname).composesp(3, AST_Expr_OperBi::new).initname("MemberAccess"),
                    pass().id("(").and(fnargls).id(")").composesp(2, AST_Expr_FuncCall::new).initname("FuncCall")
            )).initname("expr1");

            var expr2 = pass().and(expr1).repeat(
                    pass().iden("++", "--").composesp(2, AST_Expr_OperUnaryPost::new).initname("UnaryOperPost")
            ).initname("expr2_unarypost");

            var expr3 = pass();
                expr3.or(
                    struct(AST_Expr_OperUnaryPre::new).iden("++","--", "+","-", "!", "~").and(expr3).initname("UnaryOperPre"),
                    expr2
            ).initname("expr3_unarypre");

            var expr4 = pass().and(expr3).repeat(pass().iden("*", "/").and(expr3).composesp(3, AST_Expr_OperBi::new));
            var expr5 = pass().and(expr4).repeat(pass().iden("+", "-").and(expr4).composesp(3, AST_Expr_OperBi::new));

            var expr15 = pass();
                expr15.and(expr5).op(pass().iden("=").and(expr15).composesp(3, AST_Expr_OperBi::new));

            exprbase.and(expr15);  // init.
        }

        Parserls stmt = pass().initname("StatmentElection");
        stmt.or(
                struct(AST_Stmt_Block::new).id("{").repeat(stmt).id("}").initname("StmtBlock"),
                struct(AST_Stmt_If::new).id("if").id("(").and(exprbase).id(")").and(stmt).op(pass().id("else").and(stmt)).initname("StmtIf"),
                struct(AST_Stmt_While::new).id("while").id("(").and(exprbase).id(")").and(stmt).initname("StmtWhile"),
                struct(AST_Stmt_VariableDeclare::new).and(typename).and(varname).markLookahead().op(pass().id("=").and(exprbase)).id(";").initname("StmtVarDef"),
                pass().and(exprbase).id(";").markLookahead().initname("StmtExpr")
        );

        Scope sc = new Scope(null);
        sc.declare("prt", new GObject((AST_Expr_FuncCall.FuncInterf) args1 -> {
            System.out.println(args1[0].value);
            return GObject.VOID;
        }));
        sc.declare("alert", new GObject((AST_Expr_FuncCall.FuncInterf) args1 -> {
            JOptionPane.showMessageDialog(null, args1[0].value);
            return GObject.VOID;
        }));

        stmt.readone(lex).eval(sc);
//        System.out.println(stmt.readone(lex).eval(sc).value);



//        {
//            var extls = pass().id(":").name().repeat(pass().id(",").name());
//            var clas = struct().id("class").name().op(extls).id("{").id("}");
//
//
//
//        }
    }


}