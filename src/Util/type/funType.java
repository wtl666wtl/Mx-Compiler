package Util.type;

import AST.funNode;
import MIR.Function;
import Util.position;
import Util.error.internalError;
import Util.scope.funScope;
import Util.substance;

public class funType extends BaseType{
    public String funName;
    public Type funType;
    public funScope localScope;
    public boolean isMethod;
    public Function IRFunc;

    public funType(String name){
        super("funType" + name);
        this.funName = name;
        this.type = TypeCategory.FUN;
        this.isMethod = false;
        size = -1;
    }

    public void addParameter(substance x, position pos){
        localScope.addParameter(x, pos);
    }

    @Override public int dim(){
        throw new internalError("call dim of a function", new position(0, 0));
    }

    @Override public boolean isSame(Type other){
        throw new internalError("call isSame of a function", new position(0, 0));
    }
}
