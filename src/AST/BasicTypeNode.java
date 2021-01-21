package AST;

import Util.error.internalError;
import Util.position;

public class BasicTypeNode extends ASTNode {
    public String type;

    public BasicTypeNode(String type, position pos) {
        super(pos);
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        throw new internalError("visit basic type tool node", pos);
    }
}