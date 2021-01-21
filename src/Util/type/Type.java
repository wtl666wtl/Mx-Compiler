package Util.type;

import java.util.HashMap;

abstract public class Type{
    public enum TypeCategory{
        NULL, INT, BOOL, ARRAY, CLASS, CONSTRUCTOR, VOID, FUN
    }//string -> CLASS

    public TypeCategory type;

    abstract public boolean isSame(Type other);

    abstract public int dim();

    public boolean isVoid() {
        return type == TypeCategory.VOID;
    }

    public boolean isNull(){
        return type == TypeCategory.NULL;
    }

    public boolean isBool(){
        return type == TypeCategory.BOOL;
    }

    public boolean isInt() {
        return type == TypeCategory.INT;
    }

    public boolean isClass() {
        return type == TypeCategory.CLASS;
    }

    public boolean isFunc() {
        return type == TypeCategory.FUN;
    }

    public boolean isArray() {
        return dim() != 0;
    }

    public boolean isConstructor() {
        return type == TypeCategory.CONSTRUCTOR;
    }

}
