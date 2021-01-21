package AST;

import Util.position;

public class suffixExprNode extends ExprNode {
    public ExprNode src;
    public int opCode;
    //++: 0, --: 1

    public suffixExprNode(ExprNode src, int opCode, position pos) {
        super(pos, false);
        this.src = src;
        this.opCode = opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
