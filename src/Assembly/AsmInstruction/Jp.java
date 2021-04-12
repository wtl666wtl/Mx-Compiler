package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

import java.util.HashSet;

public class Jp extends BaseAsmInstruction{

    public AsmBlock destBlk;

    public Jp(AsmBlock blk, AsmBlock destBlk){
        super(null, blk);
        this.destBlk = destBlk;
    }

    @Override
    public String toString(){
        return "j " + destBlk;
    }

    @Override
    public void resolveSLImm(int stackLength){}

    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Reg> uses() {
        return new HashSet<>();
    }

    @Override
    public void changeUse(Reg origin, Reg change) {

    }

}
