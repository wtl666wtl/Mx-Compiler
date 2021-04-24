package Optim;

import MIR.Block;
import MIR.Function;
import MIR.IRoperand.Register;
import MIR.Loop;
import MIR.rootNode;

import java.util.HashMap;

public class StrengthReduce {

    public rootNode rt;
    public boolean flag = false;

    public StrengthReduce(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> workFunc(func));
        return flag;
    }

    public void workFunc(Function func){
        LoopCollector loops = new LoopCollector(func, true);
        loops.workFunc();
        loops.rootLoops.forEach(this::workLoop);
    }

    public void workLoop(Loop loop){
        /*loop.childLoops.forEach(this::workLoop);
        HashMap<Register, >;
        Block preHead = loop.preHead;
        Block head = preHead.sucblks.get(0);
        head.Phis.forEach((reg, phi) -> {
            Register
        });*/
    }

}
