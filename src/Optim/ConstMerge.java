package Optim;

import MIR.Block;
import MIR.Function;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.Binary;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.ConstBool;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.ConstNull;
import MIR.rootNode;
import Util.error.internalError;
import Util.position;

import java.util.ListIterator;

public class ConstMerge {//too many meaningless addi-inst in inline-adv

    public rootNode rt;
    public boolean flag = false;
    public Function curFunc = null;

    public ConstMerge(rootNode rt){
        this.rt = rt;
    }
    public Integer ConstVal(BaseOperand x){
        if(x instanceof ConstInt)return ((ConstInt)x).val;
        else throw new internalError("Unexpected Operand in ConstMerge", new position(0, 0));
    }

    void workBlk(Block blk){
        for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next(), nxt;
            if(p.hasNext()){
                nxt = p.next();
                p.previous();
                p.previous();
                p.next();
            } else break;
            if(inst instanceof Binary && nxt instanceof Binary){
                Binary b1 = (Binary) inst, b2 = (Binary) nxt;
                if(b1.rd == b2.lhs && b1.rd.positions.size() == 1 && b1.rhs instanceof ConstInt && b2.rhs instanceof ConstInt){
                    if(b1.opCode == Binary.binaryOpType.add && b2.opCode == Binary.binaryOpType.add){
                        int tmp = ConstVal(b1.rhs) + ConstVal(b2.rhs);
                        //if(curFunc.name.equals("globalFunction_add2"))System.out.println(tmp);
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(tmp, 32);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                    else if(b1.opCode == Binary.binaryOpType.mul && b2.opCode == Binary.binaryOpType.mul){
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(ConstVal(b1.rhs) * ConstVal(b2.rhs), 32);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                    else if(b1.opCode == Binary.binaryOpType.sub && b2.opCode == Binary.binaryOpType.sub){
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(ConstVal(b1.rhs) + ConstVal(b2.rhs), 32);
                        //System.out.println(b2);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                    else if(b1.opCode == Binary.binaryOpType.add && b2.opCode == Binary.binaryOpType.sub){
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(-ConstVal(b1.rhs) + ConstVal(b2.rhs), 32);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                    else if(b1.opCode == Binary.binaryOpType.sub && b2.opCode == Binary.binaryOpType.add){
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(-ConstVal(b1.rhs) + ConstVal(b2.rhs), 32);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                    else if(b1.opCode == Binary.binaryOpType.shl && b2.opCode == Binary.binaryOpType.shl){
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(ConstVal(b1.rhs) + ConstVal(b2.rhs), 32);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                    else if(b1.opCode == Binary.binaryOpType.ashr && b2.opCode == Binary.binaryOpType.ashr){
                        b2.lhs.deleteAppear(b2);
                        b1.lhs.deleteAppear(b1);
                        b2.lhs = b1.lhs;
                        b2.lhs.appear(b2);
                        b2.rhs = new ConstInt(ConstVal(b1.rhs) + ConstVal(b2.rhs), 32);
                        p.remove();
                        b1.deleteSelf(false);
                        flag = true;
                    }
                }
            }
        }
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> {
            curFunc = func;
            func.funcBlocks.forEach(this::workBlk);
        });
        //System.out.println(flag);
        return flag;
    }

}
