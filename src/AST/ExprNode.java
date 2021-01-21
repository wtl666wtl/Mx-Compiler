package AST;

import Util.type.Type;
import Util.position;

public abstract class ExprNode extends ASTNode {
    public Type type;
    public boolean canLeft;

    public ExprNode(position pos, boolean canLeft) {
        super(pos);
        this.canLeft = canLeft;
    }

    /*public boolean isAssignable() {
        return false;
    }*/
}
