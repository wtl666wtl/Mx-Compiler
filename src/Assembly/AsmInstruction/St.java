package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;

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
    public void resolveSLImm(int stackLength){}//no SLImm

}
