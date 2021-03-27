package outskirts.lang.lexer.parser;

import java.util.Arrays;
import java.util.List;

public final class Operator {

    public static final int ASS_LEFT = 0;
    public static final int ASS_RIGHT = 1;

    public String name;
    public int priority;
    public int assocside;

    public Operator(String name, int priority, int assocside) {
        this.name = name;
        this.priority = priority;
        this.assocside = assocside;
    }

    public static List<Operator> OPERATORS = Arrays.asList(
            new Operator("=", 1, ASS_RIGHT),
            new Operator("==", 2, ASS_LEFT),
            new Operator("<", 2, ASS_LEFT),
            new Operator(">", 2, ASS_LEFT),
            new Operator("+", 3, ASS_LEFT),
            new Operator("-", 3, ASS_LEFT),
            new Operator("*", 4, ASS_LEFT),
            new Operator("/", 4, ASS_LEFT),
            new Operator("%", 4, ASS_LEFT),
            new Operator(".", 9, ASS_LEFT)
    );

    public static Operator get(String oper) {
        for (Operator op : OPERATORS) {
            if (op.name.equals(oper))
                return op;
        }
        return null;
    }
}
