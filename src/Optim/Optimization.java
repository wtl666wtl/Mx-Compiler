package Optim;

import MIR.rootNode;

public class Optimization {

    public rootNode rt;

    public Optimization(rootNode rt){
        this.rt = rt;
    }

    public void work(){
        boolean flag = true;
        while(flag){
            flag = new DCE(rt).work();
            flag |= new ConstEval(rt).work();
            flag |= new ConstMerge(rt).work();
            flag |= new Inline(rt).work();

            //flag |= new LICM(rt).work();

            //todo LICM for const-adv & loop-adv
            //todo loop?
        }
    }
}
