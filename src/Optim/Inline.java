package Optim;

import Backend.DomGen;
import Backend.FuncBlockCollector;
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
    static public int addInstCnt = 0;
    static public int addInstLimit = 2147483647;//no limit
    static public int maxLimit = 750;
    static public int maxLimitForSmallFunc = 1000;
    static public int oneLimitForSmallFunc = 30;
    static public int blkLimit = 50;
    static public int oneLimit = 100;
    static public int taskLimit = 2000;
    public LinkedHashSet<Function> badFuncs = new LinkedHashSet<>();
    public HashSet<Function> hasVisited = new HashSet<>();
    public Stack<Function> stack = new Stack<>();
    public boolean force = false;
    public int taskCnt = 0;

    public Inline(rootNode rt, boolean force){
        this.rt = rt;
        this.force = force;
    }

    public int countInst(Function x){
        int res = 0;
        for(Block blk : x.funcBlocks)
            res += blk.stmts.size();
        return res;
    }

    void DFS(Function func){
        hasVisited.add(func);
        stack.push(func);
        boolean ring = false;
        for(Function sfunc : stack){
            if (func.callFuncs.contains(sfunc)) {
                ring = true;
                break;
            }
        }
        if(ring)badFuncs.add(func);
        func.callFuncs.forEach(callFunc -> {
            if(!hasVisited.contains(callFunc))DFS(callFunc);
        });
        stack.pop();
    }

    void tryInline(){

        rt.funcs.forEach((s, func) -> {
            //System.out.println(func.name);
            //System.out.println(func.callFuncs);
            func.callFuncs.clear();
            func.funcBlocks.forEach(blk -> blk.stmts.forEach(inst ->{
                if(inst instanceof Call){
                    Call it = (Call) inst;
                    if(!rt.builtInFuncs.containsValue(it.callee))func.callFuncs.add(it.callee);
                }
            }));
        });

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
                            //&& it.callee.outblk.getTerminator() instanceof Ret
                            && it.callee.funcBlocks.size() < 40
                            && ((inlineCnt + waitList.size() < maxLimit ||
                                countInst(it.callee) < oneLimitForSmallFunc && inlineCnt < maxLimitForSmallFunc)
                            && addInstCnt < addInstLimit && goodFunc.contains(((Call) inst).callee))){
                        waitList.put(it, func);
                        //System.out.println(it.callee.name);
                    }

                }
            }
        }));
        if(waitList.isEmpty() && force){

            rt.funcs.forEach((s, func) -> {
                //System.out.println(func.name);
                //System.out.println(func.callFuncs);
                func.callFuncs.clear();
                func.funcBlocks.forEach(blk -> blk.stmts.forEach(inst ->{
                    if(inst instanceof Call){
                        Call it = (Call) inst;
                        if(!rt.builtInFuncs.containsValue(it.callee) && it.callee != func)func.callFuncs.add(it.callee);
                    }
                }));
            });

            maxLimit = 400;
            badFuncs.clear();
            hasVisited.clear();
            stack.clear();
            hasVisited.addAll(rt.builtInFuncs.values());
            badFuncs.addAll(rt.builtInFuncs.values());
            badFuncs.add(rt.funcs.get("main"));

            rt.funcs.forEach((s, func) -> {
                if(!hasVisited.contains(func))DFS(func);
            });

            //badFuncs.forEach(func -> System.out.println(func.name));

            rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(blk -> {
                for(BaseInstruction inst : blk.stmts){
                    if(inst instanceof Call){
                        Call it = (Call) inst;
                        if(!rt.builtInFuncs.containsKey(it.callee.name) && !badFuncs.contains(it.callee)
                                //&& it.callee.outblk.getTerminator() instanceof Ret
                                && it.callee.funcBlocks.size() < 40
                                && (inlineCnt + waitList.size() < maxLimit && addInstCnt < addInstLimit) && countInst(it.callee) < oneLimit){
                            waitList.put(it, func);
                            //System.out.println(it.callee.name);
                        }

                    }
                }
            }));
        }
        for(Map.Entry<Call, Function> entry : waitList.entrySet()){
            Call call = entry.getKey();
            Function func = entry.getValue();
            inlineFunc(call, func);
            change = true;
        }
        flag |= change;
        //System.out.println(addInstCnt);
        rt.funcs.forEach((s, func) -> {
            //System.out.println(func.name);
            //System.out.println(func.callFuncs);
            func.callFuncs.clear();
            func.funcBlocks.forEach(blk -> blk.stmts.forEach(inst ->{
                if(inst instanceof Call){
                    Call it = (Call) inst;
                    if(!rt.builtInFuncs.containsValue(it.callee))func.callFuncs.add(it.callee);
                }
            }));
        });
    }

    public void inlineFunc(Call call, Function func){
        ++inlineCnt;
        if(inlineCnt > maxLimit && (force || countInst(call.callee) > oneLimitForSmallFunc || inlineCnt > maxLimitForSmallFunc))return;
        Function callee = call.callee;
        taskCnt += countInst(callee);
        if(taskCnt >= taskLimit)return;
        if(!(callee.outblk.getTerminator() instanceof Ret))return;
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
            addInstCnt += blk.Phis.size();
            addInstCnt += blk.stmts.size();
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
        func.funcBlocks = FuncBlockCollector.work(func.inblk);
        //System.out.println(func);
        new DomGen(func).workFunc();
        //System.out.println("Yes");
        /*func.callFuncs.clear();
        func.funcBlocks.forEach(blk -> blk.stmts.forEach(inst ->{
            if(inst instanceof Call){
                Call it = (Call) inst;
                if(!rt.builtInFuncs.containsValue(it.callee))func.callFuncs.add(it.callee);
            }
        }));*/
    }

    public boolean work(){
        flag = false;
        taskCnt = 0;
        tryInline();
        return flag;
    }

}