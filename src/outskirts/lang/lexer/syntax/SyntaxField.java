package outskirts.lang.lexer.syntax;

import java.util.List;

public class SyntaxField extends Syntax {

    public SyntaxField(List<Syntax> ls) {
        super(ls);
    }

    public String type() {
        return child(0).asToken();
    }
    public String name() {
        return child(1).asToken();
    }

}
