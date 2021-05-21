package outskirts.lang.langdev.parser;

import outskirts.lang.langdev.ast.AST;
import outskirts.lang.langdev.lexer.Lexer;

import java.util.List;
import java.util.function.Consumer;

public class ParserComposer extends Parser {

    private Consumer<ProxyStack> composer;

    public ParserComposer(Consumer<ProxyStack> composer) {
        this.composer = composer;
    }

    @Override
    public void read(Lexer lex, List<AST> out) {
        ProxyStack stack = new ProxyStack();
        stack.ls = out;
        composer.accept(stack);
    }

    @Override
    public boolean match(Lexer lex) {
        return true;
    }

    public static final class ProxyStack {
        private List<AST> ls;

        public <T extends AST> T pop() {
            return (T)ls.remove(ls.size()-1);
        }
        public void push(AST n) {
            ls.add(n);
        }
    }
}
