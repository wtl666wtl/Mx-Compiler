package AST;

import Util.position;

public class methodExprNode extends ExprNode{
    public ExprNode caller;
    public String method;
    public methodExprNode(ExprNode caller, String method, position pos) {
        super(pos, false);
        this.caller = caller;
        this.method = method;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
