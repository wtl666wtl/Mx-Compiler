package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

import java.util.HashSet;

public class RType extends BaseAsmInstruction{

    public Reg rs1, rs2;
    public calType opCode;

    public RType(Reg rd, AsmBlock blk, Reg rs1, Reg rs2, calType opCode){
        super(rd, blk);
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        return opCode + " " + rd + ", " + rs1 + ", " + rs2;
    }

    @Override
    public void resolveSLImm(int stackLength){}

    @Override
    public HashSet<Reg> defs() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rd);
        return use;
    }

    @Override
    public HashSet<Reg> uses() {
        HashSet<Reg> use = new HashSet<>();
        use.add(rs1);
        use.add(rs2);
        return use;
    }

    @Override
    public void changeUse(Reg origin, Reg change) {
        if(rs1 == origin)rs1 = change;
        if(rs2 == origin)rs2 = change;
    }

}
