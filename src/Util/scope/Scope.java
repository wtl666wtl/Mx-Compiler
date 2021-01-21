package Util.scope;

import Util.error.semanticError;
import Util.type.*;
import Util.position;
import Util.substance;

import java.util.HashMap;

public class Scope {

    public HashMap<String, substance> members = new HashMap<>();
    public HashMap<String, funType> methods = new HashMap<>();
    public Scope fScope;
    public funType constructor = null;

    public Scope(Scope fScope) {
        this.fScope = fScope;
    }

    public void defineMember(String name, substance t, position pos) {
        if (members.containsKey(name)) throw new semanticError("redefine", pos);
        members.put(name, t);
    }

    public boolean containsMember(String name, boolean lookUpon) {
        if (members.containsKey(name)) return true;
        else if (fScope != null && lookUpon)return fScope.containsMember(name, true);
        else return false;
    }

    public void defineMethod(String name, funType t, position pos) {
        if (methods.containsKey(name)) throw new semanticError("redefine", pos);
        methods.put(name, t);
    }

    public boolean containsMethod(String name, boolean lookUpon) {
        if (methods.containsKey(name)) return true;
        else if (fScope != null && lookUpon)return fScope.containsMember(name, true);
        else return false;
    }

    public void defineConstructor(funType t, position pos) {
        if (constructor != null) throw new semanticError("redefine", pos);
        constructor = t;
    }

    public substance getMember(String name, position pos, boolean lookUpon) {
        if (members.containsKey(name)) return members.get(name);
        else if (fScope != null && lookUpon) return fScope.getMember(name, pos, true);
        throw new semanticError("undefine", pos);
    }

    public funType getMethod(String name, position pos, boolean lookUpon) {
        if (methods.containsKey(name)) return methods.get(name);
        else if (fScope != null && lookUpon) return fScope.getMethod(name, pos, true);
        throw new semanticError("undefine", pos);
    }

}
