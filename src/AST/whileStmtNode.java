package AST;

import MIR.Block;
import Util.position;

public class whileStmtNode extends StmtNode {
    public ExprNode condition;
    public StmtNode body;
    public Block terminalblk = null, condblk = null;

    public whileStmtNode(ExprNode condition, StmtNode body, position pos) {
        super(pos);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
