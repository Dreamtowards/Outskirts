package outskirts.lang.langdev.parser.paserconbinator;

public class SyntaX {

//    // headers.
//
//    // TYPENAME
//    static final Parserls typename        = struct(AST_Typename::new)                       .initname("typename");
//    static final Parserls varname         = struct(AST_Expr_PrimaryVariableName::new).name().initname("varname");  // defined.
//
//    static final Parserls qualified_name  = pass()                                        .initname("qualified_name");
//    static final Parserls generic_params  = struct(ASTls::new);
//    static final Parserls generic_args    = struct(ASTls::new);
//
//    // EXPRESSION
//    static final Parserls exprbase        = pass()                       .initname("expression");
//
//    // STATEMENT
//    static final Parserls stmt            = pass()                       .initname("StatementElect");
//    public
//    static final Parserls stmt_block_body = struct(AST_Stmt_Block::new)  .initname("StmtBlockBody");
//    static final Parserls stmt_block      = pass()                       .initname("StmtBlock");
//    static final Parserls stmt_funcdef    = struct(AST_Stmt_DefFunc::new).initname("StmtFuncDef");
//    static final Parserls stmt_vardef     = struct(AST_Stmt_DefVar::new) .initname("StmtVarDef");
//    static final Parserls stmt_if         = struct(AST_Stmt_Strm_If::new).initname("StmtIf");
//    static final Parserls stmt_while      = struct(AST_Stmt_Strm_While::new).initname("StmtWhile");
//    static final Parserls stmt_class      = struct(AST_Stmt_DefClass::new)  .initname("ClassDeclare");
//    static final Parserls stmt_return     = struct(AST_Stmt_FuncReturn::new).initname("StmtReturn");
//    static final Parserls stmt_expr       = struct(AST_Stmt_Expr::new)      .initname("StmtExpr");
//
//    static final Parserls annotation      = struct(AST_Annotation::new)     .initname("annotation");
//
//    static final Parserls func_args       = struct(ASTls::new).repeatjoin(exprbase, ",").initname("func_args");


//    public static void init() {
//
//        init_typename();
//
//        init_expression();
//
//        init_statement();
//
//        init_etx();
//
//    }

//    private static void init_etx() {
//
//        annotation.id("@").and(typename).opnull(pass().id("(").and(func_args).id(")"));
//
//
//    }

//    private static void init_typename() {
//
//        qualified_name.oper_bi_lr(varname, ".");
//
//
//        generic_params.id("<").repeatjoin(pass().name(), ",", true).id(">");
//        generic_args.id("<").repeatjoin(typename, ",").id(">");
//
//        typename.and(qualified_name).opnull(generic_args);  // really.? seems syntax right, but while do semantic,
//
//    }

//    private static void init_statement() {
//
//        stmt_block_body.repeat(stmt);
//        stmt_block.id("{").and(stmt_block_body).id("}");
//
//        var stmt_blank = struct(AST_Stmt_Blank::new).id(";");
//
//
//        var func_param = struct(ASTls::new).and(typename).and(varname);
//        var func_params = struct(ASTls::new).repeatjoin(func_param, ",");
//        stmt_funcdef.and(typename).name()
//                .id("(").and(func_params).id(")").id("{").markLookahead().and(stmt_block_body).id("}");
//
//        stmt_vardef.and(typename).name()
//                .opnull(pass().id("=").and(exprbase)).id(";").lookaheadIndex(3);
//
//
//        stmt_if.id("if").id("(").and(exprbase).id(")").and(stmt).opnull(pass().id("else").and(stmt));
//
//        stmt_while.id("while").id("(").and(exprbase).id(")").and(stmt);
//
//        stmt_class.id("class")
//                .name().opnull(generic_params)  // there not typename actually.
//                .opnull(struct(ASTls::new).id(":").repeatjoin(typename, ",", true))
//                .id("{")
//                .and(struct(ASTls::new).repeat(struct(AST_Class_Member::new).opnull(struct(ASTls::new).repeat(annotation)).or(stmt_class, stmt_funcdef, stmt_vardef).markLookahead()))
//                .id("}");
//
//        stmt_return.id("return").opnull(exprbase).id(";");
//
//        stmt_expr.and(exprbase).id(";").markLookahead();
//
//        // FILE ROOT
//        var stmt_r_using = struct(AST_SR_Stmt_Using::new).id("using").and(qualified_name).id(";");
//        var stmt_r_package = struct(AST_SR_Stmt_Package::new).id("package").and(qualified_name).id(";");
//
//        // define.
//        stmt.or(
//                stmt_block,
//                stmt_blank,
//                stmt_r_using,
//                stmt_r_package,
//                stmt_if,
//                stmt_while,
//                stmt_class,
//                stmt_return,
//                stmt_funcdef,
//                stmt_vardef,
//                stmt_expr
//        );
//
//    }

//    private static void init_expression() {
//        // EXPRESSION.
//
//        var primary = pass().or(
//                struct(AST_Expr_PrimaryLiteralNumber::new).number().initname("NumberLiteral"),
//                struct(AST_Expr_PrimaryLiteralString::new).string().initname("StringLiteral"),
//                varname
//        ).initname("Primary");
//
//        var expr0 = pass().or(
//                pass().id("(").and(exprbase).id(")").initname("expr_parentheses"),
//                primary);
//
//        var expr1 = pass().and(expr0).repeat(pass().or(
//                pass().iden(".").and(varname).composesp(3, AST_Expr_OperBi::new).initname("MemberAccess"),
//                pass().id("(").and(func_args).id(")").composesp(2, AST_Expr_FuncCall::new).initname("FuncCall")
//        )).initname("expr1");
//
//        var expr2 = pass().and(expr1).repeat(
//                pass().iden("++", "--").composesp(2, AST_Expr_OperUnaryPost::new)
//        ).initname("expr2_oper_upost");
//
//        var expr3 = pass();  // new Obj.InnObj.ActualObj().setupBuilder().color(c).text(t);
//        expr3.or(
//                struct(AST_Expr_OperUnaryPre::new).iden("++","--", "+","-", "!", "~").and(expr3).initname("expr3_oper_upre"),
//                struct(AST_Expr_OperNew::new).id("new").and(typename).id("(").and(func_args).id(")").initname("expr_oper_new"),
//                expr2);
//
//        var _rsh2 = pass().iden_connected(">",">");
//        var _rsh3 = pass().iden_connected(">",">",">");
//
//        var expr4 = pass().oper_bi_lr(expr3, "*", "/").initname("expr4_mul");
//        var expr5 = pass().oper_bi_lr(expr4, "+", "-").initname("expr5_add");
//        var expr6 = pass().oper_bi_lr(expr5, "<<", _rsh3, _rsh2).initname("expr6_shift");
//
//        var expr7 = pass().oper_bi_lr(expr6, "<", "<=", ">", ">=", "is").initname("expr7_relation");
//        var expr8 = pass().oper_bi_lr(expr7, "==", "!=").initname("expr8_eq");
//
//        var expr9 = pass().oper_bi_lr(expr8, "&").initname("expr9_bitwise_and");  // why this prec not Greater than eqs.?
//        var expr10 = pass().oper_bi_lr(expr9, "^").initname("expr10_bitwise_xor");
//        var expr11 = pass().oper_bi_lr(expr10, "|").initname("expr11_bitwise_or");
//
//        var expr12 = pass().oper_bi_lr(expr11, "&&").initname("expr12_logical_and");
//        var expr13 = pass().oper_bi_lr(expr12, "||").initname("expr13_logical_or");
//
//        var lambda_params = struct(ASTls::new).repeatjoin(varname, ",").initname("lambda_params");
//        var expr_lambda = struct(AST_Expr_Lambda::new)
//                .id("(").and(lambda_params).id(")")
//                .or(pass().id("=>").and(exprbase), stmt_block).markLookahead().initname("expr_lambda");
//
//        var expr14 = pass().or(expr_lambda, expr13);
//
//        var expr15 = pass();
//        expr15.and(expr14).op(pass().id("?").and(expr15).id(":").and(expr15).composesp(3, AST_Expr_OperTriCon::new)).initname("expr14_tricon");  // Ternary condition. RL.
//
//        var expr16 = pass();
//        expr16.and(expr15).op(pass().iden("=").and(expr16).composesp(3, AST_Expr_OperBi::new)).initname("expr15_assignment");  // RL
//
//        // define.
//        exprbase.and(expr16);
//
//    }



}
