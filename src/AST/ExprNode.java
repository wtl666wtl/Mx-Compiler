package AST;

import MIR.Block;
import MIR.IRoperand.BaseOperand;
import Util.type.Type;
import Util.position;

public abstract class ExprNode extends ASTNode {
    public Type type;
    public boolean canLeft;
    public Block trueblk, falseblk;
    public BaseOperand operand;

    public ExprNode(position pos, boolean canLeft) {
        super(pos);
        this.canLeft = canLeft;
    }

}
