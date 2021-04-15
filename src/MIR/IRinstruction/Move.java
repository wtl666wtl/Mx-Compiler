package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;
import Util.error.internalError;
import Util.position;

import java.util.HashMap;
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

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        throw new internalError("inline a Mv inst", new position(0, 0));
    }

}
