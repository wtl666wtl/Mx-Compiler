package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

public class Bz extends BaseAsmInstruction{

    public Reg rs;
    public cmpType opCode;
    AsmBlock destblk;

    public Bz(AsmBlock blk, Reg rs, BaseAsmInstruction.cmpType opCode, AsmBlock destblk){
        super(null, blk);
        this.destblk = destblk;
        this.rs = rs;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        return "b" + opCode + "z " + rs + ", " + destblk;
    }

    @Override
    public void resolveSLImm(int stackLength){}

}
