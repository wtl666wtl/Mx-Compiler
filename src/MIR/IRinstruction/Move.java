package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ListIterator;

public class Move extends BaseInstruction{

    public BaseOperand origin;

    public Move(Register rd, Block blk, BaseOperand origin, boolean appear){
        super(rd, blk);
        this.origin = origin;
        if(appear)origin.appear(this);
    }

    @Override
    public String toString() {
        return "mv " + origin.type.toString() + " " + rd.toString() + " " + origin.toString();
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        origin.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(origin == orignOperand)origin = newOperand;
    }
}
