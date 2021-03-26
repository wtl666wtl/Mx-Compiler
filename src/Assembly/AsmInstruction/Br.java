package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

public class Br extends BaseAsmInstruction{

    public Reg rs1, rs2;
    public cmpType opCode;
    AsmBlock destblk;

    public Br(AsmBlock blk, Reg lhs, Reg rhs, cmpType opCode, AsmBlock destblk){
        super(null, blk);
        this.destblk = destblk;
        this.rs1 = lhs;
        this.rs2 = rhs;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        return "b" + opCode + " " + rs1 + ", " + rs2 + ", " + destblk;
    }

    @Override
    public void resolveSLImm(int stackLength){}

}
