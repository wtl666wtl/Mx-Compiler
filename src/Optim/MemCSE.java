package Optim;

import MIR.Block;
import MIR.Function;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.GlobalVar;
import MIR.rootNode;

import java.util.ListIterator;

public class MemCSE {

    public rootNode rt;
    public boolean flag = false;
    public Function curFunc = null;

    public MemCSE(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.globalVars.forEach(globalVar -> {
            rt.funcs.forEach((s, func) -> {
                curFunc = func;
                func.funcBlocks.forEach(Block::clearMemCSE);
                func.funcBlocks.forEach(blk -> workBlock(blk, globalVar));
            });
        });
        return flag;
    }

    public boolean judge(Call inst, GlobalVar globalVar){
        if(rt.builtInFuncs.containsValue(inst.callee))return false;
        else{
            if(curFunc == inst.callee)return true;
            for(Block blk : inst.callee.funcBlocks)
                for(BaseInstruction it : blk.stmts){
                    if(it instanceof Store && ((Store)it).addr == globalVar)return true;
                    if(it instanceof Load && ((Load)it).addr == globalVar)return true;
                    if(it instanceof Call)return true;
                }
            return false;
            //return true;
        }
    }

    public void workBlock(Block blk, GlobalVar globalVar) {
        BaseOperand nowVal = blk.nowVal;
        boolean storeReq = false;
        for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next();
            if(inst instanceof Load && ((Load)inst).addr.equals(globalVar)){
                if(nowVal == null)nowVal = inst.rd;
                else {
                    p.set(new Zext(inst.rd, blk, nowVal));
                    //inst.rd.replaceAllUse(nowVal);
                    //p.remove();
                    inst.deleteSelf(false);
                }
            } else if(inst instanceof Store && ((Store)inst).addr.equals(globalVar)){
                nowVal = ((Store)inst).storeVal;
                storeReq = true;
                p.remove();
                inst.deleteSelf(false);
            } else if(inst instanceof Call && judge((Call)inst, globalVar)){
                if(storeReq){
                    p.previous();
                    p.add(new Store(blk, globalVar, nowVal));
                    p.next();
                    storeReq = false;
                }
                nowVal = null;
            }
        }
        if(storeReq){
            blk.addInstBeforeTerminator(new Store(blk, globalVar, nowVal));
        }
        if(nowVal != null){
            if(blk.sucblks.size() == 1 && blk.sucblks.get(0).preblks.size() == 1)
                blk.sucblks.get(0).nowVal = nowVal;
        }
    }

}
