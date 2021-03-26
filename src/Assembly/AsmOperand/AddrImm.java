package Assembly.AsmOperand;

public class AddrImm extends Imm{

    public GlobalReg addrGReg;
    public boolean high;

    public AddrImm(GlobalReg addrGReg, boolean high){
        super(0);
        this.addrGReg = addrGReg;
        this.high = high;
    }

    @Override
    public String toString(){
        return "%" + (high ? "hi(" : "lo(") + addrGReg + ")";
    }

}
