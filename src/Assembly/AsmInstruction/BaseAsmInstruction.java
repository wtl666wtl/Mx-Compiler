package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

public abstract class BaseAsmInstruction {

    public enum calType {
        add, sub, slt, xor, or, and, sll, srl, sra, mul, div, rem
    }
    public enum cmpType {
        eq, ne, le, ge, lt, gt
    }

    public Reg rd;
    public AsmBlock blk;
    public BaseAsmInstruction preAdd1 = null, preAdd2 = null;
    public BaseAsmInstruction sucAdd1 = null;

    public BaseAsmInstruction(Reg rd, AsmBlock blk){
        this.rd = rd;
        this.blk = blk;
    }

    public abstract String toString();

    public abstract void resolveSLImm(int stackLength);
}
