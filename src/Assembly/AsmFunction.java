package Assembly;

import Assembly.AsmOperand.Reg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class AsmFunction {

    public String name;
    public AsmBlock inblk, outblk;
    public int vregCounter = 0;
    public int paramStSize = 0;
    public LinkedHashSet<AsmBlock> blks = new LinkedHashSet<>();
    public ArrayList<Reg> params = new ArrayList<>();

    public AsmFunction(String name, AsmBlock inblk, AsmBlock outblk){
        this.name = name;
        this.inblk = inblk;
        this.outblk = outblk;
    }

}
