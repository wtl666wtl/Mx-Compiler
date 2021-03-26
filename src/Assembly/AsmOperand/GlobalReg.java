package Assembly.AsmOperand;

public class GlobalReg extends Reg{

    public int pointToWidth;
    public String name;

    public GlobalReg(String name, int pointToWidth){
        super();
        this.name = name;
        this.pointToWidth = pointToWidth;
    }

    @Override
    public String toString(){
        return name;
    }
}
