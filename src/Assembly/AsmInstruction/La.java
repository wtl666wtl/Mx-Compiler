package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.GlobalReg;
import Assembly.AsmOperand.Reg;

import java.util.HashSet;

public class La extends BaseAsmInstruction{

    public GlobalReg symbol;

    public La(Reg rd, AsmBlock blk, GlobalReg symbol){
        super(rd, blk);
        this.symbol = symbol;
    }

    @Override
    public String toString(){
        return "la " + rd + ", " + symbol;
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
