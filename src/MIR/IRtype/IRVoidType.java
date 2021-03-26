package MIR.IRtype;

public class IRVoidType extends IRBaseType{

    public IRVoidType() {
        super();
        width = -1;
    }

    @Override
    public boolean isSame(IRBaseType other) {
        return other instanceof IRVoidType;
    }

    @Override
    public String toString(){
        return "void";
    }

}
