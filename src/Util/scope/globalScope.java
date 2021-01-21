package Util.scope;

import Util.error.semanticError;
import Util.position;
import Util.type.*;
import AST.typeNode;
import Util.substance;

import java.util.HashMap;

public class globalScope extends Scope {
    public HashMap<String, Type> types = new HashMap<>();

    public primitiveType intType = new primitiveType("int", Type.TypeCategory.INT);
    public primitiveType boolType = new primitiveType("bool", Type.TypeCategory.BOOL);
    public primitiveType voidType = new primitiveType("void", Type.TypeCategory.VOID);
    public primitiveType nullType = new primitiveType("null", Type.TypeCategory.NULL);

    public globalScope() {
        super(null);

        types.put("int",intType);
        types.put("bool",boolType);
        types.put("void",voidType);
        types.put("null",nullType);

        classType stringType = new classType("string");
        stringType.localScope = new Scope(this);
        position pos = new position(0,0);
        funType tmp;

        tmp=new funType("length");
        tmp.localScope=(new funScope(this));
        tmp.funType=intType;
        stringType.defineMethod("length",tmp,pos);

        tmp=new funType("substring");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("left",intType),pos);
        tmp.addParameter(new substance("right",intType),pos);
        tmp.funType=stringType;
        stringType.defineMethod("substring",tmp,pos);

        tmp=new funType("parseInt");
        tmp.localScope=(new funScope(this));
        tmp.funType=intType;
        stringType.defineMethod("parseInt",tmp,pos);

        tmp=new funType("ord");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("pos",intType),pos);
        tmp.funType=intType;
        stringType.defineMethod("ord",tmp,pos);

        types.put("string",stringType);

        tmp=new funType("print");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("str",stringType),pos);
        tmp.funType=voidType;
        defineMethod("print",tmp,pos);

        tmp=new funType("println");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("str",stringType),pos);
        tmp.funType=voidType;
        defineMethod("println",tmp,pos);

        tmp=new funType("printInt");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("n",intType),pos);
        tmp.funType=voidType;
        defineMethod("printInt",tmp,pos);

        tmp=new funType("printlnInt");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("n",intType),pos);
        tmp.funType=voidType;
        defineMethod("printlnInt",tmp,pos);

        tmp=new funType("getString");
        tmp.localScope=(new funScope(this));
        tmp.funType=stringType;
        defineMethod("getString",tmp,pos);

        tmp=new funType("getInt");
        tmp.localScope=(new funScope(this));
        tmp.funType=intType;
        defineMethod("getInt",tmp,pos);

        tmp=new funType("toString");
        tmp.localScope=(new funScope(this));
        tmp.addParameter(new substance("n",intType),pos);
        tmp.funType=stringType;
        defineMethod("toString",tmp,pos);

        tmp=new funType("size");
        tmp.localScope=(new funScope(this));
        tmp.funType=intType;
        defineMethod("size",tmp,pos);
    }

    public void defineClass(String name, Type t, position pos){
        if(types.containsKey(name)) throw new semanticError("redefine",pos);
        if(containsMember(name, false)) throw new semanticError("same name to a variable",pos);
        types.put(name, t);
    }

    public boolean hasType(String typeName) {
        return types.containsKey(typeName);
    }

    public Type makeType(typeNode node){
        if(node.dim != 0) return new arrayType((BaseType)getTypeFromName(node.baseTypeName,node.pos),node.dim);
        else return getTypeFromName(node.baseTypeName,node.pos);
    }

    public Type getTypeFromName(String name, position pos) {
        if (types.containsKey(name)) return types.get(name);
        throw new semanticError("undefine", pos);
    }
}
