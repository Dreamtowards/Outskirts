package outskirts.lang.g1.interpreter;

import outskirts.util.Validate;

import java.util.HashMap;
import java.util.Map;

public class RuntimeEnvironment {

    public final Map<String, ObjectInstance> varables = new HashMap<>();

    public RuntimeEnvironment outer;

    public ObjectInstance get(String name) {
        return locate(name).varables.get(name);
    }

    public void declare(String name, ObjectInstance obj) {
        Object prev = varables.put(name, obj);
        Validate.isTrue(prev == null, "Error detected. Variable already declared. ("+name);
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
