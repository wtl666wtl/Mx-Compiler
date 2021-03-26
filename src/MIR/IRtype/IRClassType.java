package MIR.IRtype;

import java.util.ArrayList;

public class IRClassType extends IRBaseType{
    public String name;
    public ArrayList<IRBaseType> members = new ArrayList<>();

    public IRClassType(String name) {
        super();
        this.name = name;
        width = 0;
    }

    public void addMember(IRBaseType mem) {
        members.add(mem);
        width += mem.width;
    }

    public int getMemberOffset(int i){
        int offset = 0;
        for (int j = 0; j < i; j++)
            offset += members.get(j).width;

        return offset;
    }

    @Override
    public boolean isSame(IRBaseType other){
        return other instanceof IRClassType && ((IRClassType) other).name.equals(name);
    }

    @Override
    public String toString(){
        return "%struct." + name;
    }

}
