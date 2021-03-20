package outskirts.lang.lexer.syntax;

import java.util.ArrayList;
import java.util.List;

public class Syntax {

    private List<Syntax> children = new ArrayList<>();

    public Syntax() { }

    public Syntax(List<Syntax> ls) {
        children.addAll(ls);
    }

    public Syntax child(int i) {
        return children.get(i);
    }
    public int size() {
        return children.size();
    }

    @Override
    public String toString() {
        return children.toString();
    }
}
