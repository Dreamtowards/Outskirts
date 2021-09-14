package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.ast.AST__Annotation;
import outskirts.lang.langdev.ast.AST_Stmt_DefClass;
import outskirts.lang.langdev.ast.AST_Stmt_Using;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.lang.langdev.lexer.TokenType;
import outskirts.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class LxParser {


    /*
     * =============== PARSER UTILITY ===============
     */

    public static AST_Expr _Parse_OperBin_LR(Lexer lx, Function<Lexer, AST_Expr> factorpsr, TokenType... opers) {
        AST_Expr l = factorpsr.apply(lx);
        Token opr;
        while ((opr=lx.selnext(opers)) != null) {
            AST_Expr r = factorpsr.apply(lx);
            l = new AST_Expr_OperBinary(l, r, AST_Expr_OperBinary.BinaryKind.of(opr.type()));
        }
        return l;
    }
    public static AST_Expr _Parse_OperBin_RL(Lexer lx, Function<Lexer, AST_Expr> fac, TokenType oper) {
        AST_Expr l = fac.apply(lx);
        if (lx.nexting(oper)) {
            AST_Expr r = _Parse_OperBin_RL(lx, fac, oper);
            return new AST_Expr_OperBinary(l, r, AST_Expr_OperBinary.BinaryKind.of(oper));
        } else {
            return l;
        }
    }

    /**
     * @param zeromoreUntil nullable. null: zero not allowed.
     */
    public static <T extends AST> List<T> _Parse_RepeatJoin_ZeroMoreUntil(Lexer lx, Function<Lexer, T> factorpsr, TokenType delimiter, TokenType zeromoreUntil) {
        if (zeromoreUntil != null && lx.peeking(zeromoreUntil))  // Reach Zero Terminal.
            return Collections.emptyList();

        List<T> ls = new ArrayList<>();
        ls.add(factorpsr.apply(lx));

        while (lx.nexting(delimiter)) {
            ls.add(factorpsr.apply(lx));
        }

        if (zeromoreUntil != null && !lx.peeking(zeromoreUntil))  // not terminated as expected.
            throw new IllegalStateException();
        return ls;
    }
    public static <T extends AST> List<T> _Parse_RepeatJoin_OneMore(Lexer lx, Function<Lexer, T> fac, TokenType delimiter) {
        return _Parse_RepeatJoin_ZeroMoreUntil(lx, fac, delimiter, null);
    }

    public static <T> List<T> _Parse_RepeatUntil(Lexer lx, Function<Lexer, T> psr, TokenType until) {
        List<T> ls = new ArrayList<>();
        while (!lx.peeking(until)) {
            ls.add(psr.apply(lx));
        }
        return ls;
    }


    // for expr_funccall, annotation.
    public static List<AST_Expr> _Parse_FuncArgs(Lexer lx) {
        lx.next(TokenType.LPAREN);
        var l = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parseExpr, TokenType.COMMA, TokenType.RPAREN);
        lx.next(TokenType.RPAREN);
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
        if (e instanceof AST_Expr_MemberAccess) {
            AST_Expr_MemberAccess c = (AST_Expr_MemberAccess)e;
            return _ExpandQualifiedName(c.getExpression()) + "." + c.getIdentifier();
        } else {
            return ((AST_Expr_PrimaryIdentifier)e).getName();
        }
    }




    /*
     * =============== EXPRESSION ===============
     */

    public static AST_Expr parseExpr(Lexer lx) {
        return parseExpr15Assignment(lx);
    }

    public static AST_Expr_PrimaryIdentifier parseExprPrimaryIdentifier(Lexer lx) {
        return new AST_Expr_PrimaryIdentifier(lx.next(TokenType.IDENTIFIER));
    }

    public static AST_Expr parseExprPrimary(Lexer lx) {
        Token t = lx.peek();
        switch (t.type()) {
            /* BEGIN LITERAL */
            case LITERAL_INT:
                lx.next();
                return new AST_Expr_PrimaryLiteral(Integer.parseInt(t.content()), AST_Expr_PrimaryLiteral.LiteralKind.INT32);
            case LITERAL_FLOAT:
                lx.next();
                return new AST_Expr_PrimaryLiteral(Float.parseFloat(t.content()), AST_Expr_PrimaryLiteral.LiteralKind.FLOAT32);
            case LITERAL_CHAR:
                lx.next();
                return new AST_Expr_PrimaryLiteral(t.content().charAt(0), AST_Expr_PrimaryLiteral.LiteralKind.CHAR);
            case LITERAL_STRING:
                lx.next();
                return new AST_Expr_PrimaryLiteral(t.content(), AST_Expr_PrimaryLiteral.LiteralKind.STRING);
            case LITERAL_TRUE:
                lx.next();
                return new AST_Expr_PrimaryLiteral(true, AST_Expr_PrimaryLiteral.LiteralKind.BOOL);
            case LITERAL_FALSE:
                lx.next();
                return new AST_Expr_PrimaryLiteral(false, AST_Expr_PrimaryLiteral.LiteralKind.BOOL);
            /* END LITERAL */

            case IDENTIFIER:
                return parseExprPrimaryIdentifier(lx);
            case LPAREN: {
                lx.next();
                AST_Expr r = parseExpr(lx);
                lx.next(TokenType.RPAREN);
                return r;
            }
            case NEW: {
                lx.next();
                AST_Expr type = parse_TypeExpression(lx);
                List<AST_Expr> args = _Parse_FuncArgs(lx);
                return new AST_Expr_OperNew(type, args);
            }
            case SIZEOF: {
                lx.next();
                lx.next(TokenType.LPAREN);
                AST_Expr type = parse_TypeExpression(lx);
                lx.next(TokenType.RPAREN);
                return new AST_Expr_OperSizeOf(type);
            }
            case DEREFERENCE: {
                lx.next();
                lx.next(TokenType.LT);
                AST_Expr type = parse_TypeExpression(lx);
                lx.next(TokenType.GT);
                lx.next(TokenType.LPAREN);
                AST_Expr expr = parseExpr(lx);
                lx.next(TokenType.RPAREN);
                return new AST_Expr_TmpDereference(type, expr);
            }
            case REFERENCE: {
                lx.next();
                lx.next(TokenType.LPAREN);
                AST_Expr expr = parseExpr(lx);
                lx.next(TokenType.RPAREN);
                return new AST_Expr_TmpReference(expr);
            }
            default:
                throw new IllegalStateException("Illegal Primary Expr. at "+t.detailString());
        }
    }

    public static AST_Expr parseExpr1AccessCall(Lexer lx) {
        AST_Expr l = parseExprPrimary(lx);
        while (true) {
            TokenType typ = lx.peek().type();
            if (typ == TokenType.DOT)
            {
                lx.next();
                String memb = parseExprPrimaryIdentifier(lx).getName();
                l = new AST_Expr_MemberAccess(l, memb);
            }
            else if (typ == TokenType.LPAREN)
            {
                List<AST_Expr> args = _Parse_FuncArgs(lx);
                l = new AST_Expr_FuncCall(l, args);
            }
            else
            {
                return l;
            }
        }
    }

    public static AST_Expr parseExpr2UnaryPost(Lexer lx) {
        AST_Expr l = parseExpr1AccessCall(lx);
        Token opr;
        while ((opr=lx.selnext(TokenType.PLUSPLUS, TokenType.SUBSUB)) != null) {
            l = new AST_Expr_OperUnary(l, AST_Expr_OperUnary.UnaryKind.of(opr.type(), true));
        }
        return l;
    }

    public static AST_Expr parseExpr3UnaryPre(Lexer lx) {
        Token opr;
        if ((opr=lx.selnext(TokenType.PLUSPLUS, TokenType.SUBSUB, TokenType.PLUS, TokenType.SUB, TokenType.BANG, TokenType.TILDE)) != null) {
            AST_Expr r = parseExpr3UnaryPre(lx);
            return new AST_Expr_OperUnary(r, AST_Expr_OperUnary.UnaryKind.of(opr.type(), false));
        } else {
            return parseExpr2UnaryPost(lx);
        }
    }

    public static AST_Expr parseExpr4Multiplecation(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr3UnaryPre, TokenType.STAR, TokenType.SLASH);
    }

    public static AST_Expr parseExpr5Addition(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr4Multiplecation, TokenType.PLUS, TokenType.SUB);
    }

    public static AST_Expr parseExpr6BitwiseShifts(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr5Addition, TokenType.LTLT, TokenType.GTGTGT, TokenType.GTGT);
    }

    public static AST_Expr parseExpr7Relations(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr6BitwiseShifts, TokenType.LTEQ, TokenType.LT, TokenType.GT, TokenType.GTEQ, TokenType.IS);
    }

    public static AST_Expr parseExpr8Equals(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr7Relations, TokenType.EQEQ, TokenType.BANGEQ);
    }

    public static AST_Expr parseExpr9BitwiseAnd(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr8Equals, TokenType.AMP);
    }

    public static AST_Expr parseExpr10BitwiseXor(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr9BitwiseAnd, TokenType.CARET);
    }

    public static AST_Expr parseExpr11BitwiseOr(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr10BitwiseXor, TokenType.BAR);
    }

    public static AST_Expr parseExpr12LogicalAnd(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr11BitwiseOr, TokenType.AMPAMP);
    }

    public static AST_Expr parseExpr13LogicalOr(Lexer lx) {
        return _Parse_OperBin_LR(lx, LxParser::parseExpr12LogicalAnd, TokenType.BARBAR);
    }

    public static AST_Expr parseExpr14TernaryConditional(Lexer lx) {
        AST_Expr l_cond = parseExpr13LogicalOr(lx);
        if (lx.nexting(TokenType.QUES)) {
            AST_Expr then = parseExpr14TernaryConditional(lx);
            lx.next(TokenType.COLON);
            AST_Expr els = parseExpr14TernaryConditional(lx);
            return new AST_Expr_OperConditional(l_cond, then, els);
        } else {
            return l_cond;
        }
    }

    public static AST_Expr parseExpr15Assignment(Lexer lx) {
        return _Parse_OperBin_RL(lx, LxParser::parseExpr14TernaryConditional, TokenType.EQ);
    }






    /*
     * =============== STATEMENT ===============
     */

    public static AST_Stmt parseStmt(Lexer lx) {
        switch (lx.peek().type()) {
            case LBRACE:
                return parseStmtBlock(lx);
            case SEMI:
                lx.next();
                return AST_Stmt_Blank.INST;
            case USING:
                return parseStmtUsing(lx);
            case NAMESPACE:
                return parseStmtNamespace(lx);
            case IF:
                return parseStmtIf(lx);
            case WHILE:
                return parseStmtWhile(lx);
            case RETURN:
                return parseStmtReturn(lx);
            default:
                AST__Modifiers modifiers = parse_Modifiers(lx);
                lx.mark();

                if (lx.peeking(TokenType.CLASS))
                {
                    AST_Stmt_DefClass a = parseStmtDefClass(lx); a.modifiers = modifiers; return a;
                }
                else if (_IsPass(lx, LxParser::parse_TypeExpression) && _IsPass(lx, LxParser::parseExprPrimaryIdentifier))  // is: Typename id
                {
                    TokenType t = lx.peek().type();
                    lx.unmark();
                    if (t == TokenType.LPAREN)
                    {
                        AST_Stmt_DefFunc a = parseStmtDefFunc(lx); a.modifiers = modifiers; return a;
                    }
                    else if (t == TokenType.EQ || t == TokenType.SEMI)
                    {
                        AST_Stmt_DefVar a = parseStmtDefVar(lx); a.modifiers = modifiers; return a;
                    }
                    else throw new IllegalStateException();
                }
                else
                {
                    Validate.isTrue(modifiers == AST__Modifiers.DEFAULT, "illegal modifiers exists.");

                    lx.unmark();
                    return parseStmtExpr(lx);
                }
        }
    }

    public static AST_Stmt_Block parseStmtBlock(Lexer lx) {
        lx.next(TokenType.LBRACE);
        AST_Stmt_Block s = new AST_Stmt_Block(_Parse_RepeatUntil(lx, LxParser::parseStmt, TokenType.RBRACE));
        lx.next(TokenType.RBRACE);
        return s;
    }

    public static AST_Stmt_If parseStmtIf(Lexer lx) {
        lx.next(TokenType.IF);
        lx.next(TokenType.LPAREN);
        AST_Expr cond = parseExpr(lx);
        lx.next(TokenType.RPAREN);

        AST_Stmt then = parseStmt(lx);

        AST_Stmt els = null;
        if (lx.nexting(TokenType.ELSE)) {
            els = parseStmt(lx);
        }

        return new AST_Stmt_If(cond, then, els);
    }

    public static AST_Stmt_While parseStmtWhile(Lexer lx) {
        lx.next(TokenType.WHILE);
        lx.next(TokenType.LPAREN);
        AST_Expr cond = parseExpr(lx);
        lx.next(TokenType.RPAREN);

        AST_Stmt then = parseStmt(lx);

        return new AST_Stmt_While(cond, then);
    }

    public static AST_Stmt_Return parseStmtReturn(Lexer lx) {
        lx.next(TokenType.RETURN);
        AST_Expr expr = null;
        if (!lx.peeking(TokenType.SEMI)) {
            expr = parseExpr(lx);
        }
        lx.next(TokenType.SEMI);
        return new AST_Stmt_Return(expr);
    }

    public static AST_Stmt_Using parseStmtUsing(Lexer lx) {
        lx.next(TokenType.USING);

        boolean isStatic = false;
        if (lx.nexting(TokenType.STATIC)) {
            isStatic = true;
        }

        AST_Expr addr = parse_QualifiedName(lx);

        String name = addr instanceof AST_Expr_MemberAccess ? ((AST_Expr_MemberAccess)addr).getIdentifier() : ((AST_Expr_PrimaryIdentifier)addr).getName();
        if (lx.nexting(TokenType.AS)) {
            name = parseExprPrimaryIdentifier(lx).getName();
        }
        lx.next(TokenType.SEMI);

        return new AST_Stmt_Using(isStatic, addr, name);
    }

    public static AST_Stmt_Namespace parseStmtNamespace(Lexer lx) {
        lx.next(TokenType.NAMESPACE);

        AST_Expr name = parse_QualifiedName(lx);

        List<AST_Stmt> stmts = new ArrayList<>();
        if (lx.nexting(TokenType.SEMI)) {   // the single scope namespace. until next namespace(same-level) or EOF.
            while (!lx.selpeeking(TokenType.NAMESPACE, TokenType.EOF))
            {
                stmts.add(parseStmt(lx));
            }
        }
        else
        {
            stmts = parseStmtBlock(lx).getStatements();
        }

        return new AST_Stmt_Namespace(name, stmts);
    }

    public static AST_Stmt_Expr parseStmtExpr(Lexer lx) {
        AST_Expr expr = parseExpr(lx);
        lx.next(TokenType.SEMI);
        return new AST_Stmt_Expr(expr);
    }


    public static AST_Stmt_DefFunc parseStmtDefFunc(Lexer lx) {

        AST_Expr rettype = parse_TypeExpression(lx);
        String name = lx.next(TokenType.IDENTIFIER).content();

        lx.next(TokenType.LPAREN);
        var params = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parseStmtDefVar_Def, TokenType.COMMA, TokenType.RPAREN);
        lx.next(TokenType.RPAREN);

        var body = parseStmtBlock(lx);

        return new AST_Stmt_DefFunc(rettype, name, params, body);
    }

    public static AST_Stmt_DefVar parseStmtDefVar(Lexer lx) {
        AST_Stmt_DefVar a = parseStmtDefVar_Def(lx);
        lx.next(TokenType.SEMI);
        return a;
    }

    // not the ';'. for support of FuncParams.
    public static AST_Stmt_DefVar parseStmtDefVar_Def(Lexer lx) {
        AST_Expr type = parse_TypeExpression(lx);
        String name = lx.next(TokenType.IDENTIFIER).content();

        AST_Expr init = null;
        if (lx.nexting(TokenType.EQ)) {
            init = parseExpr(lx);
        }

        return new AST_Stmt_DefVar(type, name, init);
    }

    public static AST_Stmt_DefClass parseStmtDefClass(Lexer lx) {
        lx.next(TokenType.CLASS);
        String name = lx.next(TokenType.IDENTIFIER).content();

        List<AST_Expr_PrimaryIdentifier> genericParams = Collections.emptyList();
        if (lx.nexting(TokenType.LT)) {
            genericParams = _Parse_RepeatJoin_OneMore(lx, LxParser::parseExprPrimaryIdentifier, TokenType.COMMA);
            lx.next(TokenType.GT);
        }

        List<AST_Expr> supers = Collections.emptyList();
        if (lx.nexting(TokenType.COLON)) {
            supers = _Parse_RepeatJoin_OneMore(lx, LxParser::parse_TypeExpression, TokenType.COMMA);
        }

        List<AST_Stmt> stmts_members = parseStmtBlock(lx).getStatements();

        // validate member type.
        stmts_members.forEach(e -> Validate.isTrue(e instanceof AST_Stmt_DefClass || e instanceof  AST_Stmt_DefVar || e instanceof AST_Stmt_DefFunc, "Illegal class member."));

        return new AST_Stmt_DefClass(name, genericParams, supers, stmts_members);
    }






    public static AST_Expr parse_QualifiedName(Lexer lx) {
        AST_Expr l = parseExprPrimaryIdentifier(lx);
        while (lx.nexting(TokenType.DOT)) {
            l = new AST_Expr_MemberAccess(l, parseExprPrimaryIdentifier(lx).getName());
        }
        return l;
    }

    public static AST_Expr parse_TypeExpression(Lexer lx) {
        AST_Expr type = parse_QualifiedName(lx);

        if (lx.nexting(TokenType.LT)) {  // the ">>" peoblem already solved as 'ofcourse' since there using In-time Lexer system.
            List<AST_Expr> generic_args = _Parse_RepeatJoin_ZeroMoreUntil(lx, LxParser::parse_TypeExpression, TokenType.COMMA, TokenType.GT);
            lx.next(TokenType.GT);
//            return new AST_Expr_GenericsArguments(type, generic_args);
            throw new UnsupportedOperationException("unsupported.");
        } else {
            return type;
        }
    }

    public static AST__Annotation parse_Annotation(Lexer lx) {
        lx.next(TokenType.AT);
        AST_Expr name = parse_QualifiedName(lx);

        List<AST_Expr> args = Collections.emptyList();
        if (lx.peeking(TokenType.LPAREN)) {
            args = _Parse_FuncArgs(lx);
        }

        return new AST__Annotation(name, args);
    }

    public static AST__Modifiers parse_Modifiers(Lexer lx) {
        List<AST__Annotation> annos = new ArrayList<>();
        while (lx.peeking(TokenType.AT)) {
            annos.add(parse_Annotation(lx));
        }

        List<TokenType> modifiers = new ArrayList<>();
        Token t;
        while ((t=lx.selnext(TokenType.MODIFIERS)) != null) {
            Validate.isTrue(!modifiers.contains(t.type()), "modifier '"+t+"' duplicated.");
            modifiers.add(t.type());
        }

        if (annos.isEmpty() && modifiers.isEmpty())
            return AST__Modifiers.DEFAULT;

        return new AST__Modifiers(annos, modifiers);
    }

    public static AST__CompilationUnit parse_CompilationUnit(Lexer lx) {
        return new AST__CompilationUnit(
                _Parse_RepeatUntil(lx, LxParser::parseStmt, TokenType.EOF));
    }
}
