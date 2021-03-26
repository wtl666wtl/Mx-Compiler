package MIR.IRoperand;

import MIR.IRinstruction.BaseInstruction;
import MIR.IRtype.IRBaseType;

abstract public class Constant extends BaseOperand{

    public Constant(IRBaseType type){
        super(type);
    }

    @Override
    abstract public boolean equals(Object other);

    @Override
    public void appear(BaseInstruction inst){

    }

    @Override
    public void deleteAppear(BaseInstruction inst) {

    }
}
