package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;
import Assembly.AsmOperand.VirtualReg;

import java.util.ArrayList;
import java.util.HashSet;

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
    public ArrayList<BaseAsmInstruction> preAdds = new ArrayList<>();
    public BaseAsmInstruction sucAdd1 = null;
    public boolean disableForImm = false;
    public ArrayList<BaseAsmInstruction> instForImm = new ArrayList<>();
    public ArrayList<BaseAsmInstruction> instForCal = new ArrayList<>();

    public BaseAsmInstruction(Reg rd, AsmBlock blk){
        this.rd = rd;
        this.blk = blk;
    }

    public void changeRd(Reg origin, Reg change){
        if(rd == origin)rd = change;
    }

    public abstract String toString();

    public abstract void resolveSLImm(int stackLength);

    public abstract HashSet<Reg> uses();

    public abstract HashSet<Reg> defs();

    public abstract void changeUse(Reg origin, Reg change);

}
