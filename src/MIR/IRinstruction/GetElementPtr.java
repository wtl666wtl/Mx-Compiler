package MIR.IRinstruction;

import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.IRPointerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;

public class GetElementPtr extends BaseInstruction{

    public IRBaseType stepType;
    public BaseOperand target, stepNum, offset;

    public GetElementPtr(Register rd, Block blk, IRBaseType stepType,
                         BaseOperand target, BaseOperand stepNum, BaseOperand offset){
        super(rd, blk);

        this.stepType = stepType;
        this.target = target;
        this.stepNum = stepNum;
        this.offset = offset;

        rd.defInst = this;
        target.appear(this);
        stepNum.appear(this);
        //offset.appear(this);
    }

    @Override
    public String toString(){
        return rd.toString()+ " = getelementptr inbounds " + stepType.toString()
                + ", " + target.type.toString() + " " + target.toString()
                + ", " + stepNum.type.toString() + " " + stepNum.toString()
                + (offset ==null ? "" : ", " + offset.type.toString() + " " + offset.toString());//may not have offset
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        target.deleteAppear(this);
        stepNum.deleteAppear(this);
        if(offset != null)offset.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register orignOperand, BaseOperand newOperand) {
        if(target == orignOperand)target = newOperand;
        if(stepNum == orignOperand)stepNum = newOperand;
    }

    @Override
    public void inlineCopy(Block newblk, Function func, inlineCorrespond a) {
        newblk.addInst(new GetElementPtr((Register) a.get(rd), newblk, stepType, a.get(target), a.get(stepNum), offset == null ? null : a.get(offset)));
    }

    @Override
    public HashSet<BaseOperand> uses() {
        HashSet<BaseOperand> use = new HashSet<>();
        use.add(target);use.add(stepNum);
        return use;
    }

}
