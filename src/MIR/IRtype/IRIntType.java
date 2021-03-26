package MIR.IRtype;

public class IRIntType extends IRFirstClassType{

    public IRIntType(int width) {
        super();
        this.width = width;
    }

    @Override
    public boolean isSame(IRBaseType other) {
        return other instanceof IRIntType && other.width == width;
    }

    @Override
    public String toString(){
        return "i" + width;
    }
}
