package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Annotation;
import outskirts.lang.langdev.ast.oop.AST_Class_Member;
import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Package;
import outskirts.lang.langdev.ast.srcroot.AST_Stmt_Using;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class LxParser {

    /*
     * =============== PARSER UTILITY ===============
     */

    public static AST_Expr _Parse_OperBin_LR(Lexer lx, Function<Lexer, AST_Expr> fac, String... opers) {
        AST_Expr l = fac.apply(lx);
        String opr;
        while ((opr=lx.peekingone_skp(opers)) != null) {
            AST_Expr r = fac.apply(lx);
            l = new AST_Expr_OperBi(l, opr, r);
        }
        return l;
    }
    public static AST_Expr _Parse_OperBin_RL(Lexer lx, Function<Lexer, AST_Expr> fac, String oper) {
        AST_Expr l = fac.apply(lx);
        if (lx.peeking_skp(oper)) {
            AST_Expr r = _Parse_OperBin_RL(lx, fac, oper);
            return new AST_Expr_OperBi(l, oper, r);
        } else {
            return l;
        }
    }

    /**
     * @param zeromoreUntil nullable. null: zero not allowed.
     */
    public static <T extends AST> List<T> _Parse_RepeatJoin_ZeroMoreUntil(Lexer lx, Function<Lexer, T> fac, String delimiter, String zeromoreUntil) {
        if (zeromoreUntil != null && lx.peeking(zeromoreUntil))
            return Collections.emptyList();
        List<T> ls = new ArrayList<>();
        ls.add(fac.apply(lx));
        while (lx.peeking_skp(delimiter)) {
            ls.add(fac.apply(lx));
        }
        if (zeromoreUntil != null && !lx.peeking(zeromoreUntil))
            throw new IllegalStateException();
        return ls;
    }
    public static <T extends AST> List<T> _Parse_RepeatJoin_OneMore(Lexer lx, Function<Lexer, T> fac, String delimiter) {
        return _Parse_RepeatJoin_ZeroMoreUntil(lx, fac, delimiter, null);
    }

    public static <T> List<T> _Parse_RepeatUntil(Lexer lx, Function<Lexer, T> fac, String until) {
        List<T> ls = new ArrayList<>();
        while (!lx.peeking(until)) {
            ls.add(fac.apply(lx));
        }
        return ls;
    }


    // for expr_funccall, annotation.
    public static List<AST_Expr> _Parse_FuncArgs(Lexer lx) {
        var l = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parseExpr, ",", ")");
        lx.rqnext(")");
        return l;
    }

    public static boolean _IsPass(Lexer lx, Function<Lexer, AST> psr) {
        try {
            psr.apply(lx);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String _ExpandQualifiedName(AST_Expr e) {
        if (e instanceof AST_Expr_OperBi) {
            AST_Expr_OperBi o = (AST_Expr_OperBi)e;
            Validate.isTrue(o.operator.equals("."));
            return _ExpandQualifiedName(o.left) + "." + _ExpandQualifiedName(o.right);
        } else if (e instanceof AST_Expr_PrimaryVariableName) {
            return ((AST_Expr_PrimaryVariableName) e).name;
        } else {
            throw new IllegalStateException();
        }
    }
    public static String _PeakQualifiedName(AST_Expr e, boolean left) {
        if (e instanceof AST_Expr_PrimaryVariableName) {
            return ((AST_Expr_PrimaryVariableName) e).name;
        } else if (e instanceof AST_Expr_OperBi) {
            return ((AST_Expr_PrimaryVariableName)(left ? ((AST_Expr_OperBi)e).left : ((AST_Expr_OperBi)e).right)).name;
        } else {
            throw new IllegalStateException();
        }
    }

    /*
     * =============== TYPENAME ===============
     */

    public static AST_Expr parse_QualifiedName(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExprPrimaryVariableName, ".");
    }

    public static AST_Typename parse_Typename(Lexer lx) {
        AST_Expr nameptr = parse_QualifiedName(lx);

        List<AST_Typename> genericArgs = Collections.emptyList();
        if (lx.peeking_skp("<")) {
            genericArgs = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parse_Typename, ",", ">");
            lx.rqnext(">");
        }
        return new AST_Typename(nameptr, genericArgs);
    }



    /*
     * =============== EXPRESSION ===============
     */

    public static AST_Expr parseExpr(Lexer lx) {
        return parseExpr15Assignment(lx);
    }

    public static AST_Expr_PrimaryLiteralNumber parseExprPrimaryLiteralNumber(Lexer lx) {
        Token t = lx.next();  Validate.isTrue(t.isNumber());
        return new AST_Expr_PrimaryLiteralNumber(t);
    }
    public static AST_Expr_PrimaryLiteralString parseExprPrimaryLiteralString(Lexer lx) {
        Token t = lx.next();  Validate.isTrue(t.isString());
        return new AST_Expr_PrimaryLiteralString(t);
    }
    public static AST_Expr_PrimaryVariableName parseExprPrimaryVariableName(Lexer lx) {
        Token t = lx.next();  Validate.isTrue(t.isName());
        return new AST_Expr_PrimaryVariableName(t);
    }

    public static AST_Expr parseExprPrimary(Lexer lx) {
        Token t = lx.peek();
        if (t.isNumber())
        {
            return parseExprPrimaryLiteralNumber(lx);
        }
        else if (t.isString())
        {
            return parseExprPrimaryLiteralString(lx);
        }
        else if (lx.peeking_skp("new"))
        {   // new Instance.
            AST_Typename type = parse_Typename(lx);
            lx.rqnext("(");
            List<AST_Expr> args = _Parse_FuncArgs(lx);
            return new AST_Expr_OperNew(type, args);
        }
        else if (lx.peeking_skp("("))
        {       // Bracks
            var r = parseExpr(lx);
            lx.rqnext(")");
            return r;
        }
        else if (t.isName()) {
            return parseExprPrimaryVariableName(lx);
        }
        else throw new IllegalStateException("Illegal Primary Expr. at "+t.detailString());
    }

    public static AST_Expr parseExpr1AccessCall(Lexer lx) {
        AST_Expr l = parseExprPrimary(lx);
        while (true) {
            switch (lx.next().text()) {
                case ".": {
                    var r = parseExprPrimaryVariableName(lx);
                    l = new AST_Expr_OperBi(l, ".", r);
                    break;
                }
                case "(": {
                    List<AST_Expr> args = _Parse_FuncArgs(lx);
                    l = new AST_Expr_FuncCall(l, args);
                    break;
                }
                default:
                    lx.back();
                    return l;
            }
        }
    }

    public static AST_Expr parseExpr2UnaryPost(Lexer lx) {
        AST_Expr l = parseExpr1AccessCall(lx);
        String opr;
        while ((opr=lx.peekingone_skp("++", "--")) != null) {
            l = new AST_Expr_OperUnaryPost(l, opr);
        }
        return l;
    }

    public static AST_Expr parseExpr3UnaryPre(Lexer lx) {
        String opr;
        if ((opr=lx.peekingone_skp("++", "--", "+", "-", "!", "~")) != null) {
            AST_Expr r = parseExpr3UnaryPre(lx);
            return new AST_Expr_OperUnaryPre(opr, r);
        } else {
            return parseExpr2UnaryPost(lx);
        }
    }

    public static AST_Expr parseExpr4Multiplecation(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr3UnaryPre, "*", "/");
    }

    public static AST_Expr parseExpr5Addition(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr4Multiplecation, "+", "-");
    }

    public static AST_Expr parseExpr6BitwiseShifts(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr5Addition, "<<", ">>>", ">>");
    }

    public static AST_Expr parseExpr7Relations(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr6BitwiseShifts, "<", "<=", ">", ">=", "is");
    }

    public static AST_Expr parseExpr8Equals(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr7Relations, "==", "!=");
    }

    public static AST_Expr parseExpr9BitwiseAnd(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr8Equals, "&");
    }

    public static AST_Expr parseExpr10BitwiseXor(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr9BitwiseAnd, "^");
    }

    public static AST_Expr parseExpr11BitwiseOr(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr10BitwiseXor, "|");
    }

    public static AST_Expr parseExpr12LogicalAnd(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr11BitwiseOr, "&&");
    }

    public static AST_Expr parseExpr13LogicalOr(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr12LogicalAnd, "||");
    }

    public static AST_Expr parseExpr14TernaryConditional(Lexer lx) {
        AST_Expr l_cond = parseExpr13LogicalOr(lx);
        if (lx.peeking_skp("?")) {
            AST_Expr then = parseExpr14TernaryConditional(lx);
            lx.rqnext(":");
            AST_Expr els = parseExpr14TernaryConditional(lx);
            return new AST_Expr_OperTriCon(l_cond, then, els);
        } else {
            return l_cond;
        }
    }

    public static AST_Expr parseExpr15Assignment(Lexer lx) {
        return _Parse_OperBin_RL(lx, LxParser::parseExpr14TernaryConditional, "=");
    }






    /*
     * =============== STATEMENT ===============
     */

    public static AST_Stmt parseStmt(Lexer lx) {
        switch (lx.peek().text()) {
            case "{":
                return parseStmtBlock(lx);
            case ";":
                lx.skip();
                return AST_Stmt_Blank.INST;
            case "using":
                return parseStmtUsing(lx);
            case "package":
                return parseStmtPackage(lx);
            case "if":
                return parseStmtIf(lx);
            case "while":
                return parseStmtWhile(lx);
            case "return":
                return parseStmtReturn(lx);
            case "class":
                return parseStmtDefClass(lx);
            default:
                int mark = lx.index;

                // is: Typename name
                if (!lx.peeking("new") && _IsPass(lx, LxParser::parse_Typename) && lx.next().isName()) {
                    switch (lx.next().text()) {
                        case "(":
                            lx.index = mark;
                            return parseStmtDefFunc(lx);
                        case "=":
                        case ";":
                            lx.index = mark;
                            return parseStmtDefVar(lx);
                        default:
                            throw new IllegalStateException();
                    }
                }

                lx.index = mark;
                return parseStmtExpr(lx);
        }
    }

    public static AST_Stmt_Block parseStmtBlock(Lexer lx) {
        lx.rqnext("{");
        var s = parseStmtBlockStmts(lx, "}");
        lx.rqnext("}");
        return s;
    }

    public static AST_Stmt_Block parseStmtBlockStmts(Lexer lx, String until) {
        return new AST_Stmt_Block(
                _Parse_RepeatUntil(lx, LxParser::parseStmt, until)
        );
    }

    public static AST_Stmt_If parseStmtIf(Lexer lex) {
        lex.rqnext("if").rqnext("(");
        AST_Expr cond = parseExpr(lex);
        lex.rqnext(")");

        AST_Stmt then = parseStmt(lex);

        AST_Stmt els = null;
        if (lex.peeking_skp("else")) {
            els = parseStmt(lex);
        }

        return new AST_Stmt_If(cond, then, els);
    }

    public static AST_Stmt_While parseStmtWhile(Lexer lx) {
        lx.rqnext("while").rqnext("(");
        AST_Expr cond = parseExpr(lx);
        lx.rqnext(")");

        AST_Stmt then = parseStmt(lx);

        return new AST_Stmt_While(cond, then);
    }

    public static AST_Stmt_Return parseStmtReturn(Lexer lx) {
        lx.rqnext("return");
        AST_Expr expr = null;
        if (!lx.peeking(";")) {
            expr = parseExpr(lx);
        }
        lx.rqnext(";");
        return new AST_Stmt_Return(expr);
    }

    public static AST_Stmt_Using parseStmtUsing(Lexer lx) {
        lx.rqnext("using");

        boolean ustatic = false;
        if (lx.peeking_skp("static")) {
            ustatic = true;
        }

        AST_Expr name = parse_QualifiedName(lx);
        lx.rqnext(";");

        return new AST_Stmt_Using(ustatic, name);
    }
    public static AST_Stmt_Package parseStmtPackage(Lexer lx) {
        lx.rqnext("package");

        AST_Expr name = parse_QualifiedName(lx);
        lx.rqnext(";");

        return new AST_Stmt_Package(name);
    }


    private static AST_Stmt_DefFunc.AST_Func_Param parseStmtDefFunc_FuncParam(Lexer lx) {
        AST_Typename type = parse_Typename(lx);
        String name = lx.next().validate(Token::isName).text();
        return new AST_Stmt_DefFunc.AST_Func_Param(type, name);
    }

    public static AST_Stmt_DefFunc parseStmtDefFunc(Lexer lx) {

        AST_Typename rettype = parse_Typename(lx);
        String name = lx.next().validate(Token::isName).text();

        lx.rqnext("(");
        var params = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parseStmtDefFunc_FuncParam, ",", ")");
        lx.rqnext(")");

        lx.rqnext("{");
        var body = parseStmtBlockStmts(lx, "}");
        lx.rqnext("}");

        return new AST_Stmt_DefFunc(rettype, name, params, body);
    }

    public static AST_Stmt_DefVar parseStmtDefVar(Lexer lx) {

        AST_Typename type = parse_Typename(lx);
        String name = lx.next().validate(Token::isName).text();

        AST_Expr init = null;
        if (lx.peeking_skp("=")) {
            init = parseExpr(lx);
        }

        lx.rqnext(";");

        return new AST_Stmt_DefVar(type, name, init);
    }

    public static AST_Stmt_Expr parseStmtExpr(Lexer lx) {
        AST_Expr expr = parseExpr(lx);
        lx.rqnext(";");
        return new AST_Stmt_Expr(expr);
    }

    public static AST_Annotation parseAnnotation(Lexer lx) {
        lx.rqnext("@");
        AST_Expr name = parse_QualifiedName(lx);

        List<AST_Expr> args = Collections.emptyList();
        if (lx.peeking_skp("(")) {
            args = _Parse_FuncArgs(lx);
        }

        return new AST_Annotation(name, args);
    }

    public static AST_Stmt_DefClass parseStmtDefClass(Lexer lx) {
        lx.rqnext("class");
        String name = lx.next().validate(Token::isName).text();

        List<AST_Expr_PrimaryVariableName> genericParams = Collections.emptyList();
        if (lx.peeking_skp("<")) {
            genericParams = _Parse_RepeatJoin_OneMore(lx, LxParser::parseExprPrimaryVariableName, ",");
            lx.rqnext(">");
        }

        List<AST_Typename> supers = Collections.emptyList();
        if (lx.peeking_skp(":")) {
            supers = _Parse_RepeatJoin_OneMore(lx, LxParser::parse_Typename, ",");
        }

        List<AST_Class_Member> members = new ArrayList<>();
        lx.rqnext("{");
        while (!lx.peeking("}")) {

            List<AST_Annotation> anns = new ArrayList<>();
            while (lx.peeking("@")) {
                anns.add(parseAnnotation(lx));
            }

            List<String> modifiers = new ArrayList<>();
            String modf;
            while ((modf=lx.peekingone_skp("static")) != null) {
                modifiers.add(modf);
            }

            int mark = lx.index;
            AST_Stmt m;
            if (lx.peeking("class")) {
                m = parseStmtDefClass(lx);
            } else if (_IsPass(lx, LxParser::parse_Typename) && _IsPass(lx, LxParser::parseExprPrimaryVariableName)) {
                String s = lx.peek().text();
                lx.index = mark;
                if (s.equals("(")) {
                    m = parseStmtDefFunc(lx);
                } else if (s.equals("=") || s.equals(";")){
                    m = parseStmtDefVar(lx);
                } else {
                    throw new IllegalStateException("Bad stmt");
                }
            } else {
                throw new IllegalStateException("Bad member");
            }

            members.add(new AST_Class_Member(anns, modifiers, m));
        }
        lx.rqnext("}");

        return new AST_Stmt_DefClass(null, name, genericParams, supers, members);
    }
}
