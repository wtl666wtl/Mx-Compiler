package MIR.IRinstruction;

import Assembly.AsmOperand.Reg;
import Backend.inlineCorrespond;
import MIR.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        phiInfo newPhiInfo = new phiInfo();
        for (int i = 0; i < myInfo.vals.size(); i++) {
            newPhiInfo.blks.add(a.get(myInfo.blks.get(i)));
            newPhiInfo.vals.add(a.get(myInfo.vals.get(i)));
        }
        newblk.addPhi(new Phi((Register) a.get(rd), newblk, newPhiInfo));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        return new HashSet<>(myInfo.vals);
    }

    @Override
    public boolean isSame(BaseInstruction it) {
        if(it instanceof Phi){
            phiInfo i = ((Phi) it).myInfo;
            HashSet<BaseOperand> valSet = new HashSet<>(i.vals);
            if(i.vals.size() == myInfo.vals.size()){
                for (int j = 0; j < myInfo.vals.size(); j++) {
                    BaseOperand val = myInfo.vals.get(j);
                    if(!valSet.contains(val) || myInfo.blks.get(j) != i.blks.get(i.vals.indexOf(val)))return false;
                }
                return true;
            }
        }
        return false;
    }
}
