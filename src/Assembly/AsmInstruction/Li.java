package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;

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
    public void resolveSLImm(int stackLength){}//no SLImm
}
