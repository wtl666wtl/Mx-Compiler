package MIR.IRtype;

import java.util.ArrayList;

import MIR.IRoperand.*;

public class IRFunctionType extends IRBaseType{

    public IRBaseType retType;
    public ArrayList<Parameter> paramList = new ArrayList<>();

    public IRFunctionType(){}
    public IRFunctionType(IRBaseType retType){
        this.retType = retType;
    }

    @Override
    public boolean isSame(IRBaseType other){
        return false;
    }

    @Override
    public String toString(){
        return "";
    }
}
