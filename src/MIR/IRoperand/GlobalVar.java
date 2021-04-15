package MIR.IRoperand;

import MIR.IRinstruction.BaseInstruction;
import MIR.IRtype.IRBaseType;

import java.util.HashSet;

public class GlobalVar extends BaseOperand{

    public String name;
    public IRBaseType type;
    public HashSet<BaseInstruction> positions = new HashSet<>();

    public GlobalVar(String name, IRBaseType type){
        super(type);
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString(){
        return "@" + name;
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
        return this;
    }
}
