package Optim;

import Backend.DominatorTree;
import MIR.*;
import MIR.IRinstruction.*;
import MIR.IRoperand.Register;
import MIR.rootNode;

import java.util.*;

public class DCE {

    public rootNode rt;
    public boolean flag = false;

    public DCE(rootNode rt){
        this.rt = rt;
    }

    public boolean judgeCall(Call inst){
        if(!rt.builtInFuncs.containsValue(inst.callee))return false;
        String name = inst.callee.name;
        //read/write can not be deleted!
        if(name.equals("builtIn_print"))return false;
        if(name.equals("builtIn_println"))return false;
        if(name.equals("builtIn_printInt"))return false;
        if(name.equals("builtIn_printlnInt"))return false;
        if(name.equals("builtIn_getInt"))return false;
        if(name.equals("builtIn_getString"))return false;
        return !name.equals("malloc");
    }

    public void NaiveEliminate(){
        boolean hasChange = true;
        while(hasChange){
            hasChange = false;
            for(Map.Entry<String, Function> f : rt.funcs.entrySet()){
                String funcName = f.getKey();
                Function func = f.getValue();
                for(Block blk : func.funcBlocks){
                    //useless phi
                    for(Iterator<Map.Entry<Register, Phi>> it = blk.Phis.entrySet().iterator(); it.hasNext();){
                        Map.Entry<Register, Phi> phiEntry = it.next();
                        Register phiReg = phiEntry.getKey();
                        Phi inst = phiEntry.getValue();
                        if(phiReg.positions.size()==0){
                            it.remove();
                            inst.deleteSelf(false);
                            hasChange = true;
                        }
                    }
                    //useless inst
                    for(Iterator<BaseInstruction> it = blk.stmts.listIterator(); it.hasNext();){
                        BaseInstruction inst = it.next();
                        if(inst.deleteFlag){
                            it.remove();
                            continue;
                        }
                        if(inst instanceof Load || inst instanceof Binary ||
                            inst instanceof Icmp || inst instanceof BitCast ||
                            inst instanceof Zext || inst instanceof Malloc ||
                            inst instanceof GetElementPtr || inst instanceof Call && judgeCall((Call) inst)){
                            Register rd = inst.rd;
                            //System.out.println(inst);
                            /*if(rd != null)for(BaseInstruction i : rd.positions){
                                System.out.println(i);
                            }*/
                            if(rd == null || rd.positions.size() == 0){
                                it.remove();
                                inst.deleteSelf(false);
                                hasChange = true;
                            }
                        }
                    }
                    //TODO: stupid cycle
                   if(!hasChange){
                        for (BaseInstruction inst : blk.stmts) {
                            if (inst instanceof Load || inst instanceof Binary ||
                                    inst instanceof Icmp || inst instanceof BitCast ||
                                    inst instanceof Zext || inst instanceof Malloc ||
                                    inst instanceof GetElementPtr || inst instanceof Call && judgeCall((Call) inst)) {
                                Register rd = inst.rd;
                                int tot = 0;
                                while (rd != null && rd.positions.size() == 1) {
                                    tot++;
                                    BaseInstruction usedInst = rd.positions.iterator().next();
                                    if(tot > 10000 || !(usedInst instanceof Load || usedInst instanceof Binary ||
                                            usedInst instanceof Icmp || usedInst instanceof BitCast ||
                                            usedInst instanceof Zext || usedInst instanceof Malloc ||
                                            usedInst instanceof GetElementPtr || usedInst instanceof Phi ||
                                    usedInst instanceof Call && judgeCall((Call) usedInst)))break;
                                    if (usedInst == inst) {
                                        hasChange = true;
                                        break;
                                    }
                                    rd = usedInst.rd;//System.out.println(usedInst);
                                }
                                if (hasChange) {
                                    inst.deleteFlag = true;
                                    inst.deleteSelf(false);
                                    while (rd != null && rd.positions.size() == 1) {
                                        BaseInstruction usedInst = rd.positions.iterator().next();
                                        if (usedInst == inst) {
                                            break;
                                        }
                                        rd = usedInst.rd;
                                        if (usedInst instanceof Phi) {
                                            usedInst.deleteSelf(true);
                                        } else {
                                            usedInst.deleteFlag = true;
                                            usedInst.deleteSelf(false);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            flag |= hasChange;
        }
        if(flag)rt.funcs.forEach((s, func) -> new DominatorTree(func).workFunc());
    }

    public void workFunc(Function func){
        HashSet<BaseInstruction> needInst = new HashSet<>();
        Queue<BaseInstruction> Q = new LinkedList<>();
        HashMap<Register, BaseInstruction> regDefs = new HashMap<>();
        func.funcBlocks.forEach(blk -> blk.stmts.forEach(it -> {
            if(it.rd != null)regDefs.put(it.rd, it);
            if(it instanceof Br || it instanceof Call || it instanceof Store || it instanceof Ret){
                needInst.add(it);
                Q.add(it);
            }
        }));
        func.funcBlocks.forEach(blk -> blk.Phis.forEach((rd, it) -> {
            if(it.rd != null)regDefs.put(it.rd, it);
        }));
        while(!Q.isEmpty()){
            BaseInstruction it = Q.poll();
            it.uses().forEach(operand -> {
                if(operand instanceof Register && regDefs.containsKey(operand)){
                    BaseInstruction def = regDefs.get(operand);
                    if(!needInst.contains(def)){
                        needInst.add(def);
                        Q.add(def);
                    }
                }
            });
        }
        func.funcBlocks.forEach(blk -> blk.stmts.removeIf(it -> !needInst.contains(it)));
    }

    public boolean work(){
        flag = false;
        NaiveEliminate();
        rt.funcs.forEach((s, func) -> workFunc(func));
        return flag;
    }
}
