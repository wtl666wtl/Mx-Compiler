package AST;

import Util.position;
import Util.scope.*;

abstract public class ASTNode {
    public position pos;
    public Scope scope;

    public ASTNode(position pos) {
        this.pos = pos;
    }

    abstract public void accept(ASTVisitor visitor);
}
