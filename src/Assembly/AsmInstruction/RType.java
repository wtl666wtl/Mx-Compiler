package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

public class RType extends BaseAsmInstruction{

    public Reg rs1, rs2;
    public calType opCode;

    public RType(Reg rd, AsmBlock blk, Reg rs1, Reg rs2, calType opCode){
        super(rd, blk);
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        return opCode + " " + rd + ", " + rs1 + ", " + rs2;
    }

    @Override
    public void resolveSLImm(int stackLength){}

}
