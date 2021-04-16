package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Imm;
import Assembly.AsmOperand.Reg;
import Assembly.AsmOperand.StackLengthImm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class IType extends BaseAsmInstruction{

    public Reg rs;
    public Imm imm;
    public calType opCode;

    public IType(Reg rd, AsmBlock blk, Reg rs, Imm imm, calType opCode){
        super(rd, blk);
        this.imm = imm;
        this.rs = rs;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        if(opCode == calType.seq || opCode == calType.sne)
            return opCode + "z " + rd + ", " + rs;
        return opCode + "i " + rd + ", " + rs + ", " + imm.val;
    }

    @Override
    public void resolveSLImm(int stackLength){
        if(imm instanceof StackLengthImm)
            imm = new Imm(stackLength * ((StackLengthImm)imm).order + imm.val);
    }

    @Override
    public HashSet<Reg> defs() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rd);
        return use;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rs);
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {
        if(rs == origin)rs = change;
    }

}
