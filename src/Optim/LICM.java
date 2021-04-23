package Optim;

import MIR.*;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.GlobalVar;
import MIR.IRoperand.Register;
import MIR.IRtype.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class LICM {

    public rootNode rt;
    public boolean flag = false;
    public HashSet<GlobalVar> storeAddr = new HashSet<>();
    public boolean hasCall = false;

    public LICM(rootNode rt){
        this.rt = rt;
    }

    public boolean simpleVar(GlobalVar x){
        IRBaseType pointTo = ((IRPointerType)x.type).pointTo;
        return pointTo instanceof IRIntType || pointTo instanceof IRBoolType || pointTo instanceof IRVoidType;
    }

    public void tryAddPreHead(BaseInstruction inst, HashSet<Register> loopDefs, Queue<BaseInstruction> optimal){
        if(inst instanceof Load){
            HashSet<BaseOperand> uses = inst.uses();
            uses.retainAll(loopDefs);
            if(uses.isEmpty() && ((Load)inst).addr instanceof GlobalVar &&
                    !hasCall && !storeAddr.contains((GlobalVar)((Load)inst).addr)){
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

    public boolean judge(Call inst){
        return !rt.builtInFuncs.containsValue(inst.callee);
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
        hasCall = false;
        loop.loopBlocks.forEach(blk -> blk.stmts.forEach(inst -> {
            if(inst instanceof Store && ((Store)inst).addr instanceof GlobalVar)storeAddr.add((GlobalVar)((Store)inst).addr);
            if(inst instanceof Call && judge((Call) inst))hasCall = true;
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
