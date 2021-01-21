package AST;

import Util.position;

import java.util.ArrayList;

public class newExprNode extends ExprNode {
    public typeNode newType;
    public ArrayList<ExprNode> exprs;

    public newExprNode(typeNode type, ArrayList<ExprNode> exprs, position pos) {
        super(pos, true);
        this.newType = type;
        this.exprs = exprs;
    }

   @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
