package AST;

import Util.position;
import Util.substance;

public class memberExprNode extends ExprNode {
    public ExprNode caller;
    public String member;
    public substance memberSubstance;

    public memberExprNode(ExprNode caller, String member, position pos) {
        super(pos, true);
        this.caller = caller;
        this.member = member;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
