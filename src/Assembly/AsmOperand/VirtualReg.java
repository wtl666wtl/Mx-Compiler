package Assembly.AsmOperand;

import org.antlr.v4.codegen.model.PlusBlock;

public class VirtualReg extends Reg{

    public int index, width;
    public boolean usedTag = true;

    public VirtualReg(int index, int width){
        super();
        this.index = index;
        this.width = width;
    }

    @Override
    public String toString(){
        return "What are you doing???";
    }
}
