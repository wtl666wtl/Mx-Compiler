package Assembly.AsmOperand;

import Assembly.AsmBlock;
import org.antlr.v4.codegen.model.PlusBlock;

import java.util.HashSet;

public class VirtualReg extends Reg{

    public int index, width;
    public boolean usedTag = true;
    public boolean nextTag = false;
    public int useTime = 0;
    public HashSet<AsmBlock> appearBlks = new HashSet<>();

    public VirtualReg(int index, int width){
        super();
        this.index = index;
        this.width = width;
    }

    @Override
    public String toString(){
        return color == null ? index + "_VR" : color.toString();
    }
}
