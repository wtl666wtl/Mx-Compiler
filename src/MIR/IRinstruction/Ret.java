package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ListIterator;

public class Ret extends BaseInstruction{

    public BaseOperand retVal;

    public Ret(Block blk, BaseOperand retVal){
        super(null, blk);
        this.retVal = retVal;
        if(retVal != null)retVal.appear(this);
    }

    @Override
    public String toString(){
        return "ret " + (retVal == null ? "void" : retVal.type.toString() + " " + retVal.toString());
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        if(retVal != null)retVal.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(retVal == orignOperand)retVal = newOperand;
    }
}