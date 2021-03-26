package MIR.IRoperand;

import MIR.IRinstruction.BaseInstruction;
import MIR.IRtype.IRBaseType;

import java.util.HashSet;

public class Register extends BaseOperand{

    public String name;
    public IRBaseType type;
    public BaseInstruction defInst;
    public HashSet<BaseInstruction> positions = new HashSet<>();

    public Register(String name, IRBaseType type){
        super(type);
        this.name = name;
        this.type = type;
    }

    public void replaceAllUse(BaseOperand newOperand){
        positions.forEach(inst -> {
            inst.replaceUse(this, newOperand);
            newOperand.appear(inst);
        });
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

}
