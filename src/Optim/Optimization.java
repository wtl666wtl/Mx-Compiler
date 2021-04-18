package Optim;

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

    public void work(){
        boolean flag = true;
        while(flag){
            flag = new DCE(rt).work();
            flag |= new ConstEval(rt).work();
            flag |= new ConstMerge(rt).work();
            flag |= new CSE(rt).work();
            if(judgeInst())flag = new Inline(rt).work();

            flag |= new LICM(rt).work();//judgeInst();

            //todo LICM for const-adv & loop-adv. After finish this, check RegAlloc!
            //todo loop?
        }
        //System.out.println("YES");
    }
}
