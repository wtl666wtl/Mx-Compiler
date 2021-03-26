package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;
import Assembly.AsmOperand.StackLengthImm;

public class IType extends BaseAsmInstruction{

    public Reg rs;
    public Imm imm;
    public calType opCode;

    public IType(Reg rd, AsmBlock blk, Reg rs, Imm imm, calType opCode){
        super(rd, blk);
        this.imm = imm;
        this.rs = rs;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        return opCode + "i " + rd + ", " + rs + ", " + imm.val;
    }

    @Override
    public void resolveSLImm(int stackLength){
        if(imm instanceof StackLengthImm)
            imm = new Imm(stackLength * ((StackLengthImm)imm).order + imm.val);
    }

}
