package Optim;

import MIR.Block;
import MIR.Function;
import MIR.IRinstruction.Br;
import MIR.IRinstruction.Phi;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;
import MIR.Loop;
import MIR.phiInfo;

import java.util.*;

public class LoopCollector {

    public Function func;
    public boolean addPreHeader;
    public HashMap<Block, Loop> loopMap = new HashMap<>();
    public HashSet<Loop> rootLoops = new HashSet<>();
    public HashSet<Block> hasVisited = new HashSet<>();
    public Stack<Loop> loopStack = new Stack<>();

    public LoopCollector(Function func, boolean addPreHeader){
        this.func = func;
        this.addPreHeader = addPreHeader;
    }

    public void workFunc(){
        func.funcBlocks.forEach(blk -> {
            for(Block sucblk : blk.sucblks){
                if(blk.tryDom(sucblk)){
                    collectLoop(blk, sucblk);
                    break;
                }
            }
        });
        if(addPreHeader)loopMap.forEach(this::addPreHeader);
        loopMap.forEach((h, l) -> l.tails.forEach(t -> getLoopBlocks(t, h)));
        visit(func.inblk);
    }

    public void collectLoop(Block tail, Block head){
        if(!loopMap.containsKey(head)){
            Loop loop = new Loop();
            loopMap.put(head, loop);
        }
        loopMap.get(head).tails.add(tail);
    }

    public void addPreHeader(Block head, Loop loop){
        ArrayList<Block> preblks = new ArrayList<>(head.preblks);
        preblks.removeAll(loop.tails);
        if(preblks.size() == 1)loop.preHead = preblks.get(0);
        else{
            Block preHead = new Block( head.name + "_loopPreHead");
            func.funcBlocks.add(preHead);
            for(Iterator<Map.Entry<Register, Phi>> p = head.Phis.entrySet().iterator(); p.hasNext();){
                Map.Entry<Register, Phi> entry = p.next();
                Phi phi = entry.getValue(), phiCopy = null;
                boolean canRemove = true;

                phiInfo phiInfo = new phiInfo(phi.myInfo);
                for(int i = 0; i < phiInfo.vals.size(); i++){
                    Block blk = phiInfo.blks.get(i);
                    BaseOperand val = phiInfo.vals.get(i);
                    if(!loop.tails.contains(blk)){
                        if(phiCopy == null)
                            phiCopy = new Phi(new Register(phi.rd.name + "_preHeadPhiReg",phi.rd.type),
                                    preHead, new phiInfo());
                        phiCopy.addOrigin(val, blk);
                        phiInfo.vals.remove(i);
                        phiInfo.blks.remove(i);
                        i--;
                    } else canRemove = false;
                }

                if(canRemove){
                    p.remove();
                    if(phiCopy != null)
                        phi.rd.replaceAllUse(phiCopy.rd);
                } else if(phiCopy != null) phi.addOrigin(phiCopy.rd, preHead);
            }

            preblks.forEach(preblk -> preblk.changeSucblk(head, preHead));
            preHead.addTerminator(new Br(preHead, null, head, null));
            loop.preHead = preHead;
        }
    }

    public void getLoopBlocks(Block tail, Block head){
        HashSet<Block> inLoopBlocks = new HashSet<>();
        Queue<Block> Q = new LinkedList<>();
        inLoopBlocks.add(head);
        inLoopBlocks.add(tail);
        Q.offer(tail);
        while(!Q.isEmpty()){
            Block nowblk = Q.poll();
            nowblk.preblks.forEach(preblk -> {
                if(!inLoopBlocks.contains(preblk)){
                    Q.offer(preblk);
                    inLoopBlocks.add(preblk);
                }
            });
        }
        loopMap.get(head).loopBlocks.addAll(inLoopBlocks);
    }

    public void visit(Block blk){
        hasVisited.add(blk);
        while(!loopStack.isEmpty() && !loopStack.peek().loopBlocks.contains(blk)) loopStack.pop();

        if(loopMap.containsKey(blk)){
            Loop loop = loopMap.get(blk);
            if(loopStack.isEmpty())rootLoops.add(loop);
            else loopStack.peek().childLoops.add(loop);
            loopStack.push(loop);
        }
        blk.loopLayers = loopStack.size();

        blk.sucblks.forEach(sucblk -> {
            if(!hasVisited.contains(sucblk))
                visit(sucblk);
        });
    }

}
