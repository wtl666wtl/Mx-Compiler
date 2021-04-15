package Optim;

import Backend.DomGen;
import Backend.inlineCorrespond;
import MIR.Block;
import MIR.Function;
import MIR.IRinstruction.*;
import MIR.IRoperand.BaseOperand;
import MIR.rootNode;

import java.util.*;

public class Inline {

    public rootNode rt;
    public boolean flag = false;
    static public int inlineCnt = 0;
    static public int limit = 10;
    static public int maxLimit = 30;

    public Inline(rootNode rt){
        this.rt = rt;
    }

    void tryInline(){
        boolean change = false;
        HashSet<String> uselessFunc = new HashSet<>();
        HashSet<Function> goodFunc = new HashSet<>();
        rt.funcs.forEach((s, func) -> {
            if(func.appear.size() == 0 && !func.name.equals("main")) uselessFunc.add(s);
            else if(func.callFuncs.isEmpty())goodFunc.add(func);
        });
        //System.out.println(goodFunc.size());
        if(uselessFunc.size() > 0){
            for(String s : uselessFunc)rt.funcs.remove(s);
        }
        HashMap<Call, Function> waitList = new LinkedHashMap<>();
        rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(blk -> {
            for(BaseInstruction inst : blk.stmts){
                if(inst instanceof Call){
                    Call it = (Call) inst;
                    if(!it.loopCall && it.callee != func && !rt.builtInFuncs.containsKey(it.callee.name)
                            && (inlineCnt < maxLimit && goodFunc.contains(((Call) inst).callee)))
                        waitList.put(it, func);
                }
            }
        }));
        for(Map.Entry<Call, Function> entry : waitList.entrySet()){
            Call call = entry.getKey();
            Function func = entry.getValue();
            inlineFunc(call, func);
            change = true;
        }
        flag |= change;
    }

    public void inlineFunc(Call call, Function func){
        ++inlineCnt;
        if(inlineCnt > maxLimit)return;
        Function callee = call.callee;
        callee.appear.remove(call);
        //System.out.println("--");
        //System.out.println(callee.name);
        //System.out.println(func.name);
        Block curblk = call.blk;
        //System.out.println(curblk.name);
        HashMap<BaseOperand, BaseOperand> correspondOperand = new HashMap<>();
        HashMap<Block, Block> correspondBlk = new HashMap<>();
        for(int i = 0; i < call.params.size(); i++){
            correspondOperand.put(callee.funType.paramList.get(i), call.params.get(i));
        }
        callee.funcBlocks.forEach(blk -> {
            Block newblk = new Block(blk.name + "_inline" + inlineCnt);
            correspondBlk.put(blk, newblk);
        });
        inlineCorrespond correspond = new inlineCorrespond(correspondOperand, correspondBlk);

        callee.funcBlocks.forEach(blk -> {
            Block newblk = correspondBlk.get(blk);
            blk.Phis.forEach((register, phi) -> phi.inlineCopy(newblk, func, correspond));
            blk.stmts.forEach(inst -> inst.inlineCopy(newblk, func, correspond));
            //blk.stmts.forEach(System.out::println);
            //System.out.println("------------------------------");
        });
        //System.out.println(callee.funcBlocks.size());
        Block afterInline = new Block(curblk.name + "_afterInline");
        curblk.inlineSplit(afterInline, call);
        Block newIn = correspondBlk.get(callee.inblk);
        /*System.out.println("**********");
        System.out.println(callee.name);
        System.out.println(callee.inblk);
        System.out.println(callee.outblk);
        System.out.println(newIn);*/
        Block newOut = correspondBlk.get(callee.outblk);
        //System.out.println(newOut);
        Ret ret = (Ret)newOut.getTerminator();
        if(ret.retVal != null){
            call.rd.replaceAllUse(ret.retVal);
        }
        newOut.deleteTerminator();
        //System.out.println(newOut.sucblks.size());
        newOut.inlineMerge(afterInline);
        //System.out.println(newOut.sucblks.size());
        curblk.inlineMerge(newIn);
        //System.out.println(curblk.sucblks.size());
        //System.out.println(curblk.stmts.size());

        if(func.outblk.equals(curblk) && newIn != newOut) func.outblk = newOut;
        LinkedHashSet<Block> newBlocks = new LinkedHashSet<>();
        //func.funcBlocks.clear();
        for(Block blk : func.funcBlocks){
            if(blk != curblk)newBlocks.add(blk);
            else{
                newBlocks.add(blk);
                //for(BaseInstruction inst : blk.stmts)System.out.println(inst);
                callee.funcBlocks.forEach(inlinedBlk -> {
                    Block addBlk = correspondBlk.get(inlinedBlk);
                    //System.out.println("F@Q");
                    if(addBlk != newIn){
                        newBlocks.add(addBlk);
                        //for(BaseInstruction inst : addBlk.stmts)System.out.println(inst);
                        //System.out.println("F@Q");
                    }
                });
            }
        }
        //System.out.println(curblk.sucblks.size());
        func.funcBlocks = newBlocks;
        func.callFuncs.clear();
        func.funcBlocks.forEach(blk -> blk.stmts.forEach(inst ->{
            if(inst instanceof Call){
                Call it = (Call) inst;
                if(!rt.builtInFuncs.containsValue(it.callee))func.callFuncs.add(it.callee);
            }
        }));
        new DomGen(func).workFunc();
    }

    public boolean work(){
        flag = false;
        int cnt = 0;
        tryInline();
        //rt.funcs.forEach((s, func) -> new DomGen(func).workFunc());
        /*rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(blk -> {
            System.out.println("=====================");
            System.out.println(blk.name);
            System.out.println("#===================#");
            blk.preblks.forEach(blk1 -> System.out.println(blk1.name));
            System.out.println("#===================#");
            blk.sucblks.forEach(blk2 -> System.out.println(blk2.name));
        }));
        System.out.println("===END===");*/
        //limit = Math.min(limit * 2, 32);
        return flag;
    }

}
