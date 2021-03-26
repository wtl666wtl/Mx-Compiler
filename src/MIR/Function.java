package MIR;

import MIR.IRoperand.Parameter;
import MIR.IRoperand.Register;
import MIR.IRtype.IRFunctionType;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class Function {

    public String name;
    public IRFunctionType funType;
    public Parameter classPtr;
    public HashSet<Function> callFuncs = new HashSet<>();
    public HashSet<Register> varPtrs =new HashSet<>();
    public Block inblk = new Block("func_in"), outblk;
    public LinkedHashSet<Block> funcBlocks = new LinkedHashSet<>();

    public Function(String name){
        this.name = name;
        funType = new IRFunctionType();
    }

    public void setClassPtr(Parameter classPtr){
        this.classPtr = classPtr;
        funType.paramList.add(classPtr);
    }

}
