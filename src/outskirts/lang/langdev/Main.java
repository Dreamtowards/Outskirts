package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Class;
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

        var stmt = pass().initname("StatmentElection");
        var stmt_block = struct(AST_Stmt_Block::new);

        var exprbase = pass().initname("expression");
        {
            var primary = pass().or(
                    struct(AST_Token_LiteralNumber::new).number().initname("NumberLiteral"),
                    struct(AST_Token_LiteralString::new).string().initname("StringLiteral"),
                    varname
            ).initname("Primary");

            var func_args = struct(ASTls::new).repeatjoin(exprbase, ",").initname("FuncArgs");

            var expr0 = pass().or(
                    pass().id("(").and(exprbase).id(")").initname("ExprParentheses"),
                    primary
            ).initname("expr0_1");

            var expr1 = pass().and(expr0).repeat(pass().or(
                    pass().iden(".").and(varname).composesp(3, AST_Expr_OperBi::new).initname("MemberAccess"),
                    pass().id("(").and(func_args).id(")").composesp(2, AST_Expr_FuncCall::new).initname("FuncCall")
            )).initname("expr1");

            var expr2 = pass().and(expr1).repeat(
                    pass().iden("++", "--").composesp(2, AST_Expr_OperUnaryPost::new).initname("UnaryOperPost")
            ).initname("expr2_unarypost");

            var expr3 = pass();
                expr3.or(
                    struct(AST_Expr_OperUnaryPre::new).iden("++","--", "+","-", "!", "~").and(expr3).initname("UnaryOperPre"),
                    expr2).initname("expr3_unarypre");

            var expr4 = pass().oper_bi_lr(expr3, "*", "/").initname("expr4_mul");
            var expr5 = pass().oper_bi_lr(expr4, "+", "-").initname("expr5_add");
            var expr6 = pass().oper_bi_lr(expr5, "<<", ">>", ">>>").initname("expr6_shift");

            var expr7 = pass().oper_bi_lr(expr6, "<", "<=", ">", ">=", "is").initname("expr7_relation");
            var expr8 = pass().oper_bi_lr(expr7, "==", "!=").initname("expr8_eq");

            var expr9 = pass().oper_bi_lr(expr8, "&").initname("expr9_bitwise_and");  // why this prec not Greater than eqs.?
            var expr10 = pass().oper_bi_lr(expr9, "^").initname("expr10_bitwise_xor");
            var expr11 = pass().oper_bi_lr(expr10, "^").initname("expr11_bitwise_or");

            var expr12 = pass().oper_bi_lr(expr11, "&&").initname("expr12_logical_and");
            var expr13 = pass().oper_bi_lr(expr12, "||").initname("expr13_logical_and");

            var expr14 = pass();
                expr14.and(expr13).op(pass().id("?").and(expr14).id(":").and(expr14).composesp(3, AST_Expr_OperTriCon::new)).initname("expr14_tricon");  // Ternary condition. RL.

            var expr15 = pass();
                expr15.and(expr14).op(pass().iden("=").and(expr15).composesp(3, AST_Expr_OperBi::new)).initname("expr15_assignment");  // RL

            var lambda_params = struct(ASTls::new).repeatjoin(varname, ",").initname("LambdaParams");
            var expr_lambda = struct(AST_Expr_Lambda::new)
                    .id("(").and(lambda_params).id(")")
                    .or(pass().id("=>").and(exprbase), stmt_block).markLookahead().initname("ExprLambda");

            var expr16 = pass().or(
                    expr_lambda,
                    expr15).initname("expr16_lambda");  // may should greater than typecast..

            // define.
            exprbase.and(expr16);
        }


        var func_param = struct(ASTls::new).and(typename).and(varname);
        var func_params = struct(ASTls::new).repeatjoin(func_param, ",");
        var stmt_funcdef = struct(AST_Stmt_DefFunc::new).and(typename).and(varname)
                .id("(").markLookahead().and(func_params).id(")").and(stmt_block).initname("StmtFuncDef");

        var stmt_vardef = struct(AST_Stmt_DefVar::new).and(typename).and(varname).markLookahead()
                .opnull(pass().id("=").and(exprbase)).id(";").initname("StmtVarDef");

        // define.
        stmt_block.id("{").repeat(stmt).id("}").initname("StmtBlock");

        // define.
        stmt.or(
                stmt_block,
                struct(AST_Stmt_If::new).id("if").id("(").and(exprbase).id(")").and(stmt).opnull(pass().id("else").and(stmt)).initname("StmtIf"),
                struct(AST_Stmt_While::new).id("while").id("(").and(exprbase).id(")").and(stmt).initname("StmtWhile"),
                struct(AST_Class::new).id("class").name().opempty(struct(ASTls::new).id(":").name().repeat(pass().id(",").name())).id("{")
                        .and(struct(ASTls::new).repeat(stmt_vardef))
                        .id("}").initname("ClassDeclare"),
                struct(AST_Stmt_FuncReturn::new).id("return").opnull(exprbase).id(";").initname("StmtReturn"),
                stmt_funcdef,
                stmt_vardef,
                pass().and(exprbase).id(";").markLookahead().initname("StmtExpr")
        );





        Scope sc = new Scope(null);

        sc.declare("prt", new GObject((FuncPtr)args1 -> {
            System.out.println(args1[0].value);
            return GObject.VOID;
        }));

        sc.declare("alert", new GObject((FuncPtr)args1 -> {
            JOptionPane.showMessageDialog(null, args1[0].value);
            return GObject.VOID;
        }));


        stmt.readone(lex).eval(sc);

//        System.out.println(stmt.readone(lex));

    }


}