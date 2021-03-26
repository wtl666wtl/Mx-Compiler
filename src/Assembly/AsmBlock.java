package Assembly;

import Assembly.AsmInstruction.*;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.LinkedTransferQueue;

public class AsmBlock {

    public String name;
    public ArrayList<AsmBlock> preblks = new ArrayList<>();
    public ArrayList<AsmBlock> sucblks = new ArrayList<>();
    public LinkedList<BaseAsmInstruction> stmts = new LinkedList<>();

    public AsmBlock(String name){
        this.name = name;
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
