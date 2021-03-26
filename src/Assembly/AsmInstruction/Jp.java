package Assembly.AsmInstruction;

import Assembly.AsmBlock;

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
}
