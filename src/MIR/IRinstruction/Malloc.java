package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ListIterator;

public class Malloc extends BaseInstruction{

    public BaseOperand length;

    public Malloc(Register rd, Block blk, BaseOperand length){
        super(rd, blk);
        this.length = length;
        length.appear(this);
        rd.defInst = this;
    }

    @Override
    public String toString() {
        return rd.toString() + " = call noalias i8* @malloc("
                + length.type.toString() + " " + length.toString() + ")";
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        length.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(length == orignOperand)length = newOperand;
    }
}
