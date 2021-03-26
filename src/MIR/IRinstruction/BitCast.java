package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ListIterator;

public class BitCast extends BaseInstruction{

    public BaseOperand origin;

    public BitCast(Register rd, Block blk, BaseOperand orign){
        super(rd, blk);
        this.origin = orign;
        orign.appear(this);
        rd.defInst = this;
    }

    @Override
    public String toString() {
        return rd.toString() + " = bitcast " + origin.type.toString()
                + " " + origin.toString() + " to " + rd.type.toString();
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        origin.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register originOperand, BaseOperand newOperand) {
        if(origin == originOperand)origin = newOperand;
    }
}
