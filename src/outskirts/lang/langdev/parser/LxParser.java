package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.AST__Annotation;
import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.AST__Typename;
import outskirts.lang.langdev.ast.AST_Stmt_Using;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class LxParser {

    public static String[] _MODIFIERS = {"static", "const"};


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
        lx.match(")");
        return l;
    }

    public static boolean _Is_Peeking_Modifiers(Lexer lx) {
        return lx.peeking("@") || lx.peekingone(_MODIFIERS);
    }
    public static AST__Modifiers _Parse_Modifiers(Lexer lx) {
        List<AST__Annotation> annos = new ArrayList<>();
        while (lx.peeking("@")) {
            annos.add(parseAnnotation(lx));
        }

        List<String> modifiers = new ArrayList<>();
        String kwm;
        while (lx.peek().isKeyword() && (kwm=lx.peekingone_skp(_MODIFIERS)) != null) {
            Validate.isTrue(!modifiers.contains(kwm), "modifier '"+kwm+"' duplicated.");
            modifiers.add(kwm);
        }

        return new AST__Modifiers(annos, modifiers);
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
        } else if (e instanceof AST_Expr_PrimaryIdentifier) {
            return ((AST_Expr_PrimaryIdentifier) e).name;
        } else {
            throw new IllegalStateException();
        }
    }
//    public static String _PeakQualifiedName(AST_Expr e, boolean left) {
//        if (e instanceof AST_Expr_PrimaryIdentifier) {
//            return ((AST_Expr_PrimaryIdentifier) e).name;
//        } else if (e instanceof AST_Expr_OperBi) {
//            return ((AST_Expr_PrimaryIdentifier)(left ? ((AST_Expr_OperBi)e).left : ((AST_Expr_OperBi)e).right)).name;
//        } else {
//            throw new IllegalStateException();
//        }
//    }

    /*
     * =============== TYPENAME ===============
     */

    public static AST_Expr parse_QualifiedName(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExprPrimaryVariableName, ".");
    }

    public static AST__Typename parse_Typename(Lexer lx) {
        AST_Expr nameptr = parse_QualifiedName(lx);

        List<AST__Typename> genericArgs = Collections.emptyList();
        if (lx.peeking_skp("<")) {
            genericArgs = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parse_Typename, ",", ">");
            lx.match(">");
        }
        return new AST__Typename(nameptr, genericArgs);
    }



    /*
     * =============== EXPRESSION ===============
     */

    public static AST_Expr parseExpr(Lexer lx) {
        return parseExpr15Assignment(lx);
    }

    public static AST_Expr_PrimaryIdentifier parseExprPrimaryVariableName(Lexer lx) {
        Token t = lx.next();
        Validate.isTrue(t.isName());
        return new AST_Expr_PrimaryIdentifier(t);
    }

    public static AST_Expr parseExprPrimary(Lexer lx) {
        Token t = lx.peek();
        if (t.isInt()) {
            lx.skip();
            return new AST_Expr_PrimaryLiteralInt(Integer.parseInt(t.text()));
        } else if (t.isFloat()) {
            lx.skip();
            return new AST_Expr_PrimaryLiteralFloat(Float.parseFloat(t.text()));
        } else if (t.isChar()) {
            lx.skip();
            String ct = t.text(); Validate.isTrue(ct.length() == 1);
            return new AST_Expr_PrimaryLiteralChar(ct.charAt(0));
        } else if (t.isString()) {
            lx.skip();
            return new AST_Expr_PrimaryLiteralString(t);
        }
        else if (t.isName())
        {
            return parseExprPrimaryVariableName(lx);
        }
        else if (lx.peeking_skp("("))   // Bracks
        {
            var r = parseExpr(lx);
            lx.match(")");
            return r;
        }
        else if (t.isKeyword() && lx.peeking_skp("new"))  // new Instance Expr.
        {
            AST__Typename type = parse_Typename(lx);
            lx.match("(");
            List<AST_Expr> args = _Parse_FuncArgs(lx);
            return new AST_Expr_OperNew(type, args);
        }
        else if (t.isKeyword() && lx.peeking_skp("sizeof"))
        {
            lx.match("(");
            AST__Typename typename = parse_Typename(lx);
            lx.match(")");
            return new AST_Expr_OperSizeOf(typename);
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
            lx.match(":");
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
            case "namespace":
                return parseStmtNamespace(lx);
            case "if":
                return parseStmtIf(lx);
            case "while":
                return parseStmtWhile(lx);
            case "return":
                return parseStmtReturn(lx);
            default:
                AST__Modifiers modifiers = AST__Modifiers.DEFAULT;
                if (_Is_Peeking_Modifiers(lx)) {
                    modifiers = _Parse_Modifiers(lx);
                }

                int mark = lx.index;

                if (lx.peeking("class"))
                {
                    AST_Stmt_DefClass a = parseStmtDefClass(lx); a.modifiers = modifiers; return a;
                }
                else if (_IsPass(lx, LxParser::parse_Typename) && _IsPass(lx, LxParser::parseExprPrimaryVariableName))  // is: Typename id
                {
                    String s = lx.next().text();
                    lx.index = mark;  // setback.
                    if (s.equals("("))
                    {
                        AST_Stmt_DefFunc a = parseStmtDefFunc(lx); a.modifiers = modifiers; return a;
                    }
                    else if (s.equals("=") || s.equals(";"))
                    {
                        AST_Stmt_DefVar a = parseStmtDefVar(lx); a.modifiers = modifiers; return a;
                    }
                    else throw new IllegalStateException();
                }
                else
                {
                    Validate.isTrue(modifiers == AST__Modifiers.DEFAULT, "illegal modifiers exists.");

                    lx.index = mark;  // setback.
                    return parseStmtExpr(lx);
                }
        }
    }

    public static AST_Stmt_Block parseStmtBlock(Lexer lx) {
        lx.match("{");
        var s = new AST_Stmt_Block(parseStmtBlockStmts(lx, "}"));
        lx.match("}");
        return s;
    }

    public static List<AST_Stmt> parseStmtBlockStmts(Lexer lx, String until) {
        return _Parse_RepeatUntil(lx, LxParser::parseStmt, until);
    }

    public static AST_Stmt_If parseStmtIf(Lexer lex) {
        lex.match("if").match("(");
        AST_Expr cond = parseExpr(lex);
        lex.match(")");

        AST_Stmt then = parseStmt(lex);

        AST_Stmt els = null;
        if (lex.peeking_skp("else")) {
            els = parseStmt(lex);
        }

        return new AST_Stmt_If(cond, then, els);
    }

    public static AST_Stmt_While parseStmtWhile(Lexer lx) {
        lx.match("while").match("(");
        AST_Expr cond = parseExpr(lx);
        lx.match(")");

        AST_Stmt then = parseStmt(lx);

        return new AST_Stmt_While(cond, then);
    }

    public static AST_Stmt_Return parseStmtReturn(Lexer lx) {
        lx.match("return");
        AST_Expr expr = null;
        if (!lx.peeking(";")) {
            expr = parseExpr(lx);
        }
        lx.match(";");
        return new AST_Stmt_Return(expr);
    }

    public static AST_Stmt_Using parseStmtUsing(Lexer lx) {
        Validate.isTrue(lx.peek().isKeyword());
        lx.match("using");

        boolean ustatic = false;
        if (lx.peeking_skp("static")) {
            ustatic = true;
        }

        AST_Expr used = parse_QualifiedName(lx);

        String asname = (used instanceof AST_Expr_OperBi ? ((AST_Expr_OperBi)used).right : used).varname();
        if (lx.peeking_skp("as")) {
            asname = parseExprPrimaryVariableName(lx).name;
        }
        lx.match(";");

        return new AST_Stmt_Using(ustatic, used, asname);
    }

    public static AST_Stmt_Namespace parseStmtNamespace(Lexer lx) {
        Validate.isTrue(lx.peek().isKeyword());
        lx.match("namespace");

        AST_Expr name = parse_QualifiedName(lx);

        List<AST_Stmt> stmts = new ArrayList<>();
        if (lx.peeking_skp(";")) {   // the single scope namespace. until next namespace(same-level) or EOF.
            while (!lx.peekingone("namespace", Token.EOF_T))
            {
                stmts.add(parseStmt(lx));
            }
        }
        else
        {
            lx.match("{");
            stmts = parseStmtBlockStmts(lx, "}");
            lx.match("}");
        }

        return new AST_Stmt_Namespace(name, stmts);
    }


    public static AST_Stmt_DefFunc parseStmtDefFunc(Lexer lx) {

        AST__Typename rettype = parse_Typename(lx);
        String name = lx.next().validate(Token::isName).text();

        lx.match("(");
        var params = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parseStmtDefVar_Def, ",", ")");
        lx.match(")");

        var body = parseStmtBlock(lx);

        return new AST_Stmt_DefFunc(rettype, name, params, body);
    }

    public static AST_Stmt_DefVar parseStmtDefVar(Lexer lx) {
        AST_Stmt_DefVar a = parseStmtDefVar_Def(lx);
        lx.match(";");
        return a;
    }

    // not the ';'. for support of FuncParams.
    public static AST_Stmt_DefVar parseStmtDefVar_Def(Lexer lx) {
        AST__Typename type = parse_Typename(lx);
        String name = lx.next().validate(Token::isName).text();

        AST_Expr init = null;
        if (lx.peeking_skp("=")) {
            init = parseExpr(lx);
        }

        return new AST_Stmt_DefVar(type, name, init);
    }

    public static AST_Stmt_Expr parseStmtExpr(Lexer lx) {
        AST_Expr expr = parseExpr(lx);
        lx.match(";");
        return new AST_Stmt_Expr(expr);
    }

    public static AST__Annotation parseAnnotation(Lexer lx) {
        lx.match("@");
        AST_Expr name = parse_QualifiedName(lx);

        List<AST_Expr> args = Collections.emptyList();
        if (lx.peeking_skp("(")) {
            args = _Parse_FuncArgs(lx);
        }

        return new AST__Annotation(name, args);
    }

    public static AST_Stmt_DefClass parseStmtDefClass(Lexer lx) {
        Validate.isTrue(lx.peek().isKeyword());
        lx.match("class");
        String name = lx.next().validate(Token::isName).text();

        List<AST_Expr_PrimaryIdentifier> genericParams = Collections.emptyList();
        if (lx.peeking_skp("<")) {
            genericParams = _Parse_RepeatJoin_OneMore(lx, LxParser::parseExprPrimaryVariableName, ",");
            lx.match(">");
        }

        List<AST__Typename> supers = Collections.emptyList();
        if (lx.peeking_skp(":")) {
            supers = _Parse_RepeatJoin_OneMore(lx, LxParser::parse_Typename, ",");
        }

        lx.match("{");
        List<AST_Stmt> stmts_members = parseStmtBlockStmts(lx, "}");
        lx.match("}");

        // validate member type.
        stmts_members.forEach(e -> Validate.isTrue(e instanceof AST_Stmt_DefClass || e instanceof  AST_Stmt_DefVar || e instanceof AST_Stmt_DefFunc, "Illegal class member."));

        return new AST_Stmt_DefClass(name, genericParams, supers, stmts_members);
    }
}
