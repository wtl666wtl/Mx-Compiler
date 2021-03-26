package Assembly.AsmOperand;

public class VirtualReg extends Reg{

    public int index, width;

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
