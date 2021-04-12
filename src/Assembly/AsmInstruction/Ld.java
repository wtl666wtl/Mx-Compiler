package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.GlobalReg;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;
import Assembly.AsmOperand.StackLengthImm;

import java.util.HashSet;

public class Ld extends BaseAsmInstruction{

    public Reg addr;
    public int width;
    public Imm offset;

    public Ld(Reg rd, AsmBlock blk, Reg addr, Imm offset, int width){
        super(rd, blk);
        this.addr = addr;
        this.width = width;
        this.offset = offset;
    }

    @Override
    public String toString(){
        String load;
        if(width == 1)load = "lb ";
        else if(width == 4)load = "lw ";
        else load = "lh ";
        return load + rd + ", " + (addr instanceof GlobalReg ? addr : offset + "(" + addr + ")");
    }

    @Override
    public void resolveSLImm(int stackLength){
        if(offset instanceof StackLengthImm)
            offset = new Imm(stackLength * ((StackLengthImm)offset).order + offset.val);
    }

    @Override
    public HashSet<Reg> defs() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rd);
        return use;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> use = new HashSet<>();
        if(!(addr instanceof GlobalReg))use.add(addr);
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {
        if(addr == origin)addr = change;
    }

}
