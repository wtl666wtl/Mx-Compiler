package Optim;

import MIR.Block;
import MIR.IRinstruction.*;
import MIR.rootNode;

import javax.swing.text.BadLocationException;
import java.util.ArrayList;
import java.util.ListIterator;

public class CSE {

    public rootNode rt;
    public boolean flag = false;

    public CSE(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(this::workBlock));
        return flag;
    }

    public void workBlock(Block blk){
        boolean change = true;
        while(change){
            change = false;
            ArrayList<BaseInstruction> insts = new ArrayList<>();
            for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
                BaseInstruction inst = p.next();
                if(!(inst instanceof Br || inst instanceof Call || inst instanceof Phi || inst instanceof Load
                        || inst instanceof Store || inst instanceof Ret || inst instanceof Malloc)){
                    boolean hasAppeared = false;
                    for(BaseInstruction it : insts){
                        if(it.isSame(inst)){
                            hasAppeared = true;
                            inst.rd.replaceAllUse(it.rd);
                            p.remove();
                            inst.deleteSelf(false);
                        }
                    }
                    if(!hasAppeared)insts.add(inst);
                    else change = true;
                }
            }
            flag |= change;
        };
    }

}
