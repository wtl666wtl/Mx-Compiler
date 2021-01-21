package AST;

import Util.position;
import Util.substance;

public class varDefNode extends StmtNode {
    public typeNode typeName;
    public String name;
    public ExprNode init;
    public substance varSubstance;

    public varDefNode(String name, ExprNode init, position pos) {
        super(pos);
        this.name = name;
        this.init = init;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
