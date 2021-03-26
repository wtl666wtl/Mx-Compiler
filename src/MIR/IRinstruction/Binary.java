package MIR.IRinstruction;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ListIterator;

public class Binary extends BaseInstruction {

    public enum binaryOpType{
        add, sub, mul, sdiv, srem,
        shl, ashr, and, or, xor
    }

    public binaryOpType opCode;
    public BaseOperand lhs, rhs;

    public Binary(Register rd, Block blk, binaryOpType opCode,BaseOperand lhs, BaseOperand rhs){
        super(rd, blk);
        this.opCode = opCode;
        this.lhs = lhs;
        this.rhs = rhs;

        lhs.appear(this);
        rhs.appear(this);
        rd.defInst = this;
    }

    @Override
    public String toString(){
        return rd.toString() + " = " + opCode.toString() + " "
                + lhs.type.toString() + " " + lhs.toString() + ", " + rhs.toString();
    }

    @Override
    public void deleteSelf(boolean flag) {
        if(flag)blk.deleteInst(this);
        lhs.deleteAppear(this);
        rhs.deleteAppear(this);
    }

    @Override
    public void replaceUse(Register originOperand, BaseOperand newOperand) {
        if(lhs == originOperand)lhs = newOperand;
        if(rhs == originOperand)rhs = newOperand;
    }
}