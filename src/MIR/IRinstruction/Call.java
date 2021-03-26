package MIR.IRinstruction;

import MIR.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRFunctionType;

import java.util.ArrayList;
import java.util.ListIterator;

public class Call extends BaseInstruction {

    public Function callee;
    public ArrayList<BaseOperand> params;

    public Call(Register rd, Block blk, Function callee, ArrayList<BaseOperand> params){
        super(rd, blk);
        this.callee = callee;
        this.params = params;
        if(rd != null)rd.defInst = this;
        params.forEach(pd -> pd.appear(this));
    }

    @Override
    public String toString(){
        String s = (rd ==null ? "call void @" : rd.toString() + " = call " + rd.type.toString() + " @") + callee.name;
        if(params.isEmpty())return s + "()";
        s = s + "(";
        for (int i = 0; i < params.size(); i++){
            if(i > 0)s = s + ", ";
            s =s + params.get(i).type.toString() + " " + params.get(i).toString();
        }
        return s + ")";
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        params.forEach(it -> it.deleteAppear(this));
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        for (int i = 0; i < params.size(); i++) {
            if(params.get(i) == orignOperand)params.set(i, newOperand);
        }
    }
}
