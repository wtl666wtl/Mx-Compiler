package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ListIterator;

public class Br extends BaseInstruction{

    public BaseOperand cond;
    public Block iftrue, iffalse;
    //only use iftrue when unconditional jump

    public Br(Block blk, BaseOperand cond, Block iftrue, Block iffalse){
        super(null, blk);
        this.cond = cond;
        this.iftrue = iftrue;
        this.iffalse = iffalse;
        if(cond != null)cond.appear(this);
    }

    @Override
    public String toString(){
        if(cond == null)return "br label " + iftrue.name;
        return "br " + cond.type.toString() + cond.toString()
                + ", label %" + iftrue.name + ", label %" + iffalse.name;
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        cond.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(cond == orignOperand)cond = newOperand;
    }
}
