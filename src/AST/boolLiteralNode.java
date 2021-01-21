package AST;

import Util.position;

public class boolLiteralNode extends ExprNode{

    public boolean value;

    public boolLiteralNode(boolean value, position pos) {
        super(pos, false);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
