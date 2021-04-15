package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.ListIterator;

abstract public class BaseInstruction {

    public Register rd;
    public Block blk;
    public boolean deleteFlag = false;

    public BaseInstruction(Register rd, Block blk){
        this.blk = blk;
        this.rd = rd;
    }

    public abstract void replaceUse(Register orignOperand, BaseOperand newOperand);

    public abstract void deleteSelf(boolean flag);

    public abstract String toString();

    public abstract void inlineCopy(Block newblk, Function func, inlineCorrespond a);

}
