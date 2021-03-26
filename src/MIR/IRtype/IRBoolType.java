package MIR.IRtype;

public class IRBoolType extends IRFirstClassType{

    public IRBoolType() {
        super();
        width = 8;
    }

    @Override
    public boolean isSame(IRBaseType other) {
        return other instanceof IRBoolType;
    }

    @Override
    public String toString(){
        return "i1";
    }
}
