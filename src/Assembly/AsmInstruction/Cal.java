package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmFunction;
import Assembly.AsmOperand.Reg;
import Assembly.AsmRootNode;

import java.util.HashSet;

public class Cal extends BaseAsmInstruction{

    public AsmFunction callee;
    public AsmRootNode AsmRt;

    public Cal(AsmBlock blk, AsmRootNode AsmRt, AsmFunction callee){
        super(null, blk);
        this.AsmRt = AsmRt;
        this.callee = callee;
    }

    @Override
    public String toString(){
        return "call " + callee.name;
    }

    @Override
    public void resolveSLImm(int stackLength){}

    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>(AsmRt.callerRegs);
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> use = new HashSet<>();
        for(int i = 0; i < Integer.min(callee.params.size(), 8); i++)
            use.add(AsmRt.phyRegs.get(10 + 8));
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {

    }

}
