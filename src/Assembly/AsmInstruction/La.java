package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.GlobalReg;
import Assembly.AsmOperand.Reg;

public class La extends BaseAsmInstruction{

    public GlobalReg symbol;

    public La(Reg rd, AsmBlock blk, GlobalReg symbol){
        super(rd, blk);
        this.symbol = symbol;
    }

    @Override
    public String toString(){
        return "la " + rd + ", " + symbol;
    }

    @Override
    public void resolveSLImm(int stackLength){}

}
