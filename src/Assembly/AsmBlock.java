package Assembly;

import Assembly.AsmInstruction.*;
import Assembly.AsmOperand.Reg;
import Backend.SmartTag;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

public class AsmBlock {

    public String name;
    public ArrayList<AsmBlock> preblks = new ArrayList<>();
    public ArrayList<AsmBlock> sucblks = new ArrayList<>();
    public LinkedList<BaseAsmInstruction> stmts = new LinkedList<>();
    public int notFree = 0;
    public LinkedList<SmartTag> freeList = new LinkedList<>();
    public HashSet<Reg> liveIn =new HashSet<>(), liveOut = new HashSet<>();
    public int blkCnt = 0;

    public AsmBlock(String name, AsmRootNode AsmRt){
        this.name = name;
        for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
        for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
        for(int i = 29; i < 32; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
    }

    public void addInst(BaseAsmInstruction inst){
        stmts.add(inst);
    }

    /*public void addInst(int index, BaseAsmInstruction inst){
        stmts.add(index, inst);
    }*/

    @Override
    public String toString(){
        return name;
    }
}
