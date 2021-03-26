package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmFunction;
import Assembly.AsmRootNode;

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

}
