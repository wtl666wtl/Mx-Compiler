package Backend;

import Assembly.AsmInstruction.*;
import Assembly.AsmInstruction.Br;
import Assembly.AsmInstruction.Ret;
import Assembly.AsmOperand.*;
import Assembly.*;
import Assembly.AsmInstruction.BaseAsmInstruction.*;
import MIR.*;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.*;
import MIR.IRoperand.*;
import MIR.IRtype.IRBaseType;
import MIR.IRtype.IRClassType;
import MIR.IRtype.IRPointerType;

import java.util.ArrayList;
import java.util.HashMap;

public class InstSelectorPP {

    public AsmRootNode AsmRt;
    public rootNode rt;
    public AsmBlock curblk;
    public AsmFunction curFunc;
    public int vregCounter = 0;
    public HashMap<Integer, VirtualReg> ImmMap = new HashMap<>();
    public HashMap<BaseOperand, Reg> regMap = new HashMap<>();
    public HashMap<Block, AsmBlock> blkMap = new HashMap<>();
    public HashMap<Function, AsmFunction> funcMap = new HashMap<>();
    public PhyReg sp, t3, t4, t5;

    public InstSelectorPP(AsmRootNode AsmRt){
        this.AsmRt = AsmRt;
        sp = AsmRt.phyRegs.get(2);
        t3 = AsmRt.phyRegs.get(28);
        t4 = AsmRt.phyRegs.get(29);
        t5 = AsmRt.phyRegs.get(30);
    }

    public boolean isSmall(int x){
        return (x < (1<<11)) && (x > -1*(1<<11));
    }

    public boolean isImm(BaseOperand it){
        return it instanceof ConstInt && isSmall(((ConstInt) it).val);
    }

    public calType transOpCode(Binary.binaryOpType it){
        switch (it){
            case sub: return calType.sub;
            case add: return calType.add;
            case mul: return calType.mul;
            case sdiv: return calType.div;
            case srem: return calType.rem;
            case shl: return calType.sll;
            case ashr: return calType.sra;
            case and: return calType.and;
            case or: return calType.or;
            case xor: return calType.xor;
            default: return null;
        }
    }

    public cmpType transOpCode(Icmp.IcmpOpType it){
        switch (it){
            case eq: return cmpType.eq;
            case ne: return cmpType.ne;
            case sgt: return cmpType.gt;
            case sge: return cmpType.ge;
            case slt: return cmpType.lt;
            case sle: return cmpType.le;
            default: return null;
        }
    }

    public boolean onlyForBr(Register x, Block blk){
        if(x.positions.size() == 1){
            for(BaseInstruction inst : x.positions)
                if(inst == blk.getTerminator())return true;
        }
        return false;
    }

    Reg getAsmReg(BaseOperand it){
        if(it instanceof Register || it instanceof Parameter){
            if(regMap.containsKey(it)){
                Reg t = regMap.get(it);
                if(t instanceof VirtualReg){
                    ((VirtualReg) t).useTime++;
                    ((VirtualReg) t).appearBlks.add(curblk);
                }
            }
            if(!regMap.containsKey(it)) {
                VirtualReg tmp = new VirtualReg(vregCounter++, it.type.width / 8);
                regMap.put(it, tmp);
                ((VirtualReg) tmp).useTime++;
                ((VirtualReg) tmp).appearBlks.add(curblk);
            }
            return regMap.get(it);
        } else if(it instanceof GlobalVar){
            if(!regMap.containsKey(it)){
                int pointToWidth = ((IRPointerType)it.type).pointTo.width / 8;
                String name = ((GlobalVar)it).name;
                GlobalReg reg = new GlobalReg(name, pointToWidth);
                regMap.put(it, reg);
                AsmRt.globalRegs.add(reg);
                return reg;
            }
            return regMap.get(it);
        } else if(it instanceof ConstString){
            if(!regMap.containsKey(it)){
                int pointToWidth = it.type.width / 8;
                String name = "." + ((ConstString)it).name;
                GlobalReg reg = new GlobalReg(name, pointToWidth);
                regMap.put(it, reg);
                //System.out.println(regMap.containsKey(it));
                AsmRt.constStrings.put(reg, ((ConstString)it).val);
                return reg;
            }
            return regMap.get(it);
        } else if(it instanceof ConstInt){
            int val = ((ConstInt) it).val;
            if(val == 0)return AsmRt.phyRegs.get(0);
            if(ImmMap.containsKey(val)){
                VirtualReg x = ImmMap.get(val);
                x.useTime++;
                x.appearBlks.add(curblk);
                return x;
            }
            VirtualReg reg = new VirtualReg(vregCounter++, 4);
            ((VirtualReg) reg).useTime+=2;
            ((VirtualReg) reg).appearBlks.add(curblk);
            curblk.addInst(new Li(reg, curblk, new Imm(val)));
            ImmMap.put(val, reg);
            return reg;
        } else if(it instanceof ConstBool){
            boolean val = ((ConstBool) it).val;
            if(!val)return AsmRt.phyRegs.get(0);
            if(ImmMap.containsKey(1)){
                VirtualReg x = ImmMap.get(1);
                x.useTime++;
                x.appearBlks.add(curblk);
                return x;
            }
            VirtualReg reg = new VirtualReg(vregCounter++, 4);
            ((VirtualReg) reg).useTime+=2;
            ((VirtualReg) reg).appearBlks.add(curblk);
            curblk.addInst(new Li(reg, curblk, new Imm(1)));
            ImmMap.put(1, reg);
            return reg;
        } else return AsmRt.phyRegs.get(0);
    }

    public void visitRt(rootNode rt){
        this.rt = rt;
        rt.builtInFuncs.forEach((funcName, func) ->{
            AsmFunction AsmFunc = new AsmFunction(funcName, null, null);
            AsmRt.builtInFuncs.add(AsmFunc);
            funcMap.put(func, AsmFunc);
        });
        rt.funcs.forEach((funcName, func) -> {
            //optimal? loop
            func.funcBlocks.forEach(blk -> {
                AsmBlock Asmblk = new AsmBlock("." + funcName + "_" + blk.name, AsmRt);
                blkMap.put(blk, Asmblk);
            });
            AsmFunction AsmFunc = new AsmFunction(funcName, blkMap.get(func.inblk), blkMap.get(func.outblk));
            funcMap.put(func, AsmFunc);
            func.funType.paramList.forEach(parameter -> AsmFunc.params.add(getAsmReg(parameter)));
            AsmRt.funcs.add(AsmFunc);
        });
        rt.funcs.forEach((funcName, func) -> visitFounc(func));
    }

    void visitFounc(Function func){
        curFunc = funcMap.get(func);
        vregCounter = 0;
        //func.funType.paramList.forEach(parameter -> curFunc.params.add(getAsmReg(parameter)));
        AsmBlock inblk = curFunc.inblk, outblk = curFunc.outblk;
        ArrayList<VirtualReg> calleeMap = new ArrayList<>();
        //sp
        StackLengthImm stackLength = new StackLengthImm(0);
        stackLength.order = -1;
        inblk.addInst(new IType(AsmRt.phyRegs.get(2), inblk, AsmRt.phyRegs.get(2), stackLength, calType.add));
        //if(func.name == "__init")System.out.println(vregCounter);
        AsmRt.calleeRegs.forEach(reg -> {
            VirtualReg vreg = new VirtualReg(vregCounter++, 4);
            calleeMap.add(vreg);
            inblk.addInst(new Mv(vreg, inblk, reg));
        });
        //ra
        VirtualReg vreg = new VirtualReg(vregCounter++, 4);
        ((VirtualReg) vreg).useTime++;
        ((VirtualReg) vreg).appearBlks.add(inblk);
        inblk.addInst(new Mv(vreg, inblk, AsmRt.phyRegs.get(1)));
        //if(func.name == "__init")System.out.println(vregCounter);
        //get params
        for (int i = 0; i < Integer.min(8, curFunc.params.size()); i++) {
            inblk.addInst(new Mv(curFunc.params.get(i), inblk, AsmRt.phyRegs.get(10 + i)));
        }
        int paramOffest = 0;
        for (int i = 8; i < curFunc.params.size(); i++) {
            inblk.addInst(new Ld(curFunc.params.get(i), inblk, sp,
                    new StackLengthImm(paramOffest), func.funType.paramList.get(i).type.width / 8));
            paramOffest = paramOffest + 4;
        }

        func.funcBlocks.forEach(blk -> {
            ImmMap.clear();
            visitBlk(blk);
            curFunc.blks.add(blkMap.get(blk));
        });

        for (int i = 0; i < AsmRt.calleeRegs.size(); i++) {
            outblk.addInst(new Mv(AsmRt.calleeRegs.get(i), outblk, calleeMap.get(i)));
        }
        ((VirtualReg) vreg).useTime++;
        ((VirtualReg) vreg).appearBlks.add(outblk);
        outblk.addInst(new Mv(AsmRt.phyRegs.get(1), outblk, vreg));
        outblk.addInst(new IType(AsmRt.phyRegs.get(2), outblk,
                AsmRt.phyRegs.get(2), new StackLengthImm(0), calType.add));
        outblk.addInst(new Ret(outblk, AsmRt));
        curFunc.vregCounter = vregCounter;
    }

    void visitBlk(Block blk){
        curblk = blkMap.get(blk);
        for (BaseInstruction inst : blk.stmts) {
            if (inst.deleteFlag)continue;
            //trans to LIR
            if(inst instanceof Load){
                Load it = (Load) inst;
                curblk.addInst(new Ld(getAsmReg(it.rd), curblk,getAsmReg(it.addr),
                        new Imm(0), it.rd.type.width / 8));
            } else if(inst instanceof Store){
                Store it = (Store) inst;
                Reg addr = getAsmReg(it.addr);
                if(addr instanceof GlobalReg){
                    //System.out.println("?");
                    VirtualReg tmp = new VirtualReg(vregCounter++, 4);
                    tmp.useTime++;
                    tmp.appearBlks.add(curblk);
                    AddrImm addr_high = new AddrImm((GlobalReg) addr, true);
                    AddrImm addr_low = new AddrImm((GlobalReg) addr, false);
                    curblk.addInst(new lui(tmp, curblk, addr_high));
                    curblk.addInst(new St(curblk, tmp, getAsmReg(it.storeVal),
                            addr_low, it.storeVal.type.width / 8));
                } else curblk.addInst(new St(curblk, addr, getAsmReg(it.storeVal),
                        new Imm(0), it.storeVal.type.width / 8));
            } else if(inst instanceof MIR.IRinstruction.Ret){
                MIR.IRinstruction.Ret it = (MIR.IRinstruction.Ret) inst;
                if(it.retVal != null){
                    //put retVal @ a0
                    Reg reg = getAsmReg(it.retVal);
                    if(reg instanceof GlobalReg)
                        curblk.addInst(new La(AsmRt.phyRegs.get(10), curblk, (GlobalReg)reg));
                    else curblk.addInst(new Mv(AsmRt.phyRegs.get(10), curblk, reg));
                }
            } else if(inst instanceof Binary){//TODO
                Binary it = (Binary) inst;
                //if((it.opCode == Binary.binaryOpType.mul || it.opCode == Binary.binaryOpType.add)){
                //System.out.print(it.rd.name + " ");
                //System.out.print((it.lhs) + " ");
                //System.out.println((it.rhs)); }
                //System.out.println(it);
                if(it.opCode == Binary.binaryOpType.mul || it.opCode == Binary.binaryOpType.sdiv
                    || it.opCode == Binary.binaryOpType.srem){
                        curblk.addInst(new RType(getAsmReg(it.rd), curblk,
                                getAsmReg(it.lhs), getAsmReg(it.rhs), transOpCode(it.opCode)));
                }else {
                    if (isImm(it.rhs)) {
                        if (it.opCode != Binary.binaryOpType.sub)
                            curblk.addInst(new IType(getAsmReg(it.rd), curblk,
                                    getAsmReg(it.lhs), new Imm(((ConstInt) it.rhs).val), transOpCode(it.opCode)));
                        else curblk.addInst(new IType(getAsmReg(it.rd), curblk,
                                getAsmReg(it.lhs), new Imm(-1 * ((ConstInt) it.rhs).val), calType.add));
                    } else if (isImm(it.lhs) && (it.opCode == Binary.binaryOpType.add || it.opCode == Binary.binaryOpType.and
                            || it.opCode == Binary.binaryOpType.or || it.opCode == Binary.binaryOpType.xor)) {
                        curblk.addInst(new IType(getAsmReg(it.rd), curblk,
                                getAsmReg(it.rhs), new Imm(((ConstInt)it.lhs).val), transOpCode(it.opCode)));
                    } else curblk.addInst(new RType(getAsmReg(it.rd), curblk,
                            getAsmReg(it.lhs), getAsmReg(it.rhs), transOpCode(it.opCode)));
                }
            } else if(inst instanceof Icmp){//TODO
                Icmp it = (Icmp) inst;
                //if(onlyForBr(it.rd, blk))continue;
                cmpType opCode = transOpCode(it.opCode);
                //System.out.println(it.arg1);
                if(opCode == cmpType.lt){
                    curblk.addInst(new RType(getAsmReg(it.rd), curblk,
                            getAsmReg(it.arg1), getAsmReg(it.arg2),calType.slt));
                }else if(opCode == cmpType.gt){
                    curblk.addInst(new RType(getAsmReg(it.rd), curblk,
                            getAsmReg(it.arg2), getAsmReg(it.arg1),calType.slt));
                }else if(opCode == cmpType.le){
                    VirtualReg tmp = new VirtualReg(vregCounter++, 4);
                    tmp.useTime++;
                    tmp.appearBlks.add(curblk);
                    curblk.addInst(new RType(tmp, curblk, getAsmReg(it.arg2), getAsmReg(it.arg1), calType.slt));
                    curblk.addInst(new IType(getAsmReg(it.rd), curblk, tmp, new Imm(1), calType.xor));
                }else if(opCode == cmpType.ge){
                    VirtualReg tmp = new VirtualReg(vregCounter++, 4);
                    tmp.useTime++;
                    tmp.appearBlks.add(curblk);
                    curblk.addInst(new RType(tmp, curblk, getAsmReg(it.arg1), getAsmReg(it.arg2), calType.slt));
                    curblk.addInst(new IType(getAsmReg(it.rd), curblk, tmp, new Imm(1), calType.xor));
                }else{
                    /*VirtualReg tmp = new VirtualReg(vregCounter++, 4);
                    tmp.useTime++;
                    tmp.appearBlks.add(curblk);
                    curblk.addInst(new RType(tmp, curblk, getAsmReg(it.arg1), getAsmReg(it.arg2), calType.slt));
                    VirtualReg tmp2 = new VirtualReg(vregCounter++, 4);
                    tmp2.useTime++;
                    tmp2.appearBlks.add(curblk);
                    curblk.addInst(new RType(tmp2, curblk, getAsmReg(it.arg2), getAsmReg(it.arg1), calType.slt));
                    if(opCode == cmpType.ne)
                        curblk.addInst(new RType(getAsmReg(it.rd), curblk, tmp, tmp2, calType.or));
                    else{
                        VirtualReg tmp3 = new VirtualReg(vregCounter++, 4);
                        tmp3.useTime++;
                        tmp3.appearBlks.add(curblk);
                        curblk.addInst(new RType(tmp3, curblk, tmp, tmp2, calType.or));
                        curblk.addInst(new IType(getAsmReg(it.rd), curblk, tmp3, new Imm(1), calType.xor));
                    }*/
                    VirtualReg tmp = new VirtualReg(vregCounter++, 4);
                    curblk.addInst(new RType(tmp, curblk, getAsmReg(it.arg1), getAsmReg(it.arg2), calType.xor));
                    if(opCode == cmpType.ne)
                        curblk.addInst(new IType(getAsmReg(it.rd), curblk, tmp, new Imm(0), calType.sne));
                    else curblk.addInst(new IType(getAsmReg(it.rd), curblk, tmp, new Imm(0), calType.seq));
                }
            } else if(inst instanceof MIR.IRinstruction.Br){
                MIR.IRinstruction.Br it = (MIR.IRinstruction.Br)inst;
                if(it.cond == null){//jp
                    curblk.addInst(new Jp(curblk, blkMap.get(it.iftrue)));
                } else {//br
                    /*if(it.cond instanceof Register && onlyForBr((Register) it.cond, blk)){
                        if(((Register) it.cond).defInst instanceof Icmp){
                            Icmp cmp = (Icmp) ((Register) it.cond).defInst;
                            if(cmp.opCode == Icmp.IcmpOpType.eq){
                                curblk.addInst(new Br(curblk, getAsmReg(cmp.arg1), getAsmReg(cmp.arg2),
                                        cmpType.eq, blkMap.get(it.iftrue)));
                            } else if(cmp.opCode == Icmp.IcmpOpType.ne){
                                curblk.addInst(new Br(curblk, getAsmReg(cmp.arg1), getAsmReg(cmp.arg2),
                                        cmpType.ne, blkMap.get(it.iftrue)));
                            } else if(cmp.opCode == Icmp.IcmpOpType.slt){
                                curblk.addInst(new Br(curblk, getAsmReg(cmp.arg1), getAsmReg(cmp.arg2),
                                        cmpType.lt, blkMap.get(it.iftrue)));
                            } else if(cmp.opCode == Icmp.IcmpOpType.sle){
                                curblk.addInst(new Br(curblk, getAsmReg(cmp.arg2), getAsmReg(cmp.arg1),
                                        cmpType.ge, blkMap.get(it.iftrue)));
                            } else if(cmp.opCode == Icmp.IcmpOpType.sgt){
                                curblk.addInst(new Br(curblk, getAsmReg(cmp.arg2), getAsmReg(cmp.arg1),
                                        cmpType.lt, blkMap.get(it.iftrue)));
                            } else if(cmp.opCode == Icmp.IcmpOpType.sge){
                                curblk.addInst(new Br(curblk, getAsmReg(cmp.arg1), getAsmReg(cmp.arg2),
                                        cmpType.ge, blkMap.get(it.iftrue)));
                            }
                            curblk.addInst(new Jp(curblk, blkMap.get(it.iffalse)));
                            continue;
                        }
                    }*/
                    curblk.addInst(new Bz(curblk, getAsmReg(it.cond), cmpType.eq, blkMap.get(it.iffalse)));
                    curblk.addInst(new Jp(curblk, blkMap.get(it.iftrue)));
                }
            } else if(inst instanceof Malloc){
                Malloc it = (Malloc) inst;
                curblk.addInst(new Mv(AsmRt.phyRegs.get(10), curblk, getAsmReg(it.length)));
                curblk.addInst(new Cal(curblk, AsmRt, funcMap.get(rt.builtInFuncs.get("malloc"))));
                curblk.addInst(new Mv(getAsmReg(it.rd), curblk, AsmRt.phyRegs.get(10)));
            } else if(inst instanceof Call){
                Call it = (Call) inst;
                //System.out.println(it.params.size());
                for (int j = 0; j < Integer.min(8, it.params.size()); j++) {
                    Reg reg = getAsmReg(it.params.get(j));
                    if(reg instanceof GlobalReg)
                        curblk.addInst(new La(AsmRt.phyRegs.get(10 + j), curblk, (GlobalReg)reg));
                    else curblk.addInst(new Mv(AsmRt.phyRegs.get(10 + j), curblk, reg));
                }
                int paramStSize = 0;
                for (int j = 8; j < it.params.size(); j++) {
                    Reg reg = getAsmReg(it.params.get(j));
                    curblk.addInst(new St(curblk, AsmRt.phyRegs.get(2), reg,
                            new Imm(paramStSize), it.params.get(j).type.width / 8));
                    paramStSize = paramStSize + 4;
                }
                if(curFunc.paramStSize < paramStSize) curFunc.paramStSize = paramStSize;
                curblk.addInst(new Cal(curblk, AsmRt, funcMap.get(it.callee)));
                if(it.rd != null)curblk.addInst(new Mv(getAsmReg(it.rd), curblk, AsmRt.phyRegs.get(10)));
            } else if(inst instanceof Zext){
                Zext it = (Zext) inst;
                Reg reg = getAsmReg(it.orign);
                if(reg instanceof GlobalReg){
                    Reg tmp = getAsmReg(it.rd);
                    if(tmp instanceof VirtualReg)((VirtualReg) tmp).usedTag = true;
                    curblk.addInst(new La(tmp, curblk, (GlobalReg)reg));
                }
                else curblk.addInst(new Mv(getAsmReg(it.rd), curblk, reg));
            } else if(inst instanceof GetElementPtr){
                GetElementPtr it = (GetElementPtr) inst;
                VirtualReg indexTmp = new VirtualReg(vregCounter++, 4);
                indexTmp.useTime=2;
                indexTmp.appearBlks.add(curblk);
                //indexTmp.usedTag = false;
                //indexTmp = target + stepType * stepNum
                if(it.stepNum instanceof ConstInt){
                    ConstInt stepNum = (ConstInt) it.stepNum;
                    if(stepNum.val == 0){
                        Reg target = getAsmReg(it.target);
                        if(target instanceof GlobalReg) {
                            curblk.addInst(new La(indexTmp, curblk, (GlobalReg) target));
                        }
                        else {
                            curblk.addInst(new Mv(indexTmp, curblk, target));
                        }
                    }else{
                        curblk.addInst(new RType(indexTmp, curblk, getAsmReg(it.target),
                                getAsmReg(new ConstInt(stepNum.val * it.stepType.width / 8, 32)), calType.add));
                    }
                } else {
                    VirtualReg tmp = new VirtualReg(vregCounter++, 4);
                    tmp.useTime=2;
                    tmp.appearBlks.add(curblk);
                    tmp.usedTag = false;
                    curblk.addInst(new RType(tmp, curblk, getAsmReg(it.stepNum),
                            getAsmReg(new ConstInt(it.stepType.width / 8, 32)), calType.mul));
                    curblk.addInst(new RType(indexTmp, curblk, getAsmReg(it.target), tmp, calType.add));
                }
                //index = indexTmp + offset
                VirtualReg index;
                if(it.offset == null)index = indexTmp;
                else{
                    assert it.offset instanceof ConstInt;
                    int offset = ((ConstInt)it.offset).val;
                    if(offset == 0)index = indexTmp;
                    else{
                        index = new VirtualReg(vregCounter++, 4);
                        index.useTime++;
                        index.appearBlks.add(curblk);
                        IRBaseType type = ((IRPointerType)it.target.type).pointTo;
                        if(type instanceof IRClassType)offset = ((IRClassType)type).getMemberOffset(offset) / 8;
                        else offset = 0;
                        if(isSmall(offset))
                            curblk.addInst(new IType(index, curblk, indexTmp,
                                    new Imm(offset), calType.add));
                        else curblk.addInst(new RType(index, curblk, indexTmp,
                                getAsmReg(new ConstInt(offset, 32)), calType.add));
                    }
                }

                if(regMap.containsKey(it.rd)) {
                    Reg tmp = regMap.get(it.rd);
                    if(tmp instanceof VirtualReg) {
                        ((VirtualReg)tmp).useTime++;
                        ((VirtualReg)tmp).appearBlks.add(curblk);
                    }
                    index.useTime++;
                    curblk.addInst(new Mv(tmp, curblk, index));
                }
                else regMap.put(it.rd, index);
            } else if(inst instanceof BitCast){
                BitCast it = (BitCast) inst;
                Reg reg = getAsmReg(it.origin);
                if(reg instanceof GlobalReg){
                    Reg tmp = getAsmReg(it.rd);
                    //if(tmp instanceof VirtualReg)((VirtualReg) tmp).usedTag = true;
                    curblk.addInst(new La(tmp, curblk, (GlobalReg)reg));
                }
                else curblk.addInst(new Mv(getAsmReg(it.rd), curblk, reg));
            } else if(inst instanceof Move){
                Move it = (Move) inst;
                if(it.origin instanceof GlobalVar || it.origin instanceof ConstString){
                    Reg tmp = getAsmReg(it.rd);
                    //if(tmp instanceof VirtualReg)((VirtualReg) tmp).usedTag = true;
                    curblk.addInst(new La(tmp, curblk, (GlobalReg)getAsmReg(it.origin)));
                }else if(it.origin instanceof ConstInt){
                    Reg tmp = getAsmReg(it.rd);
                    //if(tmp instanceof VirtualReg)((VirtualReg) tmp).usedTag = true;
                    curblk.addInst(new Li(tmp, curblk, new Imm(((ConstInt) it.origin).val)));
                }else if(it.origin instanceof ConstBool && ((ConstBool) it.origin).val){
                    Reg tmp = getAsmReg(it.rd);
                    //if(tmp instanceof VirtualReg)((VirtualReg) tmp).usedTag = true;
                    curblk.addInst(new Li(tmp, curblk, new Imm(1)));
                }else {
                    Reg tmp = getAsmReg(it.rd);
                    //if(tmp instanceof VirtualReg)((VirtualReg) tmp).usedTag = true;
                    curblk.addInst(new Mv(tmp, curblk,
                            it.origin instanceof ConstBool ? AsmRt.phyRegs.get(0) :getAsmReg(it.origin)));
                }
            }
        }
        //build AsmBlock's suc/pre
        blk.sucblks.forEach(sucblk -> {
            curblk.sucblks.add(blkMap.get(sucblk));
            //System.out.println(blk.name);
            //System.out.println(sucblk.name);
            //System.out.println(blkMap.get(sucblk));
            blkMap.get(sucblk).preblks.add(curblk);
        });
    }
}
