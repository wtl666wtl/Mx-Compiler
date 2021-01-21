package AST;

import Util.position;

public class typeNode extends ASTNode {

    public String baseTypeName;
    public int dim;

    public typeNode(String baseTypeName, int dim, position pos) {
        super(pos);
        this.baseTypeName = baseTypeName;
        this.dim = dim;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
