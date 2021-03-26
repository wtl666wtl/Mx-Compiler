package MIR.IRoperand;

import MIR.IRtype.*;

public class ConstString extends Constant{

    public String name;
    public String val;

    public ConstString(String name, String val){
        super(new IRArrayType(val.length(), new IRIntType(8)));
        this.name = name;
        this.val = val;
    }

    public String IRstring() {
        String IRval = val.replace("\\", "\\5C");
        IRval = IRval.replace("\n", "\\0A");
        IRval = IRval.replace("\0", "\\00");
        IRval = IRval.replace("\"", "\\22");
        IRval = IRval.replace("\t", "\\09");
        return IRval;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof ConstString && val.equals(((ConstString) other).val);
    }

    @Override
    public String toString(){
        return "@"+name;
    }

}
