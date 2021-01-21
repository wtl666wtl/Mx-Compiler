package AST;

import Util.position;

public class emptyStmtNode extends StmtNode{

    public emptyStmtNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
