package outskirts.lang.langdev.interpreter;

import outskirts.util.Validate;

import java.util.HashMap;
import java.util.Map;

public final class Scope {



    private final Map<String, GObject> variables = new HashMap<>();
    private final Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public void declare(String name, GObject v) {
        Validate.isTrue(!variables.containsKey(name), "The variable \""+name+"\" was already been declarated.");
        variables.put(name, v);
    }

    public GObject access(String name) {
        return locateScope(name).variables.get(name);
    }
    public void assign(String name, GObject v) {
        locateScope(name).variables.put(name, v);
    }

    private Scope locateScope(String name) {
        Scope scope = this;
        while (scope.variables.get(name) == null) {
            scope = scope.parent;
            if (scope == null)
                throw new IllegalStateException("Could not locate variable \""+name+"\".");
        }
        return scope;
    }

}
