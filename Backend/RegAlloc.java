package Backend;

import Assembly.AsmInstruction.*;
import Assembly.*;
import Assembly.AsmOperand.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class RegAlloc {

    public AsmRootNode AsmRt;
    int stackLength;
    public PhyReg sp, t0, t1, t2;
    public AsmFunction curFunc;

    public RegAlloc(AsmRootNode AsmRt){
        this.AsmRt = AsmRt;
        sp = AsmRt.phyRegs.get(2);
        t0 = AsmRt.phyRegs.get(5);
        t1 = AsmRt.phyRegs.get(6);
        t2 = AsmRt.phyRegs.get(7);
    }

    public void resolveSLImm(AsmFunction func){
        func.blks.forEach(blk -> {
            for (BaseAsmInstruction inst : blk.stmts) {
                inst.resolveSLImm(stackLength);
            }
        });
    }

    public void work(){
        AsmRt.funcs.forEach(func -> {
            stackLength = 0;
            curFunc = func;
            workFunc(func);
            stackLength += func.paramStSize + func.vregCounter * 4;
            if(stackLength % 16 != 0)stackLength = (stackLength / 16 + 1) * 16;
            resolveSLImm(func);
        });
    }

    public void workFunc(AsmFunction func){
        func.blks.forEach(this::workBlk);
    }

    public void workBlk(AsmBlock blk){
        //for(Iterator<BaseAsmInstruction> p = blk.stmts.iterator(); p.hasNext();){
        //for (int i = 0; i < blk.stmts.size(); i++) {
        for(BaseAsmInstruction inst : blk.stmts){
            //BaseAsmInstruction inst = p.next();
            //BaseAsmInstruction inst = blk.stmts.get(i);
            if(inst instanceof Bz){
                Bz it = (Bz)inst;
                if(!(it.rs instanceof VirtualReg))continue;
                inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs, t0);
                it.rs = t0;
                //i++;
            }else if(inst instanceof La){
                La it = (La)inst;
                if(!(it.rd instanceof VirtualReg))continue;
                inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                it.rd = t2;
                //i++;
            }else if(inst instanceof Li){
                Li it = (Li)inst;
                if(!(it.rd instanceof VirtualReg))continue;
                inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                it.rd = t2;
                //i++;
            }else if(inst instanceof lui){
                lui it = (lui)inst;
                if(!(it.rd instanceof VirtualReg))continue;
                inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                it.rd = t2;
                //i++;
            }else if(inst instanceof Mv){
                Mv it = (Mv)inst;
                if(it.rs instanceof VirtualReg){
                    inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs, t0);
                    it.rs = t0;
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = t2;
                    //i++;
                }
            }else if(inst instanceof RType){
                RType it = (RType) inst;
                if(it.rs1 instanceof VirtualReg){
                    inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs1, t0);
                    it.rs1 = t0;
                    //i++;
                }
                if(it.rs2 instanceof VirtualReg){
                    inst.preAdd2 = LdVReg(blk, (VirtualReg)it.rs2, t1);
                    it.rs2 = t1;
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = t2;
                    //i++;
                }
            }else if(inst instanceof IType){
                IType it = (IType) inst;
                if(it.rs instanceof VirtualReg){
                    inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs, t0);
                    it.rs = t0;
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = t2;
                    //i++;
                }
            }else if(inst instanceof Ld){
                Ld it = (Ld) inst;
                if(it.addr instanceof VirtualReg){
                    inst.preAdd1 = LdVReg(blk, (VirtualReg)it.addr, t0);
                    it.addr = t0;
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = t2;
                    //i++;
                }
            }else if(inst instanceof St){
                St it = (St) inst;
                if(it.addr instanceof VirtualReg){
                    inst.preAdd1 = LdVReg(blk, (VirtualReg)it.addr, t0);
                    it.addr = t0;
                    //i++;
                }
                if(it.val instanceof VirtualReg){
                    inst.preAdd2 = LdVReg(blk, (VirtualReg)it.val, t1);
                    it.val = t1;
                    //i++;
                }
            }
        }
    }

    public BaseAsmInstruction LdVReg(AsmBlock blk, VirtualReg r, PhyReg rd){
        return new Ld(rd, blk, sp, new Imm(r.index * 4 + curFunc.paramStSize), r.width);
    }

    public BaseAsmInstruction StVReg(AsmBlock blk, VirtualReg r){
        //if(t2)
        return new St(blk, sp, t2, new Imm(r.index * 4 + curFunc.paramStSize), r.width);
    }

}
