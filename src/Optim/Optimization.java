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
            flag = new DCE(rt).work();//adce
            flag |= new ConstEval(rt).work();//const
            flag |= new ConstMerge(rt).work();//inline-adv
            flag |= new CSE(rt).work();
            //flag |= new InstSimplify(rt).work();

            boolean ok = true;
            while(judgeInst() && ok)flag |= ok = new Inline(rt).work();//inline

            flag |= new MemCSE(rt).work();
            flag |= new LICM(rt).work();//const-adv & loop-adv
            //System.out.println("?");
        }
        //System.out.println("YES");
    }

}
