package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.ListIterator;

public class Zext extends BaseInstruction{

    public BaseOperand orign;

    public Zext(Register rd, Block blk, BaseOperand orign){
        super(rd, blk);
        this.orign = orign;
        orign.appear(this);
        rd.defInst = this;
    }

    @Override
    public String toString() {
        return rd.toString() + " = zext " + orign.type.toString()
                + " " + orign.toString() + " to " + rd.type.toString();
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        orign.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(orign == orignOperand)orign = newOperand;
    }

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new Zext((Register) a.get(rd), newblk, a.get(orign)));
    }
}
