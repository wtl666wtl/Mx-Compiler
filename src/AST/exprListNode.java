package AST;

import Util.position;

import java.util.ArrayList;

public class exprListNode extends ASTNode {
    public ArrayList<ExprNode> exprs = new ArrayList<>();

    public exprListNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
