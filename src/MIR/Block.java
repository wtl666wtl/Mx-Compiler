package MIR;

import MIR.IRinstruction.*;

import java.util.*;

import MIR.IRoperand.Register;
import Util.error.internalError;
import Util.position;

public class Block {

    public LinkedList<BaseInstruction> stmts = new LinkedList<>();
    public ArrayList<Block> preblks = new ArrayList<>();
    public ArrayList<Block> sucblks = new ArrayList<>();
    public String name;
    public boolean isTerminated = false;
    public HashMap<Register, Phi> Phis = new HashMap<>();
    public int loopLayers = 0;
    public Block iDom = null;
    public HashSet<Block> domFrontiers = new HashSet<>();
    //ListIterator<BaseInstruction>p = stmts.listIterator();
    public Block(String name) {
        this.name = name;
    }

    public void deleteInst(BaseInstruction inst){
        if(inst instanceof Phi)Phis.remove(inst.rd);
        /*else if(inst instanceof Br || inst instanceof Ret) {
            deleteTerminator();
        }*/
        //no such case now!
    }

    public BaseInstruction getTerminator(){
        if(!isTerminated)throw new internalError("no terminator",new position(0,0));
        return stmts.getLast();
    }

    public BaseInstruction getHead(){
        return stmts.isEmpty() ? null : stmts.getFirst();
    }

    public void addInstAtStart(BaseInstruction inst){
        stmts.addFirst(inst);
    }

    public void addInst(BaseInstruction inst){
        stmts.add(inst);
    }

    public void addInstBeforeTerminator(BaseInstruction inst){
        BaseInstruction terminator = stmts.removeLast();
        stmts.add(inst);
        stmts.add(terminator);
    }

    public void addTerminator(BaseInstruction Tinst){
        isTerminated = true;
        stmts.add(Tinst);
        if(Tinst instanceof Br){
            Block dest = ((Br)Tinst).iftrue;
            sucblks.add(dest);
            dest.preblks.add(this);
            if(((Br)Tinst).cond != null){
                dest = ((Br)Tinst).iffalse;
                sucblks.add(dest);
                dest.preblks.add(this);
            }
        }
    }

    public void deleteTerminator(){
        if(!isTerminated)return;
        isTerminated = false;
        BaseInstruction oldTerminator = stmts.getLast();
        if(oldTerminator instanceof Br){
            Block trueblk = ((Br)oldTerminator).iftrue;
            trueblk.preblks.remove(this);
            trueblk.Phis.forEach((reg, phi) -> phi.deleteBlock(this));
            sucblks.remove(trueblk);
            if(((Br)oldTerminator).cond != null){
                Block falseblk = ((Br)oldTerminator).iffalse;
                falseblk.preblks.remove(this);
                falseblk.Phis.forEach((reg, phi) -> phi.deleteBlock(this));
                sucblks.remove(falseblk);
            }
        }
        stmts.removeLast();
        oldTerminator.deleteSelf(false);
    }

    public boolean returnTerminated(){
        if(!isTerminated)return false;
        return getTerminator() instanceof Ret;
    }

    public void addPhi(Phi phiInst){
        Phis.put(phiInst.rd, phiInst);
    }

    public void changeSucblk(Block origin, Block newblk){
        Br inst = (Br) getTerminator();
        if(inst.cond == null){
            deleteTerminator();
            addTerminator(new Br(this, null, newblk, null));
        }else{
            Br newTerminator;
            if(inst.iftrue == origin){
                newTerminator = new Br(this, inst.cond, newblk, inst.iffalse);
            }else{
                newTerminator = new Br(this, inst.cond, inst.iftrue, newblk);
            }
            deleteTerminator();
            addTerminator(newTerminator);
        }
    }

    public void inlineSplit(Block after, BaseInstruction pos){
        sucblks.forEach(sucblk ->{
            sucblk.Phis.forEach((register, phi) -> {
                for(int i = 0; i < phi.myInfo.vals.size(); i++){
                    if(phi.myInfo.blks.get(i).equals(this))
                        phi.myInfo.blks.set(i, after);
                }
            });
            sucblk.preblks.remove(this);
            sucblk.preblks.add(after);
        });
        after.sucblks = sucblks;
        sucblks = new ArrayList<>();

        isTerminated = false;
        after.isTerminated = true;
        boolean flag = false;
        for(ListIterator<BaseInstruction> p = stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next();
            if(inst == pos)flag = true;
            if(flag){
                p.remove();
                if(inst == pos){
                    inst.deleteSelf(false);
                    continue;
                }
                inst.blk = after;
                after.addInst(inst);
            }
        }
        //System.out.println(name);
        //System.out.println(after.stmts.size());
    }

    public void inlineMerge(Block blk){
        sucblks = blk.sucblks;
        blk.sucblks.forEach(sucblk -> {
            sucblk.Phis.forEach((register, phi) -> {
                for(int i = 0; i < phi.myInfo.vals.size(); i++){
                    if(phi.myInfo.blks.get(i).equals(blk))
                        phi.myInfo.blks.set(i, this);
                }
            });
            sucblk.preblks.remove(blk);
            //if(name.equals("main_in"))System.out.println("%%%    " + sucblk.name + " " + sucblk.preblks.size());
            sucblk.preblks.add(this);
        });
        isTerminated = blk.isTerminated;
        for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next();
            inst.blk = this;
            addInst(inst);
            p.remove();
        }
    }

    public boolean tryDom(Block blk){
        Block dom = iDom;
        while(dom != null){
            if(dom == blk)return true;
            else dom = dom.iDom;
        }
        return false;
    }

}
