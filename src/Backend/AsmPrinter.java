package Backend;

import Assembly.AsmBlock;
import Assembly.AsmFunction;
import Assembly.AsmInstruction.BaseAsmInstruction;
import Assembly.AsmInstruction.Br;
import Assembly.AsmInstruction.Bz;
import Assembly.AsmInstruction.Jp;
import Assembly.AsmOperand.GlobalReg;
import Assembly.AsmRootNode;
import MIR.IRinstruction.BaseInstruction;

import java.io.PrintStream;
import java.util.HashSet;

public class AsmPrinter {

    public AsmRootNode AsmRt;
    PrintStream defaultOut;
    AsmFunction curFunc;
    int curFuncBlkCnt = 0;

    public AsmPrinter(AsmRootNode AsmRt, PrintStream defaultOut){
        this.AsmRt = AsmRt;
        this.defaultOut = defaultOut;
    }

    public void print(){
        defaultOut.println("\t.text");
        AsmRt.funcs.forEach(this::printFunc);
        AsmRt.globalRegs.forEach(this::printGReg);
        AsmRt.constStrings.forEach(this::printStr);
    }

    public void printFunc(AsmFunction func){
        //System.out.println(func.name);
        //System.out.println(func.blks.size());
        //System.out.println(func.vregCounter);
        defaultOut.println("\t.globl\t" + func.name);
        defaultOut.println("\t.p2align\t1");
        defaultOut.println("\t.type\t" + func.name +",@function");
        defaultOut.println(func.name + ":");
        curFunc = func;
        curFuncBlkCnt = 0;
        func.blks.forEach(blk -> blk.name = "." + curFunc.name + "_b." + (blk.blkCnt = curFuncBlkCnt++));
        func.blks.forEach(this::printBlk);

        defaultOut.println("\t.size\t" + func.name + ", " + ".-" + func.name + "\n");
    }

    public HashSet<AsmBlock> hasVisited = new HashSet<>();

    public void printBlk(AsmBlock blk){
        if(hasVisited.contains(blk))return;
        hasVisited.add(blk);
        defaultOut.println(blk.name + ": ");
        for (BaseAsmInstruction i : blk.stmts) {
            if(i.preAdd1 != null)printInst(i.preAdd1);//defaultOut.println("\t" + i.preAdd1.toString());
            if(i.preAdd2 != null)printInst(i.preAdd2);//defaultOut.println("\t" + i.preAdd2.toString());
            if(i.preAdds.size() > 0){
                for (BaseAsmInstruction j : i.preAdds) {
                    printInst(j);
                }
            }
            if(i.instForCal.size() > 0){
                for (BaseAsmInstruction j : i.instForCal) {
                    printInst(j);
                }
            }
            if(i instanceof Br && ((Br)i).destblk.name.equals("." + curFunc.name + "_b." + (i.blk.blkCnt + 1))){
                Jp j = (Jp)blk.stmts.getLast();
                Br it = (Br) i;
                AsmBlock tmp = it.destblk;
                it.destblk = j.destBlk;
                j.destBlk = tmp;
                if(it.opCode == BaseAsmInstruction.cmpType.ne)it.opCode = BaseAsmInstruction.cmpType.eq;
                else if(it.opCode == BaseAsmInstruction.cmpType.eq)it.opCode = BaseAsmInstruction.cmpType.ne;
                else if(it.opCode == BaseAsmInstruction.cmpType.lt)it.opCode = BaseAsmInstruction.cmpType.ge;
                else if(it.opCode == BaseAsmInstruction.cmpType.ge)it.opCode = BaseAsmInstruction.cmpType.lt;
                printInst(i);
                printInst(j);
                break;
            }
            if(i instanceof Bz && ((Bz)i).destblk.name.equals("." + curFunc.name + "_b." + (i.blk.blkCnt + 1))){
                Jp j = (Jp)blk.stmts.getLast();
                Bz it = (Bz) i;
                AsmBlock tmp = it.destblk;
                it.destblk = j.destBlk;
                j.destBlk = tmp;
                if(it.opCode == BaseAsmInstruction.cmpType.eq)it.opCode = BaseAsmInstruction.cmpType.ne;
                printInst(i);
                printInst(j);
                break;
            }
            printInst(i);//defaultOut.println("\t" + i.toString());
            if(i.sucAdd1 != null)printInst(i.sucAdd1);//defaultOut.println("\t" + i.sucAdd1.toString());
        }
    }

    public void printInst(BaseAsmInstruction inst){
        if(!inst.disableForImm){
            if(inst instanceof Jp && ((Jp)inst).destBlk.name.equals("." + curFunc.name + "_b." + (inst.blk.blkCnt + 1)))return;
            defaultOut.println("\t" + inst.toString());
            return;
        }
        for (BaseAsmInstruction i : inst.instForImm) {
            defaultOut.println("\t" + i.toString());
        }
    }

    public void printGReg(GlobalReg GReg){
        defaultOut.println("\t.type\t" + GReg.name + ",@object");
        defaultOut.println("\t.section\t.bss");
        defaultOut.println("\t.globl\t" + GReg.name);
        defaultOut.println("\t.p2align\t2");
        defaultOut.println(GReg.name + ":");
        defaultOut.println(".L" + GReg.name + "$local:");
        defaultOut.println("\t.word\t0");
        defaultOut.println("\t.size\t" + GReg.name + ", 4\n");
    }

    public void printStr(GlobalReg GReg, String s){
        defaultOut.println("\t.type\t" + GReg.name + ",@object");
        defaultOut.println("\t.section\t.rodata");
        defaultOut.println(GReg.name + ":");
        String str = s.replace("\\", "\\\\");
        str = str.replace("\n", "\\n");
        str = str.replace("\0", "");
        str = str.replace("\t", "\\t");
        str = str.replace("\"", "\\\"");
        defaultOut.println("\t.asciz\t\"" + str + "\"");
        defaultOut.println("\t.size\t" + GReg.name + ", " + s.length() + "\n");
    }

}
