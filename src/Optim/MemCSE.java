package Optim;

import MIR.Block;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.*;
import MIR.IRoperand.BaseOperand;
import MIR.rootNode;

import java.util.ListIterator;

public class MemCSE {

    public rootNode rt;
    public boolean flag = false;

    public MemCSE(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(this::workBlock));
        return flag;
    }

    public void workBlock(Block blk) {
        rt.globalVars.forEach(globalVar -> {
            BaseOperand nowVal = null;
            boolean storeReq = false;
            for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
                BaseInstruction inst = p.next();
                if(inst instanceof Load && ((Load)inst).addr.equals(globalVar)){
                    if(nowVal == null)nowVal = inst.rd;
                    else {
                        p.set(new Zext(inst.rd, blk, nowVal));
                        inst.deleteSelf(false);
                    }
                } else if(inst instanceof Store && ((Store)inst).addr.equals(globalVar)){
                    nowVal = ((Store)inst).storeVal;
                    storeReq = true;
                    p.remove();
                    inst.deleteSelf(false);
                } else if(inst instanceof Call){
                    if(storeReq){
                        p.previous();
                        p.add(new Store(blk, globalVar, nowVal));
                        p.next();
                        storeReq = false;
                    }
                    nowVal = null;
                }
            }
            if(storeReq) blk.addInstBeforeTerminator(new Store(blk, globalVar, nowVal));
        });
    }

}
