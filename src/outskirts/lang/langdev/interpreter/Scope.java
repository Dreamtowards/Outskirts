package outskirts.lang.langdev.interpreter;

import outskirts.util.Validate;

import java.util.HashMap;
import java.util.Map;

public final class Scope {

    public static Map<String, GObject> globalClassDef = new HashMap<>();

    private String _scopeCurrentClassnamePrefix = "";

    public String currentClassnamePrefix() {
        if (parent == null)
            return _scopeCurrentClassnamePrefix;

        return parent.currentClassnamePrefix()+_scopeCurrentClassnamePrefix;
    }

    public void setScopeCurrentClassnamePrefix(String pref) {
//        if (!_scopeCurrentClassnamePrefix.isEmpty())
//            throw new IllegalStateException("Alread Setted.? prev:'"+_scopeCurrentClassnamePrefix+"', next: '"+pref+"'");
        this._scopeCurrentClassnamePrefix = pref;
    }

    public final Map<String, GObject> variables = new HashMap<>();
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
        if (variables.containsKey(name)) {
            return this;
        } else if (parent != null) {
            return parent.locateScope(name);
        } else {
            throw new IllegalStateException("Could not locate variable \""+name+"\".");
        }
    }

    @Override
    public String toString() {
        return "Scope{" +
                "_scopeCurrentClassnamePrefix='" + _scopeCurrentClassnamePrefix + '\'' +
                ", variables=" + variables +
                ", parent=" + parent +
                '}';
    }
}
