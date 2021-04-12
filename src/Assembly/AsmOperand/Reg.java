package Assembly.AsmOperand;

import Assembly.AsmInstruction.Mv;

import java.util.HashSet;

public abstract class Reg extends BaseAsmOperand{

    public int degree = 0;
    public double weight = 0;
    public Reg alias = null;
    public PhyReg color;
    public Imm stackOffset = null;
    public HashSet<Reg> edgeSet = new HashSet<>();
    public HashSet<Mv> MvSet = new HashSet<>();

    public Reg(){
        super();
    }

    public void clear(){
        weight = 0;
        alias = null;
        degree = 0;
        color = null;
        edgeSet.clear();
        MvSet.clear();
    }
}
