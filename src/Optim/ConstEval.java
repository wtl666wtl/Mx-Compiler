package Optim;

import Assembly.AsmOperand.Reg;
import MIR.Block;
import MIR.IRinstruction.*;
import MIR.IRoperand.*;
import MIR.IRtype.IRIntType;
import MIR.rootNode;
import Util.error.internalError;
import Util.position;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

public class ConstEval {

    public rootNode rt;
    public boolean flag = false;

    public ConstEval(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(this::workInBlock));
        return flag;
    }

    public boolean isConstant(BaseOperand x){
        return x instanceof Constant && !(x instanceof ConstString);
    }

    public Integer ConstVal(BaseOperand x){
        if(x instanceof ConstInt)return ((ConstInt)x).val;
        else if(x instanceof ConstBool)return ((ConstBool)x).val ? 1 : 0;
        else if(x instanceof ConstNull)return null;
        else throw new internalError("Unexpected Operand in ConstEval", new position(0, 0));
    }

    public void workInBlock(Block blk){
        for(Iterator<Map.Entry<Register, Phi>> p = blk.Phis.entrySet().iterator(); p.hasNext();){
            Phi phi = p.next().getValue();
            if(phi.myInfo.blks.size() == 1){
                phi.rd.replaceAllUse(phi.myInfo.vals.get(0));
                p.remove();
                phi.deleteSelf(false);
                flag = true;
            }
        }
        for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next();
            if(inst instanceof Binary){
                Binary it = (Binary)inst;
                BaseOperand lhs = it.lhs, rhs = it.rhs;
                Register rd = it.rd;
                if(isConstant(lhs) && isConstant(rhs)){
                    Integer l = ConstVal(lhs), r = ConstVal(rhs);
                    boolean Int = lhs instanceof ConstInt;
                    switch (it.opCode) {
                        case add -> rd.replaceAllUse(new ConstInt(l + r, 32));
                        case sub -> rd.replaceAllUse(new ConstInt(l - r, 32));
                        case mul -> rd.replaceAllUse(new ConstInt(l * r, 32));
                        case sdiv -> {
                            if(r != 0)rd.replaceAllUse(new ConstInt(l / r, 32));
                            else continue;
                        }
                        case srem -> {
                            if(r != 0)rd.replaceAllUse(new ConstInt(l % r, 32));
                            else continue;
                        }
                        case shl -> rd.replaceAllUse(new ConstInt(l << r, 32));
                        case ashr -> rd.replaceAllUse(new ConstInt(l >> r, 32));
                        case and -> rd.replaceAllUse(Int ? new ConstInt(l & r, 32) : new ConstBool((l & r) == 1));
                        case or -> rd.replaceAllUse(Int ? new ConstInt(l | r, 32) : new ConstBool((l | r) == 1));
                        case xor -> rd.replaceAllUse(Int ? new ConstInt(l ^ r, 32) : new ConstBool((l ^ r) == 1));
                        default -> throw new internalError("Unexpected opCode in ConstEval", new position(0, 0));
                    }
                    p.remove();
                    inst.deleteSelf(false);
                    flag = true;
                }
            } else if(inst instanceof Icmp){
                Icmp it = (Icmp)inst;
                BaseOperand lhs = it.arg1, rhs = it.arg2;
                Register rd = it.rd;
                if(isConstant(lhs) && isConstant(rhs)){
                    Integer l = ConstVal(lhs), r = ConstVal(rhs);
                    switch (it.opCode) {
                        case eq -> rd.replaceAllUse(new ConstBool(l.equals(r)));
                        case ne -> rd.replaceAllUse(new ConstBool(!l.equals(r)));
                        case sgt -> rd.replaceAllUse(new ConstBool(l > r));
                        case sge -> rd.replaceAllUse(new ConstBool(l >= r));
                        case slt -> rd.replaceAllUse(new ConstBool(l < r));
                        case sle -> rd.replaceAllUse(new ConstBool(l <= r));
                        default -> throw new internalError("Unexpected opCode in ConstEval", new position(0, 0));
                    }
                    p.remove();
                    inst.deleteSelf(false);
                    flag = true;
                }
            } else if(inst instanceof Zext){
                Zext it = (Zext) inst;
                BaseOperand arg = it.orign;
                Register rd = it.rd;
                if(isConstant(arg)){
                    Integer x = ConstVal(arg);
                    if(rd.type instanceof IRIntType){
                        rd.replaceAllUse(new ConstInt(x, rd.type.width));
                    } else {
                        rd.replaceAllUse(new ConstBool(x == 1));
                    }
                    p.remove();
                    inst.deleteSelf(false);
                    flag = true;
                }
            } else if(inst instanceof Br){
                Br it = (Br) inst;
                if(it.cond == null)continue;
                BaseOperand cond = it.cond;
                if(cond instanceof ConstBool){
                    blk.deleteTerminator();
                    inst.deleteSelf(false);
                    if(((ConstBool) cond).val){
                        blk.addTerminator(new Br(blk, null, it.iftrue, null));
                    }else{
                        blk.addTerminator(new Br(blk, null, it.iffalse, null));
                    }
                    break;
                }
            }
        }
    }

}
