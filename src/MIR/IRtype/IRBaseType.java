package MIR.IRtype;

abstract public class IRBaseType {

    public IRBaseType(){}

    public int width;

    public int dim = 0;

    public boolean MMflag = false;

    public abstract boolean isSame(IRBaseType other);

    public abstract String toString();
}
