package Util.type;

public class constructorType extends BaseType{

    public constructorType(){
        super("constructor");
        this.type=TypeCategory.CONSTRUCTOR;
        size = -1;
    }

    @Override public boolean isSame(Type other){
        return other.isConstructor();
    }

}
