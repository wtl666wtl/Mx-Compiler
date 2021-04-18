package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.HashSet;
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

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new BitCast((Register) a.get(rd), newblk, a.get(origin)));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        HashSet<BaseOperand> use = new HashSet<>();
        use.add(origin);
        return use;
    }

    @Override
    public boolean isSame(BaseInstruction it) {
        if(it instanceof BitCast){
            BitCast i = (BitCast) it;
            return i.origin.equals(origin) && i.rd.type.isSame(rd.type);
        }
        return false;
    }

}
