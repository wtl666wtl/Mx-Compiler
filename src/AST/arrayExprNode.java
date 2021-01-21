package AST;

import Util.position;

public class arrayExprNode extends ExprNode{
    public ExprNode base, width;

    public arrayExprNode(ExprNode base, ExprNode width, position pos) {
        super(pos, base.canLeft);
        this.base = base;
        this.width = width;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
