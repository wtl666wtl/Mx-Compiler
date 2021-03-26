package Util.type;

public class arrayType extends Type{
    public BaseType baseType;
    public int dim;

    public arrayType(BaseType baseType, int dim){
        this.baseType=baseType;
        this.dim = dim;
        this.type = TypeCategory.ARRAY;
        size = 32;
    }

    public arrayType(Type lowerType){
        this.baseType = ((arrayType)lowerType).baseType;
        this.dim = lowerType.dim() - 1;
        this.type = TypeCategory.ARRAY;
    }

    @Override public boolean isSame(Type other){
        return other.isNull()||(dim==other.dim()&&baseType.isSame(((arrayType)other).baseType));
    }

    @Override public int dim(){
        return dim;
    }
}
