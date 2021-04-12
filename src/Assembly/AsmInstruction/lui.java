package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;

import java.util.HashSet;

public class lui extends BaseAsmInstruction{//using only before Store GlobalVar

    public Imm addr;

    public lui(Reg rd, AsmBlock blk, Imm addr){
        super(rd, blk);
        this.addr = addr;
    }

    @Override
    public String toString(){
        return "lui " + rd.toString() + ", " + addr.toString();
    }

    @Override
    public void resolveSLImm(int stackLength){}

    @Override
    public HashSet<Reg> defs() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rd);
        return use;
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }

    @Override
    public void changeUse(Reg origin, Reg change) {

    }

}
