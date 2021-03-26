package MIR.IRtype;

public class IRArrayType extends IRBaseType{

    public int num;
    public IRBaseType type;

    public IRArrayType(int num, IRBaseType type) {
        super();
        this.num = num;
        this.type = type;
        width = type.width;
    }

    @Override
    public boolean isSame(IRBaseType other){
        return other instanceof IRArrayType && ((IRArrayType) other).type.isSame(type) ||
                other instanceof IRPointerType && ( ((IRPointerType)other).pointTo instanceof IRVoidType ||
                        ((IRPointerType)other).pointTo.isSame(type) );
    }

    @Override
    public String toString(){
        return "[ " + num + " x "+ type.toString() + " ]";
    }
}
