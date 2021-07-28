package outskirts.lang.langdev.parser.spp;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.oop.AST_Typename;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SpParser {

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

    public static <T> List<T> _Parse_RepeatJoin_ZeroMoreUntil(Lexer lx, Function<Lexer, T> fac, String delimiter, String zeromoreUntil) {
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
    public static List<AST> _Parse_RepeatJoin_OneMore(Lexer lx, Function<Lexer, AST> fac, String delimiter) {
        return _Parse_RepeatJoin_ZeroMoreUntil(lx, fac, delimiter, null);
    }

    public static <T> List<T> _Parse_RepeatUntil(Lexer lx, Function<Lexer, T> fac, String until) {
        List<T> ls = new ArrayList<>();
        while (!lx.peeking(until)) {
            ls.add(fac.apply(lx));
        }
        return ls;
    }

    /*
     * =============== TYPENAME ===============
     */

    public static AST_Expr parse_QualifiedName(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExprPrimaryVariableName, ".");
    }

    public static AST_Typename parse_Typename(Lexer lx) {
        AST_Expr nameptr = parse_QualifiedName(lx);

        List<AST_Typename> genericArgs = Collections.emptyList();
        if (lx.peeking_skp("<")) {
            genericArgs = _Parse_RepeatJoin_ZeroMoreUntil(lx, SpParser::parse_Typename, ",", ">");
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
        if (t.isNumber()) return parseExprPrimaryLiteralNumber(lx);
        else if (t.isString()) return parseExprPrimaryLiteralString(lx);
        else if (t.isName()) return parseExprPrimaryVariableName(lx);
        else throw new IllegalStateException("Illegal Primary Expr. at "+t.detailString());
    }

    public static AST_Expr parseExpr0Parenthese(Lexer lx) {
        if (lx.peeking("(")) {
            var r = parseExpr(lx);
            lx.rqnext(")");
            return r;
        } else {
            return parseExprPrimary(lx);
        }
    }

    public static AST_Expr parseExpr1AccessCall(Lexer lx) {
        AST_Expr l = parseExpr0Parenthese(lx);
        while (true) {
            switch (lx.next().text()) {
                case ".": {
                    var r = parseExprPrimaryVariableName(lx);
                    l = new AST_Expr_OperBi(l, ".", r);
                    break;
                }
                case "(": {
                    List<AST_Expr> args = _Parse_RepeatJoin_ZeroMoreUntil(lx, SpParser::parseExpr, ",", ")");
                    lx.rqnext(")");
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
        } else if (lx.peeking_skp("new")) {

            throw new UnsupportedOperationException();
        } else {
            return parseExpr2UnaryPost(lx);
        }
    }

    public static AST_Expr parseExpr4Multiplecation(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr3UnaryPre, "*", "/");
    }

    public static AST_Expr parseExpr5Addition(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr4Multiplecation, "+", "-");
    }

    public static AST_Expr parseExpr6BitwiseShifts(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr5Addition, "<<", ">>>", ">>");
    }

    public static AST_Expr parseExpr7Relations(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr6BitwiseShifts, "<", "<=", ">", ">=", "is");
    }

    public static AST_Expr parseExpr8Equals(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr7Relations, "==", "!=");
    }

    public static AST_Expr parseExpr9BitwiseAnd(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr8Equals, "&");
    }

    public static AST_Expr parseExpr10BitwiseXor(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr9BitwiseAnd, "^");
    }

    public static AST_Expr parseExpr11BitwiseOr(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr10BitwiseXor, "|");
    }

    public static AST_Expr parseExpr12LogicalAnd(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr11BitwiseOr, "&&");
    }

    public static AST_Expr parseExpr13LogicalOr(Lexer lx) {
        return _Parse_OperBin_LR(lx, SpParser::parseExpr12LogicalAnd, "||");
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
        return _Parse_OperBin_RL(lx, SpParser::parseExpr14TernaryConditional, "=");
    }



    /*
     * =============== STATEMENT ===============
     */

    public static AST_Stmt parseStmt(Lexer lx) {
        switch (lx.peek().text()) {
            case "{":
                return parseStmtBlock(lx);
            case ";":
                return AST_Stmt_Blank.INST;
            case "using":
            case "package":
                throw new UnsupportedOperationException();
            case "if":
                return parseStmtIf(lx);
            case "while":
                return parseStmtWhile(lx);
            case "return":
            case "class":
                throw new UnsupportedOperationException();
            default:
                int mark = lx.index;


                throw new UnsupportedOperationException(lx.peek().text());
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
                _Parse_RepeatUntil(lx, SpParser::parseStmt, until)
        );
    }

    public static AST_Stmt_Strm_If parseStmtIf(Lexer lex) {
        lex.rqnext("if").rqnext("(");
        AST_Expr cond = parseExpr(lex);
        lex.rqnext(")");

        AST_Stmt then = parseStmt(lex);

        AST_Stmt els = null;
        if (lex.peeking_skp("else")) {
            els = parseStmt(lex);
        }

        return new AST_Stmt_Strm_If(cond, then, els);
    }

    public static AST_Stmt_Strm_While parseStmtWhile(Lexer lx) {
        lx.rqnext("while").rqnext("(");
        AST_Expr cond = parseExpr(lx);
        lx.rqnext(")");

        AST_Stmt then = parseStmt(lx);

        return new AST_Stmt_Strm_While(cond, then);
    }
}
