package MIR.IRoperand;

import MIR.IRtype.*;

public class ConstNull extends Constant{

    public ConstNull() {
        super(new IRPointerType(new IRVoidType(), false));
    }

    @Override
    public boolean equals(Object other){
        return other instanceof ConstNull;
    }

    @Override
    public String toString(){
        return "null";
    }
}
