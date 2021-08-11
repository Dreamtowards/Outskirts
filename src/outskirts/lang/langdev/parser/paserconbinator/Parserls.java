package outskirts.lang.langdev.parser.paserconbinator;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Parserls extends Parser {

    private final List<Parser> parsers = new ArrayList<>();

    /**
     * AST createfunc on Read.
     */
    private Function<List<AST>, AST> createfunc;

    private int lookaheadMode = LOOKAHEAD_INDEX;

    private int lookaheadIndex = 1;

    private static final int LOOKAHEAD_UNTIL = 1;
    private static final int LOOKAHEAD_INDEX = 2;

    private String dbg_name = "UNNAMED";

//    @Override
//    public List<AST> read(Lexer lex) {
//        try {
//            List<AST> ls = new ArrayList<>();
//            for (Parser p : parsers) {
//                ls.addAll(p.read(lex));
//            }
//            if (createfunc != null) {
//                return singletonList(createfunc.apply(ls));
//            } else {
//                return ls;
//            }
//        } catch (ParsingException ex) {
//            throw new RuntimeException("An error occurred on parsing "+this, ex);
//        }
//    }

    @Override
    public void read(Lexer lex, List<AST> out) {
        try {
            boolean struct = createfunc != null;
            List<AST> ls = struct ? new ArrayList<>() : out;

            for (Parser p : parsers) {
                p.read(lex, ls);
            }

            if (struct) {
                out.add(createfunc.apply(ls));
            }
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred on parsing "+this, ex);
        }
    }

    private static LinkedList<String> callstack = new LinkedList<>();

    @Override
    public boolean match(Lexer lex) {
        try {
            callstack.push(dbg_name);
        // empty parsers should been pass.
        // e.g. ornull.  pass().and(sth).or(suffix1, suffix2, pass() /*empty, no suffix*/).
        if (parsers.isEmpty())
            return true;
        // specialized. only checking first of first. not full of first.
        if (lookaheadMode == LOOKAHEAD_INDEX && lookaheadIndex == 1) {
            return parsers.get(0).match(lex);
        }

        // Lookahead.
        int mark = lex.index;
        boolean pass = true;
        List<AST> tmpls = new ArrayList<>();  // correct.?
        int i = 0;
        for (Parser p : parsers) {
            if (lookaheadMode == LOOKAHEAD_UNTIL && p == ParserLookaheadTag.LOOKAHEAD_TAG)
                break;
            if (lookaheadMode == LOOKAHEAD_INDEX && i == lookaheadIndex) {
                System.out.println("Match "+callstack+" "+lex.peek().detailString());
                break;
            }
            try {
                p.read(lex, tmpls);
            } catch (Exception ex) {
//                System.err.println("############## Bad Match "+ex);
//                ex.printStackTrace();
                pass = false;
                break;
            }
            i++;
        }
        lex.index = mark;  // restore.
        if (lookaheadMode==LOOKAHEAD_INDEX && lookaheadIndex==1) {
            if (parsers.get(0).match(lex) != pass)
                System.out.println("Diff "+dbg_name);
        }
        return pass;
        } finally {
            callstack.pop();
        }
    }

    // passthrough
    public static Parserls pass() {
        Parserls p = struct(null);
        p.dbg_name = new Throwable().getStackTrace()[1].toString();
        return p;
    }
    public static Parserls struct(Function<List<AST>, AST> createfunc) {
        Parserls l = new Parserls();
        l.createfunc = createfunc;
        l.dbg_name = new Throwable().getStackTrace()[1].toString();
        return l;
    }

    public Parserls initname(String s) {
        dbg_name = s;
        return this;
    }

    public Parserls and(Parser p) {
        parsers.add(p);
        return this;
    }

    public Parserls markLookahead() {
        lookaheadMode = LOOKAHEAD_UNTIL;
        return and(ParserLookaheadTag.LOOKAHEAD_TAG);
    }
    public Parserls lookaheadIndex(int i) {
        lookaheadMode = LOOKAHEAD_INDEX;
        lookaheadIndex = i;
        return this;
    }

    private Parserls token(Function<Token, String> validator, boolean create) {
        ParserToken p = new ParserToken(validator, create);
        return and(p);
    }

    public Parserls number() {
        return token(t -> {
            if (!t.isNumber())
                return "Required Type: Number.";
            return null;
        }, true);
    }
    public Parserls string() {
        return token(t -> {
            if (!t.isString())
                return "Required Type: String.";
            return null;
        }, true);
    }
    public Parserls name() {
        return token(t -> {
            if (!t.isName())
                return "Required Type: Name.";
            return null;
        }, true);
    }

    /**
     * @param rqNx requiredNextToTheNext
     */
    public Parserls id(String id, boolean create, boolean rqNx) {
        return token(t -> {
            if (rqNx && !t.isConnectedNext())
                return "Required Token NextToTheNext. actual: false.";
            if (!t.isIdentifier() || !t.text().equals(id))
                return "Expected identifier: \""+id+"\", actual: \""+t.text() + "\".";
            return null;
        }, create);
    }
    public Parserls id(String id) {
        return id(id, false, false);
    }

    /**
     * @param ids Parserls(rule) or String(id).
     */
    public Parserls iden(Object... ids) {
        Parserls[] ps = new Parserls[ids.length];
        for (int i = 0;i < ids.length;i++) {
            Object e = ids[i];
            if (e instanceof String) {
                ps[i] = pass().id((String)e, true, false);
            } else if (e instanceof Parserls) {
                ps[i] = (Parserls)e;
            } else
                throw new IllegalArgumentException();
        }
        return or(ps);
    }

    public Parserls iden_connected(String... parts) {
        Parserls p = struct(AST_Token::composeConnected);
        for (int i = 0;i < parts.length;i++) {
            boolean last = i == parts.length-1;
            p.id(parts[i], true, !last);
        }
        p.markLookahead();  // important. FullCheck Required.
        return and(p);
    }

    public Parserls or(Parser... ps) {
        return and(new ParserOr(Arrays.asList(ps)));
    }

    public Parserls repeat(Parser p) {
        return and(new ParserRepeat(p, false));
    }
    public Parserls op(Parser p) {
        return and(new ParserRepeat(p, true));
    }

    public final Parserls repeatjoin(Parser p, String delimiter, boolean onemore) {
        var r = pass().and(p).repeat(pass().id(delimiter).and(p));
        if (onemore) {
            and(r);
        } else {
            op(r);
        }
        return this;
    }
    public final Parserls repeatjoin(Parser p, String delimiter) {
        return repeatjoin(p, delimiter, false);
    }

//    public final Parserls oper_bi_lr(Parser factor, Object... opers) {
//        return and(factor).repeat(pass().iden(opers).and(factor).composesp(3, AST_Expr_OperBi::new));
//    }

    public final Parserls opnull(Parser p) {  // how about op-RealNull, its more fast.
        return or(p, ParserPutNull.INST);
    }
//    public final Parserls opempty(Parser p) {
//        return or(p, struct(ASTls::new));
//    }

    public Parserls composer(Consumer<ParserComposer.ProxyStack> composer) {
        return and(new ParserComposer(composer));
    }
    // Compose Stack Top's.
    public Parserls composesp(int n, Function<List<AST>, AST> createfunc) {
        return composer(stk -> {
            List<AST> ls = new ArrayList<>(n);
            for (int i = 0;i < n;i++) {
                ls.add(0, stk.pop());
            }
            stk.push(createfunc.apply(ls));
        });
    }

    @Override
    public String toString() {
        return "{"+dbg_name+"}";
    }
}
