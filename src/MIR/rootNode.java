package MIR;

import MIR.IRoperand.ConstString;
import MIR.IRoperand.GlobalVar;
import MIR.IRoperand.Parameter;
import MIR.IRtype.*;
import Util.type.*;
import java.util.ArrayList;
import java.util.HashMap;

public class rootNode {

    public HashMap<String, Function> builtInFuncs = new HashMap<>();
    public HashMap<String, Function> funcs = new HashMap<>();
    public HashMap<String, IRClassType> newClassTypes = new HashMap<>();
    public ArrayList<GlobalVar> globalVars =new ArrayList<>();
    public HashMap<String, ConstString> constStrings = new HashMap<>(), constStringMap = new HashMap<>();

    public static IRBaseType INT = new IRIntType(32), INT8 = new IRIntType(8),
                            BOOL = new IRBoolType(), VOID = new IRVoidType(),
                            STRING = new IRPointerType(new IRIntType(8), false);

    public rootNode(){
        Function builtIn_print = new Function("builtIn_print");
        builtIn_print.funType = new IRFunctionType(VOID);
        builtIn_print.funType.paramList.add(new Parameter(STRING, "s"));
        builtInFuncs.put("builtIn_print", builtIn_print);

        Function builtIn_println = new Function("builtIn_println");
        builtIn_println.funType = new IRFunctionType(VOID);
        builtIn_println.funType.paramList.add(new Parameter(STRING, "s"));
        builtInFuncs.put("builtIn_println", builtIn_println);

        Function builtIn_printInt = new Function("builtIn_printInt");
        builtIn_printInt.funType = new IRFunctionType(VOID);
        builtIn_printInt.funType.paramList.add(new Parameter(INT, "x"));
        builtInFuncs.put("builtIn_printInt", builtIn_printInt);

        Function builtIn_printlnInt = new Function("builtIn_printlnInt");
        builtIn_printlnInt.funType = new IRFunctionType(VOID);
        builtIn_printlnInt.funType.paramList.add(new Parameter(INT, "x"));
        builtInFuncs.put("builtIn_printlnInt", builtIn_printlnInt);

        Function builtIn_getInt = new Function("builtIn_getInt");
        builtIn_getInt.funType = new IRFunctionType(INT);
        builtInFuncs.put("builtIn_getInt", builtIn_getInt);

        Function builtIn_getString = new Function("builtIn_getString");
        builtIn_getString.funType = new IRFunctionType(STRING);
        builtInFuncs.put("builtIn_getString", builtIn_getString);

        Function builtIn_toString = new Function("builtIn_toString");
        builtIn_toString.funType = new IRFunctionType(STRING);
        builtIn_toString.funType.paramList.add(new Parameter(INT, "x"));
        builtInFuncs.put("builtIn_toString", builtIn_toString);

        Function builtIn_string_length = new Function("builtIn_string_length");
        builtIn_string_length.funType = new IRFunctionType(INT);
        builtIn_string_length.funType.paramList.add(new Parameter(STRING, "s"));
        builtInFuncs.put("builtIn_string_length", builtIn_string_length);

        Function builtIn_string_substring = new Function("builtIn_string_substring");
        builtIn_string_substring.funType = new IRFunctionType(STRING);
        builtIn_string_substring.funType.paramList.add(new Parameter(STRING, "s"));
        builtIn_string_substring.funType.paramList.add(new Parameter(INT, "L"));
        builtIn_string_substring.funType.paramList.add(new Parameter(INT, "R"));
        builtInFuncs.put("builtIn_string_substring", builtIn_string_substring);

        Function builtIn_string_parseInt = new Function("builtIn_string_parseInt");
        builtIn_string_parseInt.funType = new IRFunctionType(INT);
        builtIn_string_parseInt.funType.paramList.add(new Parameter(STRING, "x"));
        builtInFuncs.put("builtIn_string_parseInt", builtIn_string_parseInt);

        Function builtIn_string_ord = new Function("builtIn_string_ord");
        builtIn_string_ord.funType = new IRFunctionType(INT);
        builtIn_string_ord.funType.paramList.add(new Parameter(STRING, "s"));
        builtIn_string_ord.funType.paramList.add(new Parameter(INT, "pos"));
        builtInFuncs.put("builtIn_string_ord", builtIn_string_ord);

        Function builtIn_string_add = new Function("builtIn_string_add");
        builtIn_string_add.funType = new IRFunctionType(STRING);
        builtIn_string_add.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_add.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_add", builtIn_string_add);

        Function builtIn_string_Less = new Function("builtIn_string_Less");
        builtIn_string_Less.funType = new IRFunctionType(INT);
        builtIn_string_Less.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_Less.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_Less", builtIn_string_Less);

        Function builtIn_string_Greater = new Function("builtIn_string_Greater");
        builtIn_string_Greater.funType = new IRFunctionType(INT);
        builtIn_string_Greater.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_Greater.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_Greater", builtIn_string_Greater);

        Function builtIn_string_LessEqual = new Function("builtIn_string_LessEqual");
        builtIn_string_LessEqual.funType = new IRFunctionType(INT);
        builtIn_string_LessEqual.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_LessEqual.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_LessEqual", builtIn_string_LessEqual);

        Function builtIn_string_GreaterEqual = new Function("builtIn_string_GreaterEqual");
        builtIn_string_GreaterEqual.funType = new IRFunctionType(INT);
        builtIn_string_GreaterEqual.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_GreaterEqual.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_GreaterEqual", builtIn_string_GreaterEqual);

        Function builtIn_string_Equal = new Function("builtIn_string_Equal");
        builtIn_string_Equal.funType = new IRFunctionType(INT);
        builtIn_string_Equal.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_Equal.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_Equal", builtIn_string_Equal);

        Function builtIn_string_NotEqual = new Function("builtIn_string_NotEqual");
        builtIn_string_NotEqual.funType = new IRFunctionType(INT);
        builtIn_string_NotEqual.funType.paramList.add(new Parameter(STRING, "a"));
        builtIn_string_NotEqual.funType.paramList.add(new Parameter(STRING, "b"));
        builtInFuncs.put("builtIn_string_NotEqual", builtIn_string_NotEqual);

        Function malloc = new Function("malloc");
        malloc.funType = new IRFunctionType(INT);
        malloc.funType.paramList.add(new Parameter(INT, "a"));
        builtInFuncs.put("malloc", malloc);

        Function init = new Function("__init");
        init.funType= new IRFunctionType(VOID);
        init.outblk = init.inblk;
        init.funcBlocks.add(init.inblk);
        funcs.put("__init", init);
    }

    public IRBaseType solveArray(arrayType type, boolean standardWidth){
        IRBaseType res;
        res = createIRType(type.baseType, standardWidth);
        for(int i = 0; i < type.dim(); i++) res = new IRPointerType(res, false);
        return res;
    }

    public IRBaseType createIRType(Type type, boolean standardWidth){
        if(type.isInt())return rootNode.INT;
        else if(type.isBool()){
            if(standardWidth)return rootNode.INT8;
            return rootNode.BOOL;
        } else if(type.isClass()){
            if(((classType)type).name.equals("string"))return rootNode.STRING;
            else return new IRPointerType(newClassTypes.get(((classType)type).name), false);
        } else if(type.isArray()){
            return solveArray((arrayType)type, standardWidth);
        } else return rootNode.VOID;
    }

    public ConstString addConstString(String name, String realString){
        if(!constStrings.containsKey(name)){
            ConstString tmp = new ConstString(name, realString);
            constStrings.put(name, tmp);
            constStringMap.put(realString, tmp);
            return tmp;
        } return constStrings.get(name);
    }

}
