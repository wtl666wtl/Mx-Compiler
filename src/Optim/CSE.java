package Optim;

import Assembly.AsmOperand.Reg;
import MIR.Block;
import MIR.Function;
import MIR.IRinstruction.*;
import MIR.IRoperand.Register;
import MIR.rootNode;

import java.util.*;

public class CSE {

    public rootNode rt;
    public boolean flag = false;
    public Function curFunc = null;

    public CSE(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> {
            curFunc = func;
            func.funcBlocks.forEach(this::workBlock);
        });
        return flag;
    }

    public void workPre(Block blk, ArrayList<BaseInstruction> insts){
        int Limit = 5, req = 0, cnt = 0 , hit = 0;
        for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next();
            /*if(cnt == Limit){
                if(hit >= req)req++;
                else return;
                cnt = hit = 0;
            }*/
            cnt++;
            if(!(inst instanceof Br || inst instanceof Call || inst instanceof Phi || inst instanceof Load
                    || inst instanceof Store || inst instanceof Ret || inst instanceof Malloc)){
                for(BaseInstruction it : insts){
                    if(it.isSame(inst)){
                        inst.rd.replaceAllUse(it.rd);
                        p.remove();
                        inst.deleteSelf(false);
                        hit++;
                        break;
                    }
                }
            }
        }
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
                            break;
                        }
                    }
                    if(!hasAppeared)insts.add(inst);
                    else change = true;
                }
            }
            /*curFunc.funcBlocks.forEach(fblk -> {
                if(fblk.tryDom(blk))workPre(fblk, insts);
            });*/
            blk.sucblks.forEach(sucblk -> {
                if (sucblk.tryDom(blk)) workPre(sucblk, insts);
            });
            HashSet<Phi> phis = new HashSet<>();
            for(Iterator<Map.Entry<Register, Phi>> p = blk.Phis.entrySet().iterator(); p.hasNext();){
                Map.Entry<Register, Phi> entry = p.next();
                Phi phi = entry.getValue();
                boolean hasAppeared = false;
                for(Phi it : phis){
                    if(it.isSame(phi)){
                        hasAppeared = true;
                        phi.rd.replaceAllUse(it.rd);
                        break;
                    }
                }
                if(!hasAppeared)phis.add(phi);
                else{
                    p.remove();
                    phi.deleteSelf(false);
                    change = true;
                }
            }
            flag |= change;
        };
    }

}
