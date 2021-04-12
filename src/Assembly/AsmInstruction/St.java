package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.GlobalReg;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;
import Assembly.AsmOperand.StackLengthImm;

import java.util.HashSet;

public class St extends BaseAsmInstruction{

    public Reg addr, val;
    public Imm offset;
    public int width;

    public St(AsmBlock blk, Reg addr, Reg val, Imm offset, int width){
        super(null, blk);
        this.addr = addr;
        this.val = val;
        this.offset = offset;
        this.width = width;
    }

    @Override
    public String toString(){
        String store;
        if(width == 1)store = "sb ";
        else if(width == 4)store = "sw ";
        else store = "sh ";
        return store + val + ", " + offset + "(" + addr + ")";
    }

    @Override
    public void resolveSLImm(int stackLength){
        if(offset instanceof StackLengthImm)
            offset = new Imm(offset.val + stackLength);
    }

    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> use = new HashSet<>();
        if(!(addr instanceof GlobalReg))use.add(addr);
        use.add(val);
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {
        if(val == origin)val = change;
        if(addr == origin)addr = change;
    }

}
