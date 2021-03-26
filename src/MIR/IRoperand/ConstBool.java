package MIR.IRoperand;

import MIR.IRtype.IRBoolType;

public class ConstBool extends Constant{

    public boolean val;

    public ConstBool(boolean val){
        super(new IRBoolType());
        this.val = val;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof ConstBool && val == ((ConstBool)other).val;
    }

    @Override
    public String toString(){
        return val==true ? "1" : "0";
    }
}
