package Assembly.AsmOperand;

public class Imm extends BaseAsmOperand{

    public int val;

    public Imm(int val){
        this.val = val;
    }

    @Override
    public String toString(){
        return "" + val;
    }
}
