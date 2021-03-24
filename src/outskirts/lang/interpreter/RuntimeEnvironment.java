package outskirts.lang.interpreter;

import outskirts.util.Validate;

import java.util.HashMap;
import java.util.Map;

public class RuntimeEnvironment {

    public final Map<String, Object> varables = new HashMap<>();

    public RuntimeEnvironment outer;

    public Object get(String name) {
        return locate(name).varables.get(name);
    }

    public void set(String name, Object val) {
        RuntimeEnvironment env = locate(name);
        Validate.isTrue(env.varables.containsKey(name));
        env.varables.put(name, val);
    }

    public void declare(String name, Object val) {
        Validate.isTrue(!varables.containsKey(name), "Already declared.");
        varables.put(name, val);
    }

    public RuntimeEnvironment locate(String varname) {

        if (varables.containsKey(varname))
            return this;
        else if (outer != null)
            return outer.locate(varname);
        else
            throw new NullPointerException("Undeclared variable: "+varname);
    }


}
