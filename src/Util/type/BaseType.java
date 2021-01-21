package Util.type;

abstract public class BaseType extends Type{
    public String name;

    public BaseType(String name){
        super();
        this.name = name;
    }

    @Override public int dim(){
        return 0;
    }

}
