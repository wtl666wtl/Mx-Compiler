package Assembly.AsmOperand;

public class Param extends Reg{

    public int index, width;

    public Param(int index, int width){
        super();
        this.index = index;
        this.width = width;
    }

    @Override
    public String toString(){
        return "What are you doing???";
    }
}
