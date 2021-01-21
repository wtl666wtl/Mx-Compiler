package AST;

import Util.position;

import java.util.ArrayList;

public class varDefListNode extends StmtNode {
    public ArrayList<varDefNode> varDefs = new ArrayList<>();

    public varDefListNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
