package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

public class Mv extends BaseAsmInstruction{

    public Reg rs;
    public Mv(Reg rd, AsmBlock blk, Reg rs){
        super(rd, blk);
        this.rs = rs;
    }

    @Override
    public String toString(){
        return "mv " + rd + ", " + rs;
    }

    @Override
    public void resolveSLImm(int stackLength){}

}
