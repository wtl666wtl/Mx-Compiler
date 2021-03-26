package Util.type;

import Util.position;
import Util.scope.Scope;

import java.util.ArrayList;

public class classType extends BaseType{

    public int memberAllocSize = 0;
    public Scope localScope;
    public ArrayList<Type> memberTypes = new ArrayList<>();

    public classType(String name){
        super(name);
        this.type = TypeCategory.CLASS;
        size = 32;
    }

    public void defineMethod(String name, funType func, position pos){
        localScope.defineMethod(name, func, pos);
    }

    public int addMember(Type memberType){
        memberTypes.add(memberType);
        memberAllocSize += memberType.size;
        return memberTypes.size() - 1;
    }

    @Override public boolean isSame(Type other){
        return other.isNull()||(other.isClass()&&name.equals(((classType)other).name));
    }

}