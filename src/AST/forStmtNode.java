package AST;

import Util.position;
import MIR.*;

public class forStmtNode extends StmtNode{
    public ExprNode condition, incr, init;
    public StmtNode body;
    public Block terminalblk = null, incrblk = null;

    public forStmtNode(ExprNode init, ExprNode incr, ExprNode condition, StmtNode body, position pos) {
        super(pos);
        this.init = init;
        this.incr = incr;
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
