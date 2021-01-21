package Util.type;

import Util.position;
import Util.scope.Scope;

public class classType extends BaseType{
    public Scope localScope;

    public classType(String name){
        super(name);
        this.type = TypeCategory.CLASS;
    }

    public void defineMethod(String name, funType func, position pos){
        localScope.defineMethod(name, func, pos);
    }

    @Override public boolean isSame(Type other){
        return other.isNull()||(other.isClass()&&name.equals(((classType)other).name));
    }

}