package AST;

import Util.position;

import java.util.ArrayList;

public class funCallExprNode extends ExprNode {
    public ArrayList<ExprNode> parameters;
    public ExprNode callee;

    public funCallExprNode(ExprNode callee, exprListNode paramters, position pos) {
        super(pos, false);
        this.callee = callee;
        this.parameters = paramters == null ? new ArrayList<>() : paramters.exprs;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
