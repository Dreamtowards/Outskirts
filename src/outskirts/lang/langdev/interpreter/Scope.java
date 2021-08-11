package outskirts.lang.langdev.interpreter;

import outskirts.lang.langdev.ast.oop.AST_Stmt_DefClass;
import outskirts.util.Validate;

import java.util.HashMap;
import java.util.Map;

public final class Scope {

    public AST_Stmt_DefClass clxdef;  // only for ClassDef. scopes.



    private String classnamePrefix;
    public String currentClassnamePrefix() {
        if (parent == null)
            return classnamePrefix;

        String p = parent.currentClassnamePrefix();
        if (classnamePrefix != null && p != null) {
            return p+"."+classnamePrefix;
        } else if (classnamePrefix != null) {
            return classnamePrefix;
        } else if (p != null) {
            return p;
        } else {
            return null;
        }
    }
    public void setClassnamePrefix(String pref) {
        Validate.isTrue(!pref.isEmpty());
        if (classnamePrefix != null)
            throw new IllegalStateException("Already set "+classnamePrefix);

        classnamePrefix = pref;
    }

    public final Map<String, GObject> variables = new HashMap<>();
    public final Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public void declare(String name, GObject v) {
        Validate.isTrue(!variables.containsKey(name), "The variable \""+name+"\" was already been declarated.");
        variables.put(name, v);
    }

    public GObject tryaccess(String name) {
        try {
            return access(name);
        } catch (IllegalStateException ex) {
            return null;  // not exists.
        }
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
        return "Scope{" + variables +'}';
    }
}
