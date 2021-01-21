package AST;

import Util.position;
import Util.type.*;

public class thisExprNode extends ExprNode{

    public classType theClass;

    public thisExprNode(position pos) {
        super(pos, false);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}