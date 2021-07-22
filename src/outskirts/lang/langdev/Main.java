package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.ex.FuncPtr;
import outskirts.lang.langdev.ast.oop.AST_Annotation;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.interpreter.ASTEvaluator;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.interpreter.astprint.ASTPrinter;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.parser.Parserls;
import outskirts.util.IOUtils;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;

import static outskirts.lang.langdev.parser.Parserls.pass;
import static outskirts.lang.langdev.parser.Parserls.struct;

public class Main {


    static final Parserls typename     = struct(AST_Typename::new)                       .initname("typename");
    static final Parserls varname      = struct(AST_Expr_PrimaryVariableName::new).name().initname("varname");  // defined.

    // headers.
    static final Parserls exprbase     = pass()                       .initname("expression");

    static final Parserls stmt         = pass()                       .initname("StatementElect");
    static final Parserls stmt_block   = struct(AST_Stmt_Block::new)  .initname("StmtBlock");
    static final Parserls stmt_funcdef = struct(AST_Stmt_DefFunc::new).initname("StmtFuncDef");
    static final Parserls stmt_vardef  = struct(AST_Stmt_DefVar::new) .initname("StmtVarDef");
    static final Parserls stmt_if      = struct(AST_Stmt_Strm_If::new).initname("StmtIf");
    static final Parserls stmt_while   = struct(AST_Stmt_Strm_While::new).initname("StmtWhile");
    static final Parserls stmt_class   = struct(AST_Stmt_DefClass::new)  .initname("ClassDeclare");
    static final Parserls stmt_return  = struct(AST_Stmt_FuncReturn::new).initname("StmtReturn");
    static final Parserls stmt_expr    = struct(AST_Stmt_Expr::new)      .initname("StmtExpr");

    static final Parserls annotation   = struct(AST_Annotation::new)     .initname("annotation");


    public static void main(String[] args) throws IOException {

        String s = IOUtils.toString(new FileInputStream("main.g"));

        Lexer lex = new Lexer();
        lex.read(s);

//        System.out.println(lex.tokens());
//        System.exit(0);

        {
            var iden = pass().name();

            var package_name = pass().oper_bi_lr(iden, ".");
            var class_name = pass().op(pass().and(package_name).id(".")).oper_bi_lr(iden, ".");

            // array<pair<foo, ele>> a = addr(5 >> 3)
            var generic_params = struct(ASTls::new).id("<").repeatjoin(typename, ",").id(">");

            typename.name().opnull(generic_params);
        }


        {   // EXPRESSION.

            var func_args = struct(ASTls::new).repeatjoin(exprbase, ",").initname("func_args");

            var primary = pass().or(
                    struct(AST_Expr_PrimaryLiteralNumber::new).number().initname("NumberLiteral"),
                    struct(AST_Expr_PrimaryLiteralString::new).string().initname("StringLiteral"),
                    varname
            ).initname("Primary");

            var expr0 = pass().or(
                    pass().id("(").and(exprbase).id(")").initname("expr_parentheses"),
                    primary);

            var expr0_1 = pass().or(
                    struct(AST_Expr_OperNew::new).id("new").and(typename).id("(").and(func_args).id(")").initname("expr_oper_new"),
                    expr0);

            var expr1 = pass().and(expr0_1).repeat(pass().or(
                    pass().iden(".").and(varname).composesp(3, AST_Expr_OperBi::new).initname("MemberAccess"),
                    pass().id("(").and(func_args).id(")").composesp(2, AST_Expr_FuncCall::new).initname("FuncCall")
            )).initname("expr1");

            var expr2 = pass().and(expr1).repeat(
                    pass().iden("++", "--").composesp(2, AST_Expr_OperUnaryPost::new)
            ).initname("expr2_oper_upost");

            var expr3 = pass();
                expr3.or(
                    struct(AST_Expr_OperUnaryPre::new).iden("++","--", "+","-", "!", "~").and(expr3).initname("expr3_oper_upre"),
                    expr2);

            var _rsh2 = pass().iden_connected(">",">");
            var _rsh3 = pass().iden_connected(">",">",">");

            var expr4 = pass().oper_bi_lr(expr3, "*", "/").initname("expr4_mul");
            var expr5 = pass().oper_bi_lr(expr4, "+", "-").initname("expr5_add");
            var expr6 = pass().oper_bi_lr(expr5, "<<", _rsh3, _rsh2).initname("expr6_shift");

            var expr7 = pass().oper_bi_lr(expr6, "<", "<=", ">", ">=", "is").initname("expr7_relation");
            var expr8 = pass().oper_bi_lr(expr7, "==", "!=").initname("expr8_eq");

            var expr9 = pass().oper_bi_lr(expr8, "&").initname("expr9_bitwise_and");  // why this prec not Greater than eqs.?
            var expr10 = pass().oper_bi_lr(expr9, "^").initname("expr10_bitwise_xor");
            var expr11 = pass().oper_bi_lr(expr10, "|").initname("expr11_bitwise_or");

            var expr12 = pass().oper_bi_lr(expr11, "&&").initname("expr12_logical_and");
            var expr13 = pass().oper_bi_lr(expr12, "||").initname("expr13_logical_and");

            var lambda_params = struct(ASTls::new).repeatjoin(varname, ",").initname("lambda_params");
            var expr_lambda = struct(AST_Expr_Lambda::new)
                    .id("(").and(lambda_params).id(")")
                    .or(pass().id("=>").and(exprbase), stmt_block).markLookahead().initname("expr_lambda");

            var expr14 = pass().or(expr_lambda, expr13);

            var expr15 = pass();
                expr15.and(expr14).op(pass().id("?").and(expr15).id(":").and(expr15).composesp(3, AST_Expr_OperTriCon::new)).initname("expr14_tricon");  // Ternary condition. RL.

            var expr16 = pass();
                expr16.and(expr15).op(pass().iden("=").and(expr16).composesp(3, AST_Expr_OperBi::new)).initname("expr15_assignment");  // RL

            // define.
            exprbase.and(expr16);
        }


        var func_param = struct(ASTls::new).and(typename).and(varname);
        var func_params = struct(ASTls::new).repeatjoin(func_param, ",");
        stmt_funcdef.and(typename).name()
                .id("(").markLookahead().and(func_params).id(")").and(stmt_block);

        stmt_vardef.and(typename).name().markLookahead()
                .opnull(pass().id("=").and(exprbase)).id(";");

        stmt_block.id("{").repeat(stmt).id("}").initname("StmtBlock");

        stmt_if.id("if").id("(").and(exprbase).id(")").and(stmt).opnull(pass().id("else").and(stmt));

        stmt_while.id("while").id("(").and(exprbase).id(")").and(stmt);

        stmt_class.id("class")
                .and(typename)  // there not typename actually.
                .opnull(struct(ASTls::new).id(":").repeatjoin(typename, ",", true))
                .id("{")
                    .and(struct(ASTls::new).repeat(struct(AST_Class_Member::new).opnull(struct(ASTls::new).repeat(annotation)).or(stmt_class, stmt_funcdef, stmt_vardef).markLookahead()))
                .id("}");

        stmt_return.id("return").opnull(exprbase).id(";");

        stmt_expr.and(exprbase).id(";").markLookahead();

        // define.
        stmt.or(
                stmt_block,
                stmt_if,
                stmt_while,
                stmt_class,
                stmt_return,
                stmt_expr,
                stmt_funcdef,
                stmt_vardef
        );


        annotation.id("@").and(typename);




        Scope sc = new Scope(null);

        sc.declare("prt", new GObject((FuncPtr) args1 -> {
            System.out.println(args1[0].value);
            return GObject.VOID;
        }));

        sc.declare("alert", new GObject((FuncPtr)args1 -> {
            JOptionPane.showMessageDialog(null, args1[0].value);
            return GObject.VOID;
        }));


        AST ast = stmt.readone(lex);
//        System.out.println(ast);


//        ASTPrinter prtr = new ASTPrinter();
//        prtr.printExpr((AST_Expr)ast, 0);
//        System.out.println(prtr.buf.toString());


        ASTEvaluator evlr = new ASTEvaluator();
        evlr.evalStmt((AST_Stmt)ast, sc);

    }


}