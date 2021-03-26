package MIR.IRoperand;

import MIR.IRinstruction.BaseInstruction;
import MIR.IRtype.IRBaseType;

abstract public class BaseOperand {

    public IRBaseType type;

    public BaseOperand(IRBaseType type) {
        this.type = type;
    }

    abstract public String toString();

    abstract public void appear(BaseInstruction inst);

    abstract public void deleteAppear(BaseInstruction inst);
}
