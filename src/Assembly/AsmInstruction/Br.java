package Assembly.AsmInstruction;

import Assembly.AsmBlock;
import Assembly.AsmOperand.Reg;

import java.util.HashSet;

public class Br extends BaseAsmInstruction{

    public Reg rs1, rs2;
    public cmpType opCode;
    public AsmBlock destblk;

    public Br(AsmBlock blk, Reg rs1, Reg rs2, cmpType opCode, AsmBlock destblk){
        super(null, blk);
        this.destblk = destblk;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.opCode = opCode;
    }

    @Override
    public String toString(){
        return "b" + opCode + " " + rs1 + ", " + rs2 + ", " + destblk;
    }

    @Override
    public void resolveSLImm(int stackLength){}

    @Override
    public HashSet<Reg> defs() {
        return new HashSet<>();
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
