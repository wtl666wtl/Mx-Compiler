package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.*;
import MIR.IRtype.IRPointerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;

public class Store extends BaseInstruction{

    public BaseOperand addr;
    public BaseOperand storeVal;

    public Store(Block blk, BaseOperand addr, BaseOperand storeVal){
        super(null, blk);
        this.addr = addr;
        this.storeVal = storeVal;
        addr.appear(this);
        storeVal.appear(this);
    }

    @Override
    public String toString(){
        String t = storeVal instanceof ConstNull ? ((IRPointerType)storeVal.type).pointTo.toString() : storeVal.type.toString();
        //Maybe easier
        return "store " + t + " " + storeVal.toString()
                + ", " + addr.type.toString() + " " + addr.toString()
                + ", align " + storeVal.type.width / 8;
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        addr.deleteAppear(this);
        storeVal.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(addr == orignOperand)addr = newOperand;
        if(storeVal == orignOperand)storeVal = newOperand;
    }

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new Store(newblk, a.get(addr), a.get(storeVal)));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        HashSet<BaseOperand> use = new HashSet<>();
        use.add(storeVal);use.add(addr);
        return use;
    }

}
