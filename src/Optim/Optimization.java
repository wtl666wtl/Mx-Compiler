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

        }
    }
}
