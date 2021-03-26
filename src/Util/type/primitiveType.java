package Util.type;

public class primitiveType extends BaseType{

    public primitiveType(String name, TypeCategory type){
        super(name);
        this.type = type;
        if(isInt())size = 32;
        else if(isBool())size = 8;
        else size = -1;
    }

    @Override public boolean isSame(Type other){
        return (isNull()&&(other.isArray()||other.isClass()))||(type==other.type&&other.dim()==0);
    }

}
