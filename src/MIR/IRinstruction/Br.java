package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.HashSet;
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
        if(cond != null)cond.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(cond == orignOperand)cond = newOperand;
    }

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        if(cond == null)newblk.addTerminator(new Br(newblk, null, a.get(iftrue), null));
        else newblk.addTerminator(new Br(newblk, a.get(cond), a.get(iftrue), a.get(iffalse)));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        HashSet<BaseOperand> use = new HashSet<>();
        if(cond != null)use.add(cond);
        return use;
    }

    @Override
    public boolean isSame(BaseInstruction it) {
        return false;
    }
}
