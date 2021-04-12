package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

import java.util.HashSet;

public class Mv extends BaseAsmInstruction{

    public Reg rs;
    public Mv(Reg rd, AsmBlock blk, Reg rs){
        super(rd, blk);
        this.rs = rs;
    }

    @Override
    public String toString(){
        return "mv " + rd + ", " + rs;
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
        HashSet<Reg> use = new HashSet<>();
        use.add(rs);
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {
        if(rs == origin)rs = change;
    }

}
