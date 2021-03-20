package outskirts.lang.lexer.parser;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.Token;
import outskirts.lang.lexer.syntax.Syntax;
import outskirts.lang.lexer.syntax.SyntaxBiExpression;
import outskirts.lang.lexer.syntax.SyntaxToken;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Parser {

    private List<Rule> rules = new ArrayList<>();
    private Function<List<Syntax>, Syntax> createfunc;

    public Parser(Function<List<Syntax>, Syntax> createfunc) {
        this.createfunc = createfunc;
    }

    public static Parser rule(Function<List<Syntax>, Syntax> createfunc) {
        return new Parser(createfunc);
    }
    public static Parser rule() {
        return rule(Syntax::new);
    }

    // read()
    public Syntax read(Lexer lexer) {
        List<Syntax> out = new ArrayList<>();
        for (Rule n : rules) {
            out.addAll(
                n.parse(lexer)
            );
            Log.LOGGER.info(out);
        }
        return createfunc.apply(out);
    }

    static int stk = 0;
    public boolean match(Lexer lexer) {
        stk++;
        Log.LOGGER.info(StringUtils.repeat(" ", stk)+"Start Match, sz: "+rules.size());
        try {
            for (Rule n : rules) {
                Token t = lexer.peek();
                boolean m = n.match(lexer);
                Log.LOGGER.info(StringUtils.repeat(" ", stk)+"Match: "+m+" '"+t + "' ("+n.getClass().getSimpleName());
                if (!m) {

                    return false;
                }
            }
            return true;
        }finally {
            stk--;
        }
    }


    public Parser number() {
        rules.add(new RuleToken(SyntaxToken::new, Token.TYPE_NUMBER, null));
        return this;
    }

    public Parser name() {
        rules.add(new RuleToken(SyntaxToken::new, Token.TYPE_NAME, null));
        return this;
    }

//    public Parser id(String id) {
//        rules.add(new RuleToken(null, Token.TYPE_BORDER, id));
//        return this;
//    }

    public Parser tree(Parser p) {
        rules.add(new Rule() {
            @Override
            public List<Syntax> parse(Lexer lexer) {
                return Collections.singletonList(p.read(lexer));
            }
            @Override
            public boolean match(Lexer lexer) {
                return p.match(lexer);
            }
        });
        return this;
    }

    public Parser repeat(Parser p) {
        rules.add(new NodeRepeatTree(p));
        return this;
    }

    public Parser or(Parser... ps) {
        rules.add(new NodeOrTree(ps));
        return this;
    }



    private static class Rule {

        public List<Syntax> parse(Lexer lexer) {
            return null;
        }

        public boolean match(Lexer lexer) {
            return false;
        }

    }

    private static class RuleToken extends Rule {

        // null: dosent create tree.
        private Function<Token, Syntax> createfunc;
        private int tokentype;
        private String requiredtoken;

        public RuleToken(Function<Token, Syntax> createfunc, int tokentype, String requiredtoken) {
            this.createfunc = createfunc;
            this.tokentype = tokentype;
            this.requiredtoken = requiredtoken;
        }

        @Override
        public List<Syntax> parse(Lexer lexer) {
            Token t = lexer.next();
            if (t.type() == tokentype && (requiredtoken == null || requiredtoken.equals(t.getText()))) {
                if (createfunc == null)
                    return Collections.emptyList();
                return Collections.singletonList(createfunc.apply(t));
            } else {
                throw new IllegalStateException("illegal token: '"+t+"'. required: ["+tokentype+"]"+requiredtoken);
            }
        }

        @Override
        public boolean match(Lexer lexer) {
            Token t = lexer.next();
//            Log.LOGGER.info(t);
            return t.type()==tokentype && (requiredtoken==null || requiredtoken.equals(t.getText()));
        }

    }

    public static class NodeRepeatTree extends Rule {

        private Parser parser;

        public NodeRepeatTree(Parser parser) {
            this.parser = parser;
        }

        @Override
        public List<Syntax> parse(Lexer lexer) {
            List<Syntax> out = new ArrayList<>();
            while (parser.match(lexer)) {
                Syntax s = parser.read(lexer);
//                if (s.size() > 0)
                out.add(s);
            }
            return out;
        }

        @Override
        public boolean match(Lexer lexer) {
            while (parser.match(lexer)) {

            }
            return false;
        }
    }

    public static class NodeOrTree extends Rule {

        private Parser[] parsers;

        public NodeOrTree(Parser[] parsers) {
            this.parsers = parsers;
        }

        @Override
        public List<Syntax> parse(Lexer lexer) {
            Parser p = findMatched(lexer);
            if (p == null)
                throw new IllegalStateException("Not found 'OR' available tree." + lexer.peek());

            return Collections.singletonList(p.read(lexer));
        }

        @Override
        public boolean match(Lexer lexer) {
            return findMatched(lexer) != null;
        }

        private Parser findMatched(Lexer lexer) {
            for (Parser p : parsers) {
                if (p.match(lexer))
                    return p;
            }
            return null;
        }
    }


}
