package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.*;
import outskirts.lang.langdev.lexer.Lexer;
import outskirts.lang.langdev.lexer.Token;
import outskirts.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Parserls extends Parser {

    private final List<Parser> parsers = new ArrayList<>();

    /**
     * AST createfunc on Read.
     */
    private Function<List<AST>, AST> createfunc;

    private boolean useLookaheadUntil = false;

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

    @Override
    public boolean match(Lexer lex) {
        // empty parsers should been pass.
        // e.g. ornull.  pass().and(sth).or(suffix1, suffix2, pass() /*empty, no suffix*/).
        if (parsers.isEmpty())
            return true;
        if (!useLookaheadUntil)
            return parsers.get(0).match(lex);

        // Lookahead.
        int mark = lex.index;
        boolean pass = true;
        List<AST> tmpls = new ArrayList<>();  // correct.?
        for (Parser p : parsers) {
            if (p == ParserLookaheadTag.LOOKAHEAD_TAG)
                break;
            try {
                p.read(lex, tmpls);
            } catch (Exception ex) {
                pass = false;
                break;
            }
        }
        lex.index = mark;  // restore.
        return pass;
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
        useLookaheadUntil = true;
        return and(ParserLookaheadTag.LOOKAHEAD_TAG);
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
    public Parserls id(String id, boolean create) {
        return token(t -> {
            if (!t.isIdentifier() || !t.text().equals(id))
                return "Expected identifier: \""+id+"\", actual: \""+t.text() + "\".";
            return null;
        }, create);
    }
    public Parserls id(String id) {
        return id(id, false);
    }
    public Parserls iden(String... ids) {
        return token(t -> {
            if (!t.isIdentifier() || !CollectionUtils.contains(ids, t.text()))
                return "Expected identifiers: "+Arrays.toString(ids)+", actual: "+t.detailString()+".";
            return null;
        }, true);
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

    public final Parserls repeatjoin(Parser p, String delimiter) {
        return op(pass().and(p).repeat(pass().id(delimiter).and(p)));
    }
    public final Parserls oper_bi_lr(Parser factor, String... opers) {
        return and(factor).repeat(pass().iden(opers).and(factor).composesp(3, AST_Expr_OperBi::new));
    }

    public final Parserls opnull(Parser p) {  // how about op-RealNull, its more fast.
        return or(p, ParserPutNull.INST);
    }
    public final Parserls opempty(Parser p) {
        return or(p, struct(ASTls::new));
    }

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
