package Optim;

import Backend.DomGen;
import MIR.Block;
import MIR.Function;
import MIR.rootNode;

import java.util.HashSet;

public class Optimization {

    public rootNode rt;
    public static int LastInst = 0;
    public static int inst = 2147483647;
    public static int instLimit = 9000;

    public Optimization(rootNode rt){
        this.rt = rt;
    }

    public boolean judgeInst(){
        LastInst = inst;
        inst = 0;
        HashSet<String> uselessFunc = new HashSet<>();
        rt.funcs.forEach((s, func) -> {
            if(func.appear.size() == 0 && !func.name.equals("main")) uselessFunc.add(s);
        });
        if(uselessFunc.size() > 0){
            for(String s : uselessFunc)rt.funcs.remove(s);
        }
        rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(blk -> inst += blk.stmts.size() + blk.Phis.size()));
        //System.out.println("inst = " + inst);
        return inst < instLimit;
    }

    public void mergeFuncBlock(Function func){
        boolean qwq = true;
        while(qwq){
            HashSet<Block> merge = new HashSet<>();
            func.funcBlocks.forEach(blk -> {
                if(blk.preblks.size() == 1 && blk.preblks.get(0).sucblks.size() == 1)
                    merge.add(blk);
            });
            merge.forEach(blk -> {
                Block preblk = blk.preblks.get(0);
                preblk.deleteTerminator();
                preblk.inlineMerge(blk);
                if(func.outblk == blk)func.outblk = preblk;
            });
            func.funcBlocks.removeAll(merge);
            qwq = !merge.isEmpty();
        }
        new DomGen(func).workFunc();
    }

    public void work1(){
        boolean flag = true;
        while (flag){
            flag = new DCE(rt).work();//adce
            flag |= new ConstEval(rt).work();//const
            flag |= new ConstMerge(rt).work();//inline-adv
            flag |= new CSE(rt).work();
            flag |= new InstSimplify(rt).work();
            //flag |= new StrengthReduce(rt).work();

            flag |= new MemCSE(rt).work();
            flag |= new LICM(rt).work();//const-adv & loop-adv
        }

    }

    public void work(){
        /*rt.funcs.forEach((s, func) -> {
            //System.out.println(func.name);
            if(func.inblk.sucblks.size() == 1 && func.inblk.sucblks.get(0).preblks.size() == 1){
                Block second = func.inblk.sucblks.get(0);
                func.inblk.deleteTerminator();
                func.inblk.inlineMerge(second);
                if(func.outblk == second)func.outblk = func.inblk;
                func.funcBlocks.remove(second);
                new DomGen(func).workFunc();
            }
        });*/
        work1();
        boolean ok = true;
        /*while (judgeInst() && ok){
            ok = new Inline(rt, false).work();//inline
            //new ConstMerge(rt).work();
        }*/
        work1();
        ok = true;
        /*while (judgeInst() && ok){
            ok = new Inline(rt, true).work();
            //new ConstMerge(rt).work();
        }*/
        work1();
        //rt.funcs.forEach((s, func) -> mergeFuncBlock(func));
    }

}
