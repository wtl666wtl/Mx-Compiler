package AST;

import Util.position;

public class funNode extends ExprNode {
    public String funName;

    public funNode(String name, position pos) {
        super(pos, false);
        this.funName = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
