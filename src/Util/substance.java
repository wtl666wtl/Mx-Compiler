package Util;

import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.ConstInt;
import Util.type.Type;

public class substance {
    public String name;
    public Type type;

    public boolean isGlobalVar, inClass = false;
    public BaseOperand operand;
    public ConstInt index;

    public  substance(String name, Type type, boolean isGlobalVar){
        this.name = name;
        this.type = type;
        this.isGlobalVar = isGlobalVar;
    }

}
