package Backend;

import MIR.*;
import MIR.IRoperand.*;
import MIR.IRtype.*;
import MIR.IRinstruction.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SolvePhi {

    public rootNode rt;

    public SolvePhi(rootNode rt){
        this.rt = rt;
    }

    public void run(){
        rt.funcs.forEach((funcName, func) -> solveFuncPhi(func));
        //rt.funcs.forEach((funcName, func) -> func.funcBlocks = FuncBlockCollector.work(func.inblk));
    }

    public void solveFuncPhi(Function func){
        HashSet<blockPair> tmp = new HashSet<>();
        func.funcBlocks.forEach(blk -> {
            if(blk.sucblks.size() > 1){
                for (Block sucblk : blk.sucblks) {
                    if(sucblk.preblks.size() > 1)
                        tmp.add(new blockPair(blk, sucblk));
                }
            }
        });

        tmp.forEach(blkPair -> {
            Block blk1 = blkPair.blk1, blk2 = blkPair.blk2;
            Block mid = new Block("midblk");
            func.funcBlocks.add(mid);
            //System.out.println("@" + blk1.name);
            //System.out.println(blk1.sucblks.size());
            //System.out.println("@" + blk2.name);
            mid.addTerminator(new Br(mid, null, blk2, null));

            blk2.Phis.forEach((reg, phi) -> {
                for(int i = 0;i < phi.myInfo.blks.size(); i++){
                    if(phi.myInfo.blks.get(i) == blk1) {
                        //System.out.println(i);
                        phi.myInfo.blks.set(i, mid);
                    }
                }
            });
            blk1.changeSucblk(blk2, mid);
        });

        HashMap<Block, phiToMove> blkPhis = new HashMap<>();
        func.funcBlocks.forEach(blk -> blkPhis.put(blk, new phiToMove()));
        func.funcBlocks.forEach(blk -> blk.Phis.forEach((reg, phi) -> {
            for(int i = 0; i < phi.myInfo.blks.size(); i++){
                Block preBlk = phi.myInfo.blks.get(i);
                BaseOperand val = phi.myInfo.vals.get(i);
                blkPhis.get(preBlk).addMove(new Move(reg, preBlk, val, false));
            }
        }));
        //System.out.println(func.name);
        blkPhis.forEach(this::solveBlkPhi);
        //System.out.println(func.name);

        //compress jump-only blks

        HashSet<Block> jmpOnlySet = new HashSet<>();
        //System.out.println(func.name);
        func.funcBlocks.forEach(blk -> {
            BaseInstruction it = blk.getHead();
            //System.out.println(blk.stmts.size());
            //blk.stmts.forEach(System.out::println);
            if(it instanceof Br && ((Br)it).cond == null){
                if(blk != func.inblk || ((Br)it).iftrue.preblks.size() == 1) {
                    jmpOnlySet.add(blk);
                    //System.out.println(blk.name);
                    //System.out.println("...");
                }
            }
        });

        jmpOnlySet.forEach(blk -> {
            Block sucblk = blk;
            boolean dangerous = false;
            while (jmpOnlySet.contains(sucblk)) {
                sucblk = ((Br) sucblk.getTerminator()).iftrue;
                if(sucblk == blk){
                    dangerous = true;
                    break;
                }
                //System.out.println("?");
            }
            if(!dangerous) {
                //System.out.println(blk.preblks.size());
                HashSet<Block> preblks = new HashSet<>(blk.preblks);
                for (Block preblk : preblks) {
                    preblk.changeSucblk(blk, sucblk);
                }
                if (blk == func.inblk)
                    func.inblk = sucblk;
            } else jmpOnlySet.remove(blk);
        });
        func.funcBlocks.removeAll(jmpOnlySet);

        /*if(func.inblk.preblks.size() > 0){
            Block old = func.inblk;
            func.inblk = new Block("newIn");
            func.inblk.addTerminator(new Br(func.inblk, null, old, null));
            //func.funcBlocks.add(func.inblk);
            func.funcBlocks = FuncBlockCollector.work(func.inblk);
        }*/
        //System.out.println(func.name);
    }

    public void solveBlkPhi(Block blk, phiToMove para){
        boolean flag = true;
        while(flag){

            boolean hasMore = false;
            for(Iterator<Move> it = para.moveLists.iterator(); it.hasNext();){
                Move inst = it.next();

                if(!para.useTime.containsKey(inst.rd)){
                    it.remove();
                    if(inst.origin instanceof Register){
                       // System.out.println(inst);
                        if(para.useTime.get(inst.origin) > 1)
                            para.useTime.put(inst.origin, para.useTime.get(inst.origin) - 1);
                        else para.useTime.remove(inst.origin);
                    }

                    blk.addInstBeforeTerminator(new Move(inst.rd, blk, inst.origin, true));
                    hasMore = true;
                }
            }

            int size = para.moveLists.size();
            if(!hasMore){
                for (int i = 0; i < size; i++) {
                    Move inst = para.moveLists.get(i);
                    if(inst.origin != inst.rd){
                        Register mirror = new Register("mirror_" + inst.origin + "_Reg", inst.origin.type);
                        blk.addInstBeforeTerminator(new Move(mirror, blk, inst.origin, true));
                        para.useTime.put(mirror, para.useTime.get(inst.origin) - 1);
                        para.useTime.remove(inst.origin);
                        para.moveLists.forEach(mv -> {
                            if(mv.origin == inst.origin)mv.origin = mirror;
                        });
                        break;
                    }
                }
            }

            flag = false;
            for (int i = 0; i < size; i++) {
                Move inst = para.moveLists.get(i);
                if(inst.origin != inst.rd){
                    flag = true;
                    break;
                }
            }

        }
    }

}
