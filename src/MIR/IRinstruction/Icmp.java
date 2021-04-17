package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;

public class Icmp extends BaseInstruction{

    public enum IcmpOpType{
        eq, ne, sgt, sge, slt, sle
    }
    public IcmpOpType opCode;
    public BaseOperand arg1, arg2;

    public Icmp(Register rd, Block blk, IcmpOpType opCode, BaseOperand arg1, BaseOperand arg2){
        super(rd, blk);
        this.opCode = opCode;
        this.arg1 = arg1;
        this.arg2 = arg2;

        rd.defInst = this;
        arg1.appear(this);
        arg2.appear(this);
    }

    @Override
    public String toString(){
        return rd.toString() + " = icmp " + opCode.toString() + " "
                + rd.type.toString() + " " + arg1.toString() + ", " + arg2.toString();
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        arg1.deleteAppear(this);
        arg2.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(arg1 == orignOperand)arg1 = newOperand;
        if(arg2 == orignOperand)arg2 = newOperand;
    }

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new Icmp((Register) a.get(rd), newblk, opCode, a.get(arg1), a.get(arg2)));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        HashSet<BaseOperand> use = new HashSet<>();
        use.add(arg1);use.add(arg2);
        return use;
    }

}
