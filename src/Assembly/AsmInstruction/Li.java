package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;
import Assembly.AsmOperand.StackLengthImm;

import java.util.HashSet;

public class Li extends BaseAsmInstruction {

    public Imm val;

    public Li(Reg rd, AsmBlock blk, Imm val){
        super(rd, blk);
        this.val = val;
    }

    @Override
    public String toString(){
        return "li " + rd + ", " + val;
    }

    @Override
    public void resolveSLImm(int stackLength){
        if(val instanceof StackLengthImm)
            val = new Imm(val.val + stackLength);
    }

    @Override
    public HashSet<Reg> defs() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rd);
        return use;
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }

    @Override
    public void changeUse(Reg origin, Reg change) {

    }

}
