package outskirts.util.jruntime.vm;

import outskirts.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

public class VM {

    private static Map<String, Function<Object[], Object>> systemfuncs = new HashMap<>();
    static {
        systemfuncs.put("print", args -> {
            System.out.println(args[0]);
            return null;
        });
    }


    public static void execGcode(String[] codes) {
        LinkedList<Object> opstack = new LinkedList<>();
        Object[] locals = new Object[1];  // local variable references

        for (String line : codes) {
            if (line.trim().isEmpty())
                continue;
            String[] b = StringUtils.explode(line, " ");
//            System.out.println("line: "+line);
            switch (b[0]) {
                case "const": {
                    if (b[1].equals("float")) {
                        opstack.push(Float.parseFloat(b[2]));
                    } else
                        throw new UnsupportedOperationException();
                    break;
                }
                case "store": {
                    Object o = opstack.pop();
                    int idx = Integer.parseInt(b[1]);
                    locals[idx] = o;
                    break;
                }
                case "load": {
                    int idx = Integer.parseInt(b[1]);
                    opstack.push(locals[idx]);
                    break;
                }
                case "nadd": {
                    float f1 = (float)opstack.pop();
                    float f2 = (float)opstack.pop();
                    opstack.push(f1 + f2);
                    break;
                }
                case "invoke": {
                    systemfuncs.get(b[1]).apply(opstack.toArray());
                    break;
                }
                default: {
                    System.out.println("Unknown instruction: "+b[0]);
                }
            }
        }
    }

    public static void main(String[] args) {

        execGcode(StringUtils.explode("const float 10\n" +
                "store 0\n" +
                "\n" +
                "load 0\n" +
                "const float 8\n" +
                "nadd\n" +
                "store 0\n" +
                "\n" +
                "load 0\n" +
                "invoke print\n" +
                "\n" +
                "return", "\n"));

    }

}
