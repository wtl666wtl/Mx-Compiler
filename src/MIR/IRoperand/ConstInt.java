package MIR.IRoperand;

import MIR.IRtype.IRIntType;

public class ConstInt extends Constant{

    public int val, width;

    public ConstInt(int val, int width){
        super(new IRIntType(width));
        this.width = width;
        this.val = val;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof ConstInt && val == ((ConstInt)other).val && type.isSame(((ConstInt)other).type);
    }

    @Override
    public String toString(){
        return ""+val;
    }
}
