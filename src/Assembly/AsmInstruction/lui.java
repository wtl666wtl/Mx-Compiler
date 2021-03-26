package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;

public class lui extends BaseAsmInstruction{//using before Store GlobalVar

    public Imm addr;

    public lui(Reg rd, AsmBlock blk, Imm addr){
        super(rd, blk);
        this.addr = addr;
    }

    @Override
    public String toString(){
        return "lui " + rd.toString() + ", " + addr.toString();
    }

    @Override
    public void resolveSLImm(int stackLength){}
}
