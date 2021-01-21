package AST;

import Util.position;

public class intLiteralNode extends ExprNode {
    public int value;

    public intLiteralNode(int value, position pos) {
        super(pos, false);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
