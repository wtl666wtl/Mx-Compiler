/*package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.Register;
import MIR.IRtype.IRPointerType;

public class Alloca extends BaseInstruction {

    public Alloca(Register rd, Block blk){
        super(rd, blk);
        rd.defInst = this;
    }

    @Override
    public String toString(){
        return rd.toString()+ " = alloca "
                + ((IRPointerType)(rd.type)).pointTo.toString()
                + ", align " + ((IRPointerType)(rd.type)).pointTo.width / 8;
    }

}*/
