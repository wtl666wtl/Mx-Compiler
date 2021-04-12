package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;
import Assembly.AsmRootNode;

import java.util.HashSet;

public class Ret extends BaseAsmInstruction{

    public AsmRootNode AsmRt;

    public Ret(AsmBlock blk, AsmRootNode AsmRt){
        super(null, blk);
        this.AsmRt = AsmRt;
    }

    @Override
    public String toString(){
        return "ret";
    }

    @Override
    public void resolveSLImm(int stackLength){}

    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> use = new HashSet<>();
        use.add(AsmRt.phyRegs.get(1));
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {

    }

}
