package Optim;

import Backend.FuncBlockCollector;
import MIR.Block;
import MIR.Function;
import MIR.IRinstruction.*;
import MIR.IRoperand.Register;
import MIR.rootNode;

import java.util.ListIterator;

public class TRE {

    public rootNode rt;

    public TRE(rootNode rt){
        this.rt = rt;
    }

    public void work(){
        rt.funcs.forEach((s, func) -> workFunc(func));
    }

    public void workFunc(Function func){
        func.funcBlocks = FuncBlockCollector.work(func.inblk);

        func.funcBlocks.forEach(blk -> {
            for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
                BaseInstruction inst = p.next();
                if(inst instanceof Call){
                    Call it = (Call) inst;
                    if(!(it.loopCall && it == blk.stmts.get(blk.stmts.size() - 2) &&
                            (blk.stmts.getLast() instanceof Ret || blk.stmts.getLast() instanceof Br && ((Br)blk.stmts.getLast()).iftrue == func.outblk) ))continue;
                    if(func.classPtr != null)continue;
//p -> call
                    //p+1 -> ret/jp to returnBlock(must be a Terminator)
                    blk.deleteTerminator();
                    while(blk.stmts.removeLast() != inst);
                    int i = func.classPtr == null ? 0 : 1;
                    //System.out.println(func.name);
                    for(Register var : func.ParamPtrs){
                        //System.out.println(var);
                        //System.out.println(new Store(blk, var, it.params.get(i)));
                        blk.addInst(new Store(blk, var, it.params.get(i++)));
                    }
                    blk.addTerminator(new Br(blk, null, func.inblk.sucblks.get(0), null));
                    break;
                }
            }
        });

        /*func.funcBlocks.forEach(blk -> {
            System.out.println(blk.name);
            blk.stmts.forEach(System.out::println);
        });*/

    }

}
