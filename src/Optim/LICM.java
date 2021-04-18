package Optim;

import MIR.*;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.GlobalVar;
import MIR.IRoperand.Register;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class LICM {

    public rootNode rt;
    public boolean flag = false;
    public HashSet<BaseOperand> storeAddr = new HashSet<>();

    public LICM(rootNode rt){
        this.rt = rt;
    }

    void tryAddPreHead(BaseInstruction inst, HashSet<Register> loopDefs, Queue<BaseInstruction> optimal){
        if(inst instanceof Load){
            HashSet<BaseOperand> uses = inst.uses();
            uses.retainAll(loopDefs);
            if(uses.isEmpty() && ((Load)inst).addr instanceof GlobalVar && !storeAddr.contains(((Load)inst).addr)){
                loopDefs.remove(inst.rd);
                optimal.add(inst);
            }
        } else if(!(inst instanceof Br || inst instanceof Call || inst instanceof Phi
                || inst instanceof Store || inst instanceof Ret || inst instanceof Malloc)){
            HashSet<BaseOperand> uses = inst.uses();
            uses.retainAll(loopDefs);
            if(uses.isEmpty()){
                loopDefs.remove(inst.rd);
                optimal.add(inst);
            }
        }
    }

    public void workLoop(Loop loop){
        if(!loop.childLoops.isEmpty())
            loop.childLoops.forEach(this::workLoop);
        HashSet<Register> loopDefs = new HashSet<>();
        Queue<BaseInstruction> optimal = new LinkedList<>();
        loop.loopBlocks.forEach(blk -> {
            blk.Phis.forEach((register, phi) -> loopDefs.add(register));
            blk.stmts.forEach(inst -> {
                if(inst.rd != null)loopDefs.add(inst.rd);
            });
        });

        storeAddr.clear();
        loop.loopBlocks.forEach(blk -> blk.stmts.forEach(inst -> {
            if(inst instanceof Store)storeAddr.add(((Store)inst).addr);
        }));

        Block preHead = loop.preHead;
        loop.loopBlocks.forEach(blk -> blk.stmts.forEach(inst -> tryAddPreHead(inst, loopDefs, optimal)));

        flag |= !optimal.isEmpty();
        while(!optimal.isEmpty()){
            BaseInstruction inst = optimal.poll();
            inst.blk = preHead;
            preHead.addInstBeforeTerminator(inst);
            inst.rd.positions.forEach(pos -> {
                if(loopDefs.contains(pos.rd))
                    tryAddPreHead(pos, loopDefs, optimal);
            });
        }
        //if(preHead.stmts.size()>100)System.out.println(preHead.stmts.size());

        loop.loopBlocks.forEach(blk -> blk.stmts.removeIf(inst -> inst.blk != blk));
    }

    public void workFunc(Function func){
        LoopCollector loopCollector = new LoopCollector(func, true);
        loopCollector.workFunc();
        loopCollector.rootLoops.forEach(this::workLoop);
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> workFunc(func));
        return flag;
    }

}
