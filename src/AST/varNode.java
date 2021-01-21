package AST;

import Util.substance;
import Util.position;

public class varNode extends ExprNode {
    public String name;
    public substance varSubstance;

    public varNode(String name, position pos) {
        super(pos, true);
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
