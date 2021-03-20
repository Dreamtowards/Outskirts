package outskirts.lang.lexer.parser;

import outskirts.lang.lexer.Lexer;
import outskirts.lang.lexer.Token;
import outskirts.lang.lexer.syntax.Syntax;
import outskirts.lang.lexer.syntax.SyntaxToken;
import outskirts.util.Val;
import outskirts.util.Validate;
import outskirts.util.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Ruled Reader/Parser.
 */
public abstract class Rule {

    /**
     * @return nonnull if read (always).  null if skip (often, e.g. ensure ids).
     */
    public abstract Syntax read(Lexer lexer);



    public static class RuleList extends Rule {

        private List<Rule> rules = new ArrayList<>();

        @Override
        public Syntax read(Lexer lexer) {
            List<Syntax> ls = new ArrayList<>();
            for (Rule e : rules) {
                Syntax syn = e.read(lexer);
                if (syn != null) {
                    ls.add(syn);
                }
            }
            return new Syntax(ls);
        }

        public RuleList and(Rule rule) {
            rules.add(rule);
            return this;
        }

        public final RuleList id(String id) {
            return and(new RuleId(id));
        }
        public final RuleList name() {
            return and(new RuleName());
        }


        public final RuleList repeat(Rule e) {
            return and(new RuleRepeat(e));
        }
    }

    public static class RuleId extends Rule {
        private String id;
        public RuleId(String id) { this.id = id; }

        @Override
        public Syntax read(Lexer lexer) {
            Token t = lexer.next();
            Validate.isTrue(t.isName() || t.isBorder(), "Required type: name or border.");
            Validate.isTrue(t.getText().equals(id), "Expected: '%s', actually: '%s'", id, t.getText());
            return null;
        }
    }

    public static class RuleName extends Rule {
        @Override
        public Syntax read(Lexer lexer) {
            Token t = lexer.next();
            Validate.isTrue(t.isName(), "Expected type: Name, actually type: "+t.type() + " '"+t.getText()+"'");
            return new SyntaxToken(t);
        }
    }

    public static class RuleRepeat extends Rule {
        private Rule e;
        public RuleRepeat(Rule e) { this.e = e; }

        @Override
        public Syntax read(Lexer lexer) {
            List<Syntax> ls = new ArrayList<>();
            int i = lexer.index;
            try {
                while (true) {
                    ls.add(e.read(lexer));
                    i = lexer.index;
                }
            } catch (Exception ex) {
                // rule not match.
                // ex.printStackTrace();
                lexer.index = i;  // rollback.
            }
            return new Syntax(ls);
        }
    }

    public static class RuleOr extends Rule {
        private Rule[] es;
        public RuleOr(Rule... es) { this.es = es; }

        @Override
        public Syntax read(Lexer lexer) {
            int i = lexer.index;
            for (Rule e : es) {
                try {
                    return e.read(lexer);
                } catch (Exception ex) {
                    // not match
                    // ex.printStackTrace();
                    lexer.index = i;  // setback.
                }
            }
            throw new IllegalStateException("'OR' not matched");
        }
    }
}
