package AST;

import Util.position;

public class stringLiteralNode extends ExprNode {
    public String value;

    public stringLiteralNode(String value, position pos) {
        super(pos, false);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
