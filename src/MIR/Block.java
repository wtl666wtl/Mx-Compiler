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
        //may not need removeself?
    }

    public boolean returnTerminated(){
        if(!isTerminated)return false;
        return getTerminator() instanceof Ret;
    }

    public void addPhi(Phi phiInst){
        Phis.put(phiInst.rd, phiInst);
    }

}
