package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRPointerType;

import java.util.HashMap;
import java.util.ListIterator;

public class Load extends BaseInstruction{

    public BaseOperand addr;
    public ListIterator<BaseInstruction> p = null;

    public Load(Register rd, Block blk, BaseOperand addr){
        super(rd, blk);
        rd.defInst = this;
        this.addr = addr;
        addr.appear(this);
    }

    @Override
    public String toString(){
        return rd.toString()+ " = load " + rd.type.toString()
                + ", " + addr.type.toString() + " " + addr.toString()
                + ", align " + rd.type.width / 8;
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        addr.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(addr == orignOperand)addr = newOperand;
    }

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new Load((Register) a.get(rd), newblk, a.get(addr)));
    }
}
