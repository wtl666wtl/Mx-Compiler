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
    public PhyReg zero, sp, t0, t1, t2, t3, t4, t5;
    public AsmFunction curFunc;

    public RegAlloc(AsmRootNode AsmRt){
        this.AsmRt = AsmRt;
        zero = AsmRt.phyRegs.get(0);
        sp = AsmRt.phyRegs.get(2);
        t0 = AsmRt.phyRegs.get(5);
        t1 = AsmRt.phyRegs.get(6);
        t2 = AsmRt.phyRegs.get(7);
        t3 = AsmRt.phyRegs.get(28);
        t4 = AsmRt.phyRegs.get(29);
        t5 = AsmRt.phyRegs.get(30);
    }

    public void resolveSLImm(AsmFunction func){
        func.blks.forEach(blk -> {
            for (BaseAsmInstruction inst : blk.stmts) {
                inst.resolveSLImm(stackLength);
                if(inst instanceof IType){
                    //if(((IType) inst).imm.val >= (1<<11) || ((IType) inst).imm.val < -(1<<11))
                    //    dealITypeImm((IType) inst);
                }
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
            //stackLength = Integer.min(stackLength, 2032);
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
                //if(it.imm.val >= (1<<12))dealITypeImm(it);
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
                //if(it.offset.val >= (1<<12))dealLdImm(it);
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
                //if(it.offset.val >= (1<<12))dealStImm(it);
            }
        }
    }

    public void dealITypeImm(IType it){
        it.disableForImm = true;
        if(it.imm.val > 0){
            int hi = (it.imm.val >> 12), lo = it.imm.val - (hi << 12);
            it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
            it.instForImm.add(new IType(t3, it.blk, t3, new Imm(lo), BaseAsmInstruction.calType.add) );
            it.instForImm.add(new RType(it.rd, it.blk, it.rs, t3, it.opCode));
        }else{
            it.imm.val = -it.imm.val;
            int hi = (it.imm.val >> 12), lo = it.imm.val - (hi << 12);
            it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
            it.instForImm.add(new IType(t3, it.blk, t3, new Imm(lo), BaseAsmInstruction.calType.add) );
            it.instForImm.add(new RType(t3, it.blk, zero, t3, BaseAsmInstruction.calType.sub));
            it.instForImm.add(new RType(it.rd, it.blk, it.rs, t3, it.opCode));
        }
    }

    public void dealLdImm(Ld it){
        it.disableForImm = true;
        int hi = (it.offset.val >> 12), lo = it.offset.val - (hi << 12);
        it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
        it.instForImm.add(new IType(t3, it.blk, t3, new Imm(lo), BaseAsmInstruction.calType.add) );
        it.instForImm.add(new RType(t4, it.blk, t3, it.addr, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new Ld(it.rd, it.blk, t4, new Imm(0), it.width));
    }

    public void dealStImm(St it){
        it.disableForImm = true;
        int hi = (it.offset.val >> 12), lo = it.offset.val - (hi << 12);
        it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
        it.instForImm.add(new IType(t3, it.blk, t3, new Imm(lo), BaseAsmInstruction.calType.add) );
        it.instForImm.add(new RType(t4, it.blk, t3, it.addr, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new St(it.blk, t4, it.val, new Imm(0), it.width));
    }

    public BaseAsmInstruction LdVReg(AsmBlock blk, VirtualReg r, PhyReg rd){
        Ld inst = new Ld(rd, blk, sp, new Imm(r.index * 4 + curFunc.paramStSize), r.width);
        if(r.index * 4 + curFunc.paramStSize >= (1<<11))dealLdImm(inst);
        return inst;
    }

    public BaseAsmInstruction StVReg(AsmBlock blk, VirtualReg r){
        St inst = new St(blk, sp, t2, new Imm(r.index * 4 + curFunc.paramStSize), r.width);
        if(r.index * 4 + curFunc.paramStSize >= (1<<11))dealStImm(inst);
        return inst;
    }

}
