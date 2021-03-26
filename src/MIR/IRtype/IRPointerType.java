package MIR.IRtype;

import com.sun.jdi.VoidType;

public class IRPointerType extends IRFirstClassType{

    public IRBaseType pointTo;

    public IRPointerType(IRBaseType pointTo, boolean MMflag) {
        super();
        this.pointTo = pointTo;
        this.MMflag = MMflag;
        dim = pointTo.dim + 1;
        width = 32;
    }

    @Override
    public boolean isSame(IRBaseType other) {
        return ( other instanceof IRPointerType && ( ((IRPointerType)other).pointTo instanceof IRVoidType
                || ((IRPointerType)other).pointTo.isSame(pointTo) ) ) ||
                ( other instanceof IRArrayType && other.isSame(this) );
    }

    @Override
    public String toString(){
        if(pointTo instanceof VoidType)return "";
        return pointTo.toString() + "*";
    }
}
