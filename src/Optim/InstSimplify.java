package Optim;

import AST.BasicTypeNode;
import Assembly.AsmInstruction.BaseAsmInstruction;
import Assembly.AsmInstruction.IType;
import Assembly.AsmOperand.Imm;
import MIR.Block;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.Binary;
import MIR.IRinstruction.Icmp;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.ConstBool;
import MIR.IRoperand.ConstInt;
import MIR.IRoperand.ConstNull;
import MIR.rootNode;
import Util.error.internalError;
import Util.position;

import java.util.ListIterator;

public class InstSimplify {

    public rootNode rt;
    public boolean flag = false;

    public InstSimplify(rootNode rt){
        this.rt = rt;
    }

    public boolean work(){
        flag = false;
        rt.funcs.forEach((s, func) -> func.funcBlocks.forEach(this::workBlock));
        return flag;
    }

    public Integer ConstVal(BaseOperand x){
        if(x instanceof ConstInt)return ((ConstInt)x).val;
        else if(x instanceof ConstBool)return ((ConstBool)x).val ? 1 : 0;
        else if(x instanceof ConstNull)return null;
        else throw new internalError("Unexpected Operand in InstSimplify", new position(0, 0));
    }

    public int try2(int x){
        int ans = 0;
        while(x > 1 && x % 2 == 0){
            x /= 2;
            ans++;
        }
        if(x > 1)return -1;
        return ans;
    }

    public void workBlock(Block blk){
        for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            BaseInstruction inst = p.next();
            if(inst instanceof Binary){
                Binary it = (Binary) inst;
                BaseOperand ans = null;
                if(it.opCode == Binary.binaryOpType.add){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = it.lhs;
                    else if(it.lhs instanceof ConstInt && ConstVal(it.lhs) == 0)ans = it.rhs;
                } else if(it.opCode == Binary.binaryOpType.or){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = it.lhs;
                    else if(it.lhs instanceof ConstInt && ConstVal(it.lhs) == 0)ans = it.rhs;
                } else if(it.opCode == Binary.binaryOpType.xor){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = it.lhs;
                    else if(it.lhs instanceof ConstInt && ConstVal(it.lhs) == 0)ans = it.rhs;
                } else if(it.opCode == Binary.binaryOpType.and){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = new ConstInt(0, 32);
                    else if(it.lhs instanceof ConstInt && ConstVal(it.lhs) == 0)ans = new ConstInt(0, 32);
                } else if(it.opCode == Binary.binaryOpType.sub){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = it.lhs;
                } else if(it.opCode == Binary.binaryOpType.sdiv){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 1)ans = it.lhs;
                } else if(it.opCode == Binary.binaryOpType.shl){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = it.lhs;
                } else if(it.opCode == Binary.binaryOpType.ashr){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = it.lhs;
                } else if(it.opCode == Binary.binaryOpType.mul){
                    if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 1)ans = it.lhs;
                    else if(it.lhs instanceof ConstInt && ConstVal(it.lhs) == 1)ans = it.rhs;
                    else if(it.rhs instanceof ConstInt && ConstVal(it.rhs) == 0)ans = new ConstInt(0, 32);
                    else if(it.lhs instanceof ConstInt && ConstVal(it.lhs) == 0)ans = new ConstInt(0, 32);
                    else {
                        int tmp;
                        if (it.lhs instanceof ConstInt && (tmp = try2(((ConstInt) it.lhs).val)) != -1) {
                            p.set(new Binary(it.rd, blk, Binary.binaryOpType.shl, it.rhs, new ConstInt(tmp, 32)));
                            it.deleteSelf(false);
                            continue;
                        } else if (it.rhs instanceof ConstInt && (tmp = try2(((ConstInt) it.rhs).val)) != -1) {
                            p.set(new Binary(it.rd, blk, Binary.binaryOpType.shl, it.lhs, new ConstInt(tmp, 32)));
                            it.deleteSelf(false);
                            continue;
                        }
                    }
                }
                if(ans != null){
                    it.rd.replaceAllUse(ans);
                    p.remove();
                    it.deleteSelf(false);
                }
            } else if(inst instanceof Icmp){
                Icmp it = (Icmp) inst;
                if(it.opCode == Icmp.IcmpOpType.sle){
                    if(it.arg1 instanceof ConstInt){
                        it.arg1 = new ConstInt(ConstVal(it.arg1) - 1, 32);
                        it.opCode = Icmp.IcmpOpType.slt;
                    } else if(it.arg2 instanceof ConstInt){
                        it.arg2 = new ConstInt(ConstVal(it.arg2) + 1, 32);
                        it.opCode = Icmp.IcmpOpType.slt;
                    }
                } else if(it.opCode == Icmp.IcmpOpType.sge){
                    if(it.arg1 instanceof ConstInt){
                        int tmp = ConstVal(it.arg1) + 1;
                        it.arg1 = it.arg2;
                        it.arg2 = new ConstInt(tmp, 32);
                        it.opCode = Icmp.IcmpOpType.slt;
                    } else if(it.arg2 instanceof ConstInt){
                        int tmp = ConstVal(it.arg2) - 1;
                        it.arg2 = it.arg1;
                        it.arg1 = new ConstInt(tmp, 32);
                        it.opCode = Icmp.IcmpOpType.slt;
                    }
                }
            }
        }
    }

}
