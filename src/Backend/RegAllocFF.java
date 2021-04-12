package Backend;

import Assembly.AsmInstruction.*;
import Assembly.*;
import Assembly.AsmOperand.*;
import MIR.IRinstruction.Store;

import java.util.*;

public class RegAllocFF {

    public AsmRootNode AsmRt;
    int stackLength;
    public PhyReg zero, sp, t3;
    public LinkedList<SmartTag> freeList = new LinkedList<>();
    public int tot;

    public AsmFunction curFunc;
    public AsmBlock curblk;
    public int tvreg = 0;
    public HashMap<Integer, Integer> TVRegMap = new HashMap<>();
    public ArrayList<PhyReg> PR = new ArrayList<>();

    public RegAllocFF(AsmRootNode AsmRt){
        this.AsmRt = AsmRt;
        zero = AsmRt.phyRegs.get(0);
        sp = AsmRt.phyRegs.get(2);
        for(int i = 3; i < 10; i++){
            freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
            PR.add(AsmRt.phyRegs.get(i));
        }
        for(int i = 18; i < 28; i++){
            freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
            PR.add(AsmRt.phyRegs.get(i));
        }
        for(int i = 29; i < 32; i++){
            freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
            PR.add(AsmRt.phyRegs.get(i));
        }
        tot = freeList.size();

        t3 = AsmRt.phyRegs.get(28);
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
            tvreg = 0;
            TVRegMap.clear();
            curFunc = func;
            workFunc(func);
            stackLength += func.paramStSize + tvreg * 4;//true vreg
            if(stackLength % 16 != 0)stackLength = (stackLength / 16 + 1) * 16;
            resolveSLImm(func);
        });
    }

    public void workFunc(AsmFunction func){
        func.blks.forEach(this::workBlk);
    }

    public PhyReg SmartFind(BaseAsmInstruction it, VirtualReg vreg, boolean loadFlag, boolean moveFlag){
        vreg.useTime--;
        for(int i = tot - 1; i > -1; i--) {
            SmartTag x = freeList.get(i);
            if (x.vreg == vreg) {
                if(!loadFlag)x.dirty = true;
                freeList.remove(i);
                freeList.addLast(x);
                return x.phyReg;
            }
        }
        for(int i = tot - 1; i > -1; i--) {
            SmartTag x = freeList.get(i);
            if(x.vreg == null){
                x.vreg = vreg;
                if(loadFlag)it.preAdds.add(LdVReg(curblk, vreg, x.phyReg));
                else x.dirty = true;
                freeList.remove(i);
                freeList.addLast(x);
                return x.phyReg;
            }
        }
        for (int i = 0; i < tot; i++) {
            SmartTag x = freeList.get(i);
            if(x.vreg.useTime <= 0 && x.vreg.appearBlks.size() <= 1){
                freeList.remove(i);
                x.dirty = false;
                if(loadFlag)it.preAdds.add(LdVReg(curblk, vreg, x.phyReg));
                else x.dirty = true;
                x.vreg = vreg;
                freeList.addLast(x);
                return x.phyReg;
            }
        }
        //todo: Maybe appear@son First!
        SmartTag x = freeList.getFirst();
        freeList.removeFirst();
        if(x.dirty && (x.vreg.useTime > 0 || x.vreg.appearBlks.size() > 1))
            it.preAdds.add(StVReg(curblk, x.phyReg, x.vreg));
        x.dirty = false;
        if(loadFlag)it.preAdds.add(LdVReg(curblk, vreg, x.phyReg));
        else x.dirty = true;
        x.vreg = vreg;
        freeList.addLast(x);
        return x.phyReg;
    }

    public boolean appearAtSon(int where, SmartTag t, AsmBlock blk, BaseAsmInstruction inst){
        boolean flag = false;
        /*for(AsmBlock sucblk : blk.sucblks){
            if(t.vreg.appearBlks.contains(sucblk) && havePalace(x, sucblk)){
                flag = true;
                SmartTag x = sucblk.freeList.removeFirst();
                x.vreg = t.vreg;
                //todo many things
                inst.instForCal.add(new Mv(x.phyReg, blk, t.phyReg));
                sucblk.freeList.addLast(x);
            }
        }*/
        for(AsmBlock sucblk : blk.sucblks)
            if (t.vreg.appearBlks.contains(sucblk)) {
                flag = true;
                break;
            }
        if(!flag)return false;
        for (int i = 0; i < tot; i++){
            int pos = 0;
            for (int j = 0; j < tot; j++) {
                if(freeList.get(j).phyReg == PR.get(i)){
                    pos = j;
                    break;
                }
            }
            if(freeList.get(pos).vreg != null && freeList.get(pos) != t)continue;
            boolean OK = true;
            for(AsmBlock sucblk : blk.sucblks)
                if(t.vreg.appearBlks.contains(sucblk) && sucblk.freeList.get(i).vreg != null){
                    OK = false;
                    break;
                }
            if(OK){
                for(AsmBlock sucblk : blk.sucblks)
                    if(t.vreg.appearBlks.contains(sucblk) && sucblk.freeList.get(i).vreg != null){
                        sucblk.freeList.get(i).vreg = t.vreg;
                        sucblk.freeList.get(i).dirty = t.dirty;
                    }
                if(freeList.get(pos) == t)return true;
                inst.instForCal.add(new Mv(PR.get(i), blk, t.vreg));
                freeList.get(where).vreg = null;
                freeList.get(pos).vreg = t.vreg;
                return true;
            }
        }
        return false;
    }

    public void workBlk(AsmBlock blk){
        freeList = blk.freeList;
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
                    if (!(x.vreg != null && x.dirty && it.rs != x.phyReg && (x.vreg.useTime > 0 || x.vreg.appearBlks.size() > 1)) )
                        x.vreg = null;
                }
                for (int i = 0; i < tot; i++) {
                    SmartTag x = freeList.get(i);
                    if (x.vreg != null && x.dirty && it.rs != x.phyReg && (x.vreg.useTime > 0 || x.vreg.appearBlks.size() > 1) ) {
                        //System.out.println(x.phyReg);
                        if(appearAtSon(i, x, blk, inst))continue;
                        inst.instForCal.add(StVReg(curblk, x.phyReg, x.vreg));
                    }
                }
                freeList.clear();
                for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                for(int i = 29; i < 32; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
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
                    if (!(x.vreg != null && x.dirty && (x.vreg.useTime > 0 || x.vreg.appearBlks.size() > 1)) )
                        x.vreg = null;
                }
                for (int i = 0; i < tot; i++) {
                    SmartTag x = freeList.get(i);
                    /*if(x != null && x.vreg != null){
                    System.out.println(x.vreg.index);
                    System.out.println(x.vreg.useTime);}*/
                    if (x.vreg != null && x.dirty && (x.vreg.useTime > 0 || x.vreg.appearBlks.size() > 1)) {//System.out.println(x.phyReg);
                        //System.out.println(x.phyReg);
                        if(inst instanceof Jp && appearAtSon(i, x, blk, inst))continue;
                        inst.instForCal.add(StVReg(curblk, x.phyReg, x.vreg));
                    }
                }
                freeList.clear();
                for(int i = 3; i < 10; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                for(int i = 18; i < 28; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
                for(int i = 29; i < 32; i++)freeList.add(new SmartTag(AsmRt.phyRegs.get(i), null));
            }
        }
    }

    public void dealITypeImm(IType it){
        it.disableForImm = true;
        it.instForImm.add(new Li(t3, it.blk, new Imm(it.imm.val)) );
        it.instForImm.add(new RType(it.rd, it.blk, it.rs, t3, it.opCode));
    }

    public void dealLdImm(Ld it){
        it.disableForImm = true;
        it.instForImm.add(new Li(t3, it.blk, new Imm(it.offset.val)) );
        it.instForImm.add(new RType(t3, it.blk, t3, it.addr, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new Ld(it.rd, it.blk, t3, new Imm(0), it.width));
    }

    public void dealStImm(St it){
        it.disableForImm = true;
        it.instForImm.add(new Li(t3, it.blk, new Imm(it.offset.val)) );
        it.instForImm.add(new RType(t3, it.blk, t3, it.addr, BaseAsmInstruction.calType.add) );
        it.instForImm.add(new St(it.blk, t3, it.val, new Imm(0), it.width));
    }

    public BaseAsmInstruction LdVReg(AsmBlock blk, VirtualReg r, PhyReg rd){
        int tmp = TVRegMap.get(r.index) * 4;
        Ld inst = new Ld(rd, blk, sp, new Imm(tmp + curFunc.paramStSize), r.width);
        if(tmp + curFunc.paramStSize >= (1<<11))dealLdImm(inst);
        return inst;
    }

    public BaseAsmInstruction StVReg(AsmBlock blk, PhyReg fr, VirtualReg r){
        int tmp;
        if(TVRegMap.containsKey(r.index))tmp = TVRegMap.get(r.index) * 4;
        else{
            TVRegMap.put(r.index, tvreg++);
            tmp = (tvreg-1) * 4;
        }
        St inst = new St(blk, sp, fr, new Imm(tmp + curFunc.paramStSize), r.width);
        if(tmp + curFunc.paramStSize >= (1<<11))dealStImm(inst);
        return inst;
    }

}
