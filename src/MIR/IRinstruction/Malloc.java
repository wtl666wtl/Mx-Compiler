package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.HashSet;
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

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new Malloc((Register) a.get(rd), newblk, a.get(length)));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        HashSet<BaseOperand> use = new HashSet<>();
        use.add(length);
        return use;
    }

    @Override
    public boolean isSame(BaseInstruction it) {
        return false;
    }
}
