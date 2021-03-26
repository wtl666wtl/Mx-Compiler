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
            mid.addTerminator(new Br(mid, null, blk2, null));

            blk2.Phis.forEach((reg, phi) -> {
                for(int i = 0;i < phi.myInfo.blks.size(); i++){
                    if(phi.myInfo.blks.get(i) == blk1)
                        phi.myInfo.blks.set(i, mid);
                }
            });
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
        blkPhis.forEach(this::solveBlkPhi);

        //This is for optimal

        /*HashSet<Block> canMix = new HashSet<>();
        func.funcBlocks.forEach(blk -> {
            BaseInstruction headInst = blk.getHead();
            if(headInst instanceof Br && ((Br)headInst).cond == null)canMix.add(blk);//jmp
        });

        canMix.forEach(blk -> {
            Block suc = blk;
            do {
                suc = ((Br) suc.getTerminator()).iftrue;
            } while (canMix.contains(suc));
        });

        func.funcBlocks.removeAll(canMix);*/
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
