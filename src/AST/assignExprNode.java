package AST;

import Util.position;

public class assignExprNode extends ExprNode{
    public ExprNode lhs, rhs;

    public assignExprNode(ExprNode lhs, ExprNode rhs, boolean canLeft, position pos) {
        super(pos, canLeft);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
