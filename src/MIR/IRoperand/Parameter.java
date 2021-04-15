package MIR.IRoperand;

import MIR.IRinstruction.BaseInstruction;
import MIR.IRtype.IRBaseType;

import java.util.HashSet;

public class Parameter extends BaseOperand{

    public String name;
    public HashSet<BaseInstruction> positions = new HashSet<>();

    public Parameter(IRBaseType type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public String toString(){
        return "%" + name;
    }

    @Override
    public void appear(BaseInstruction inst){
        positions.add(inst);
    }

    @Override
    public void deleteAppear(BaseInstruction inst){
        positions.remove(inst);
    }

    @Override
    public BaseOperand inlineCopy() {
        return new Parameter(type, name + "_inlineCopy");
    }
}
