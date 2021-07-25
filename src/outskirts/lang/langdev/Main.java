package outskirts.lang.langdev;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.ex.FuncPtr;
import outskirts.lang.langdev.ast.oop.AST_Annotation;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.ast.srcroot.AST_SR_StmtLs;
import outskirts.lang.langdev.ast.srcroot.AST_SR_Stmt_Package;
import outskirts.lang.langdev.ast.srcroot.AST_SR_Stmt_Using;
import outskirts.lang.langdev.interpreter.ASTEvaluator;
import outskirts.lang.langdev.interpreter.GObject;
import outskirts.lang.langdev.interpreter.Scope;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.parser.Parserls;
import outskirts.util.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

import static outskirts.lang.langdev.parser.Parserls.pass;
import static outskirts.lang.langdev.parser.Parserls.struct;

public class Main {

    // headers.

    // TYPENAME
    static final Parserls typename        = struct(AST_Typename::new)                       .initname("typename");
    static final Parserls varname         = struct(AST_Expr_PrimaryVariableName::new).name().initname("varname");  // defined.

    static final Parserls qualified_name  = pass()                                        .initname("qualified_name");
    static final Parserls generic_params  = struct(ASTls::new);
    static final Parserls generic_args    = struct(ASTls::new);

    // FILE ROOT
    static final Parserls r_using         = struct(AST_SR_Stmt_Using::new);
    static final Parserls r_package       = struct(AST_SR_Stmt_Package::new);

    // EXPRESSION
    static final Parserls exprbase        = pass()                       .initname("expression");

    // STATEMENT
    static final Parserls stmt            = pass()                       .initname("StatementElect");
    static final Parserls stmt_block_body = struct(AST_Stmt_Block::new)  .initname("StmtBlockBody");
    static final Parserls stmt_block      = pass()                       .initname("StmtBlock");
    static final Parserls stmt_funcdef    = struct(AST_Stmt_DefFunc::new).initname("StmtFuncDef");
    static final Parserls stmt_vardef     = struct(AST_Stmt_DefVar::new) .initname("StmtVarDef");
    static final Parserls stmt_if         = struct(AST_Stmt_Strm_If::new).initname("StmtIf");
    static final Parserls stmt_while      = struct(AST_Stmt_Strm_While::new).initname("StmtWhile");
    static final Parserls stmt_class      = struct(AST_Stmt_DefClass::new)  .initname("ClassDeclare");
    static final Parserls stmt_return     = struct(AST_Stmt_FuncReturn::new).initname("StmtReturn");
    static final Parserls stmt_expr       = struct(AST_Stmt_Expr::new)      .initname("StmtExpr");

    static final Parserls annotation      = struct(AST_Annotation::new)     .initname("annotation");

    static final Parserls srcroot         = struct(AST_SR_StmtLs::new);


    private static final String[] SourceBasePaths = {"src/outskirts/lang/stdlib/"};
    private static final Scope RootScope = new Scope(null);

    public static void main(String[] args) throws IOException {

        init_typename();

        init_expression();

        init_statement();

        init_etx();


        init_rootscope();

        exec(readSrcByPath("main.g"));
    }

    private static File locateSourceFile(String spath, boolean fullcheck) {
        File dest = null;
        for (String basepath : SourceBasePaths) {
            File f = new File(basepath, spath);
            if (f.exists()) {
                if (fullcheck && dest != null)
                    throw new IllegalStateException("Found Duplicated File in path "+spath);
                dest = f;
                if (!fullcheck)
                    return f;
            }
        }
        return dest;
    }

    private static String readSrcByPath(String path) {
        try {
            return IOUtils.toString(new FileInputStream(locateSourceFile(path, true)));
        } catch (IOException ex) {
            throw new RuntimeException("Failed read file", ex);
        }
    }

    // Exec Code, return the Scope, or a Variable if the scope have only one variable.
    private static GObject imports(String spath) {
        String s = readSrcByPath(spath);
        Scope sc = exec(s);

        if (sc.variables.size() == 1) {
            return sc.variables.get(sc.variables.keySet().iterator().next());
        } else {
            return new GObject(sc);
        }
    }

    private static Scope exec(String code) {  // return a Scope or a SingleObject .?(when only one object been defined on that scope.)
//        System.out.println("ExecCode: "+code);
        Lexer lex = new Lexer();
        lex.read(code);

        Scope sc = new Scope(RootScope);

        AST ast = srcroot.readone(lex);
//        System.out.println("### Read Program: "+ast);

        if (!lex.eof())
            throw new IllegalStateException("Failed Read File Fully. syntax err. at '"+lex.peek()+"'");

//        ASTPrinter prtr = new ASTPrinter();
//        prtr.printExpr((AST_Expr)ast, 0);
//        System.out.println(prtr.buf.toString());


        ASTEvaluator evlr = new ASTEvaluator();
        evlr.eval(ast, sc);

        return sc;
    }

    private static void init_etx() {

        annotation.id("@").and(typename);

        r_using.id("using").and(qualified_name).id(";");
        r_package.id("package").and(qualified_name).id(";");

        srcroot.repeat(pass().or(
                r_package,
                r_using,
                stmt
        ));
    }

    private static void init_typename() {

        qualified_name.oper_bi_lr(varname, ".");


        generic_params.id("<").repeatjoin(pass().name(), ",", true).id(">");
        generic_args.id("<").repeatjoin(typename, ",").id(">");

        typename.and(qualified_name).opnull(generic_args);  // really.? seems syntax right, but while do semantic,

    }

    private static void init_statement() {

        var func_param = struct(ASTls::new).and(typename).and(varname);
        var func_params = struct(ASTls::new).repeatjoin(func_param, ",");
        stmt_funcdef.and(typename).name()
                .id("(").and(func_params).id(")").id("{").markLookahead().and(stmt_block_body).id("}");

        stmt_vardef.and(typename).name()
                .opnull(pass().id("=").and(exprbase)).id(";").markLookahead();

        stmt_block_body.repeat(stmt);
        stmt_block.id("{").and(stmt_block_body).id("}");

        stmt_if.id("if").id("(").and(exprbase).id(")").and(stmt).opnull(pass().id("else").and(stmt));

        stmt_while.id("while").id("(").and(exprbase).id(")").and(stmt);

        stmt_class.id("class")
                .name().opnull(generic_params)  // there not typename actually.
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
                stmt_funcdef,
                stmt_vardef,
                stmt_expr
        );

    }

    private static void init_expression() {
        // EXPRESSION.

        var func_args = struct(ASTls::new).repeatjoin(exprbase, ",").initname("func_args");

        var primary = pass().or(
                struct(AST_Expr_PrimaryLiteralNumber::new).number().initname("NumberLiteral"),
                struct(AST_Expr_PrimaryLiteralString::new).string().initname("StringLiteral"),
                varname
        ).initname("Primary");

        var expr0 = pass().or(
                pass().id("(").and(exprbase).id(")").initname("expr_parentheses"),
                primary);

        var expr1 = pass().and(expr0).repeat(pass().or(
                pass().iden(".").and(varname).composesp(3, AST_Expr_OperBi::new).initname("MemberAccess"),
                pass().id("(").and(func_args).id(")").composesp(2, AST_Expr_FuncCall::new).initname("FuncCall")
        )).initname("expr1");

        var expr2 = pass().and(expr1).repeat(
                pass().iden("++", "--").composesp(2, AST_Expr_OperUnaryPost::new)
        ).initname("expr2_oper_upost");

        var expr3 = pass();  // new Obj.InnObj.ActualObj().setupBuilder().color(c).text(t);
        expr3.or(
                struct(AST_Expr_OperUnaryPre::new).iden("++","--", "+","-", "!", "~").and(expr3).initname("expr3_oper_upre"),
                struct(AST_Expr_OperNew::new).id("new").and(typename).id("(").and(func_args).id(")").initname("expr_oper_new"),
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

    private static void init_rootscope() {
        Scope sc = RootScope;

        sc.declare("s_print", new GObject((FuncPtr) args1 -> {
            System.out.println(args1[0].value);
            return GObject.VOID;
        }));

        sc.declare("s_alert", new GObject((FuncPtr)args1 -> {
            JOptionPane.showMessageDialog(null, args1[0].value);
            return GObject.VOID;
        }));



        sc.declare("s_readfile", new GObject((FuncPtr) args1 -> {
            return new GObject(readSrcByPath((String)args1[0].value));
        }));

        sc.declare("s_exec", new GObject((FuncPtr) args1 -> {
            return new GObject(exec((String)args1[0].value));
        }));

        sc.declare("s_import", new GObject((FuncPtr) args1 -> {
            return imports((String)args1[0].value);
        }));

    }


}