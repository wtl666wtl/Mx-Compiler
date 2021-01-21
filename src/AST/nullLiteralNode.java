package AST;

import Util.position;

public class nullLiteralNode extends ExprNode{

    public nullLiteralNode(position pos) {
        super(pos, false);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
