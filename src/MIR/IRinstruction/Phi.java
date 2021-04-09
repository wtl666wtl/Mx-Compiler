package MIR.IRinstruction;

import Assembly.AsmOperand.Reg;
import MIR.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ArrayList;
import java.util.ListIterator;

public class Phi extends BaseInstruction{

    public phiInfo myInfo;

    public Phi(Register rd, Block blk, phiInfo myInfo){
        super(rd, blk);
        this.myInfo = myInfo;
        rd.defInst = this;
        myInfo.vals.forEach(vd -> vd.appear(this));
    }

    public void deleteBlock(Block deleteblk){
        for(int i= 0; i < myInfo.blks.size(); i++){
            if(myInfo.blks.get(i) == deleteblk){
                myInfo.delete(i);
                break;
            }
        }
    }

    public void addOrigin(BaseOperand val, Block origin){
        myInfo.vals.add(val);
        myInfo.blks.add(origin);
        if(! (val instanceof Register))val.appear(this);
        else{
            //System.out.println(val);
            //System.out.println(((Register) val).substance);
            //System.out.println(rd.substance);
            //if(((Register) val).substance != rd.substance || rd.substance == null)
                val.appear(this);
        }
    }

    @Override
    public String toString(){
        String s = rd.toString() + " = phi " + rd.type.toString() + " ";
        for(int i = 0;i < myInfo.blks.size();i++){
            if(i > 0)s = s + ", ";
            s = s + "[ " + myInfo.vals.get(i).toString() + ", %" + myInfo.blks.get(i).name + "]";
        }
        return s;
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        myInfo.vals.forEach(v -> v.deleteAppear(this));
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        for (int i = 0; i < myInfo.vals.size(); i++) {
            if(myInfo.vals.get(i) == orignOperand)
                myInfo.vals.set(i, newOperand);
        }
    }
}
