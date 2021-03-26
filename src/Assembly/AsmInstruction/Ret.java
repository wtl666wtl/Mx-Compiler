package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmRootNode;

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
}
