package Backend;

import Assembly.AsmInstruction.*;
import Assembly.*;
import Assembly.AsmOperand.*;
import MIR.IRinstruction.Store;

import java.util.*;

public class RegAllocPlus {

    public AsmRootNode AsmRt;
    int stackLength;
    public PhyReg zero, sp, t3, t4, t5;
    public LinkedList<SmartTag> freeList = new LinkedList<>();
    public int tot = 0;

    public AsmFunction curFunc;
    public AsmBlock curblk;

    public RegAllocPlus(AsmRootNode AsmRt){
        this.AsmRt = AsmRt;
        zero = AsmRt.phyRegs.get(0);
        sp = AsmRt.phyRegs.get(2);
        for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
        for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
        freeList.add(new SmartTag(AsmRt.phyRegs.get(31), null));
        tot = freeList.size();

        t3 = AsmRt.phyRegs.get(28);
        t4 = AsmRt.phyRegs.get(29);
        t5 = AsmRt.phyRegs.get(30);
    }

    public void resolveSLImm(AsmFunction func){
        func.blks.forEach(blk -> {
            for (BaseAsmInstruction inst : blk.stmts) {
                inst.resolveSLImm(stackLength);
                if(inst instanceof IType){
                    if(((IType) inst).imm.val >= (1<<11) || ((IType) inst).imm.val < -(1<<11))
                        dealITypeImm((IType) inst);
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

    public PhyReg SmartFind(BaseAsmInstruction it, VirtualReg vreg, boolean loadFlag, boolean moveFlag){
        for(int i = tot - 1; i > -1; i--) {
            SmartTag x = freeList.get(i);
            if (x.vreg == vreg) {
                if(!loadFlag)x.dirty = true;
                freeList.remove(i);
                freeList.addLast(x);
                return x.phyReg;
            }
            if(x.vreg == null){
                x.vreg = vreg;
                if(loadFlag)it.preAdds.add(LdVReg(curblk, vreg, x.phyReg));
                else x.dirty = true;
                freeList.remove(i);
                freeList.addLast(x);
                return x.phyReg;
            }
        }
        SmartTag x = freeList.getFirst();
        freeList.removeFirst();
        if(x.dirty)it.preAdds.add(StVReg(curblk, x.phyReg, x.vreg));
        x.dirty = false;
        if(loadFlag)it.preAdds.add(LdVReg(curblk, vreg, x.phyReg));
        else x.dirty = true;
        x.vreg = vreg;
        freeList.addLast(x);
        return x.phyReg;
    }



    public void workBlk(AsmBlock blk){
        freeList.clear();
        //System.out.println(blk.name);
        for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
        for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
        freeList.add(new SmartTag(AsmRt.phyRegs.get(31), null));
        curblk = blk;
        //for(Iterator<BaseAsmInstruction> p = blk.stmts.iterator(); p.hasNext();){
        //for (int i = 0; i < blk.stmts.size(); i++) {
        for(BaseAsmInstruction inst : blk.stmts){
            //BaseAsmInstruction inst = p.next();
            //BaseAsmInstruction inst = blk.stmts.get(i);
            if(inst instanceof Bz){
                Bz it = (Bz)inst;
                if(it.rs instanceof VirtualReg)
                    it.rs = SmartFind(it, (VirtualReg)it.rs, true, false);
                //it.rs = t0;
                //i++;
                for (int i = 0; i < tot; i++) {
                    SmartTag x = freeList.get(i);
                    if (x.vreg != null && x.dirty && it.rs != x.phyReg) {
                        //System.out.println(x.phyReg);
                        inst.instForCal.add(StVReg(curblk, x.phyReg, x.vreg));
                    }
                }
                freeList.clear();
                for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                freeList.add(new SmartTag(AsmRt.phyRegs.get(31), null));
            }else if(inst instanceof La){
                La it = (La)inst;
                if(!(it.rd instanceof VirtualReg))continue;
                //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                it.rd = SmartFind(it, (VirtualReg)it.rd, false, false);
                //i++;
            }else if(inst instanceof Li){
                Li it = (Li)inst;
                if(!(it.rd instanceof VirtualReg))continue;
                //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                it.rd = SmartFind(it, (VirtualReg)it.rd, false, false);
                //i++;
            }else if(inst instanceof lui){
                lui it = (lui)inst;
                if(!(it.rd instanceof VirtualReg))continue;
                //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                it.rd = SmartFind(it, (VirtualReg)it.rd, false, false);
                //i++;
            }else if(inst instanceof Mv){
                Mv it = (Mv)inst;
                if(it.rs instanceof VirtualReg){
                    //inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs, t0);
                    it.rs = SmartFind(it, (VirtualReg)it.rs, true, false);
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = SmartFind(it, (VirtualReg)it.rd, false, true);
                    //i++;
                }
            }else if(inst instanceof RType){
                RType it = (RType) inst;
                if(it.rs1 instanceof VirtualReg){
                    //inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs1, t0);
                    it.rs1 = SmartFind(it, (VirtualReg)it.rs1, true, false);
                    //i++;
                }
                if(it.rs2 instanceof VirtualReg){
                    //inst.preAdd2 = LdVReg(blk, (VirtualReg)it.rs2, t1);
                    it.rs2 = SmartFind(it, (VirtualReg)it.rs2, true, false);
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = SmartFind(it, (VirtualReg)it.rd, false, false);
                    //i++;
                }
            }else if(inst instanceof IType){
                IType it = (IType) inst;
                if(it.rs instanceof VirtualReg){
                    //inst.preAdd1 = LdVReg(blk, (VirtualReg)it.rs, t0);
                    it.rs = SmartFind(it, (VirtualReg)it.rs, true, false);
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = SmartFind(it, (VirtualReg)it.rd, false, false);
                    //i++;
                }
                if(it.imm.val >= (1<<11))dealITypeImm(it);
            }else if(inst instanceof Ld){
                Ld it = (Ld) inst;
                if(it.addr instanceof VirtualReg){
                    //inst.preAdd1 = LdVReg(blk, (VirtualReg)it.addr, t0);
                    it.addr = SmartFind(it, (VirtualReg)it.addr, true, false);
                    //i++;
                }
                if(it.rd instanceof VirtualReg){
                    //inst.sucAdd1 = StVReg(blk, (VirtualReg)it.rd);
                    it.rd = SmartFind(it, (VirtualReg)it.rd, false, false);
                    //i++;
                }
                if(it.offset.val >= (1<<11))dealLdImm(it);
            }else if(inst instanceof St){
                St it = (St) inst;
                if(it.addr instanceof VirtualReg){
                    //inst.preAdd1 = LdVReg(blk, (VirtualReg)it.addr, t0);
                    it.addr = SmartFind(it, (VirtualReg)it.addr, true, false);
                    //i++;
                }
                if(it.val instanceof VirtualReg){
                    //inst.preAdd2 = LdVReg(blk, (VirtualReg)it.val, t1);
                    it.val = SmartFind(it, (VirtualReg)it.val, true, false);
                    //i++;
                }
                if(it.offset.val >= (1<<11))dealStImm(it);
            }else if(inst instanceof Cal || inst instanceof Jp){
                // maybe + if(inst instanceof Cal && ((Cal) inst).isBuiltIn) continue;
               // System.out.println("------");
                for (int i = 0; i < tot; i++) {
                    SmartTag x = freeList.get(i);

                    if (x.vreg != null && x.dirty) {//System.out.println(x.phyReg);
                        //System.out.println(x.phyReg);
                        inst.instForCal.add(StVReg(curblk, x.phyReg, x.vreg));
                    }
                }
                freeList.clear();
                for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                freeList.add(new SmartTag(AsmRt.phyRegs.get(31), null));
            }
        }
    }

    public void dealITypeImm(IType it){
        it.disableForImm = true;
        if(it.imm.val > 0){
            int hi = (it.imm.val >> 12), lo = (it.imm.val - (hi << 12)) >> 1;
            it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
            it.instForImm.add(new Li(t5, it.blk, new Imm(lo)) );
            it.instForImm.add(new IType(t5, it.blk, t5, new Imm(1), BaseAsmInstruction.calType.sll) );
            if((it.imm.val & 1) == 1)
                it.instForImm.add(new IType(t3, it.blk, t3, new Imm(1), BaseAsmInstruction.calType.add) );
            it.instForImm.add(new RType(t3, it.blk, t3, t5, BaseAsmInstruction.calType.add) );
            it.instForImm.add(new RType(it.rd, it.blk, it.rs, t3, it.opCode));
        }else{
            it.imm.val = -it.imm.val;
            int hi = (it.imm.val >> 12), lo = (it.imm.val - (hi << 12)) >> 1;
            it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
            it.instForImm.add(new Li(t5, it.blk, new Imm(lo)) );
            it.instForImm.add(new IType(t5, it.blk, t5, new Imm(1), BaseAsmInstruction.calType.sll) );
            if((it.imm.val & 1) == 1)
                it.instForImm.add(new IType(t3, it.blk, t3, new Imm(1), BaseAsmInstruction.calType.add) );
            it.instForImm.add(new RType(t3, it.blk, t3, t5, BaseAsmInstruction.calType.add) );
            it.instForImm.add(new RType(t3, it.blk, zero, t3, BaseAsmInstruction.calType.sub));
            it.instForImm.add(new RType(it.rd, it.blk, it.rs, t3, it.opCode));
        }
    }

    public void dealLdImm(Ld it){
        it.disableForImm = true;
        int hi = (it.offset.val >> 12), lo = (it.offset.val - (hi << 12)) >> 1;
        it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
        it.instForImm.add(new Li(t5, it.blk, new Imm(lo)) );
        it.instForImm.add(new IType(t5, it.blk, t5, new Imm(1), BaseAsmInstruction.calType.sll) );
        if((it.offset.val & 1) == 1)
            it.instForImm.add(new IType(t3, it.blk, t3, new Imm(1), BaseAsmInstruction.calType.add) );
        it.instForImm.add(new RType(t3, it.blk, t3, t5, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new RType(t4, it.blk, t3, it.addr, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new Ld(it.rd, it.blk, t4, new Imm(0), it.width));
    }

    public void dealStImm(St it){
        it.disableForImm = true;
        int hi = (it.offset.val >> 12), lo = (it.offset.val - (hi << 12)) >> 1;
        it.instForImm.add(new lui(t3, it.blk, new Imm(hi)) );
        it.instForImm.add(new Li(t5, it.blk, new Imm(lo)) );
        it.instForImm.add(new IType(t5, it.blk, t5, new Imm(1), BaseAsmInstruction.calType.sll) );
        if((it.offset.val & 1) == 1)
            it.instForImm.add(new IType(t3, it.blk, t3, new Imm(1), BaseAsmInstruction.calType.add) );
        it.instForImm.add(new RType(t3, it.blk, t3, t5, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new RType(t4, it.blk, t3, it.addr, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new St(it.blk, t4, it.val, new Imm(0), it.width));
    }

    public BaseAsmInstruction LdVReg(AsmBlock blk, VirtualReg r, PhyReg rd){
        Ld inst = new Ld(rd, blk, sp, new Imm(r.index * 4 + curFunc.paramStSize), r.width);
        if(r.index * 4 + curFunc.paramStSize >= (1<<11))dealLdImm(inst);
        return inst;
    }

    public BaseAsmInstruction StVReg(AsmBlock blk, PhyReg fr, VirtualReg r){
        St inst = new St(blk, sp, fr, new Imm(r.index * 4 + curFunc.paramStSize), r.width);
        if(r.index * 4 + curFunc.paramStSize >= (1<<11))dealStImm(inst);
        return inst;
    }

}
