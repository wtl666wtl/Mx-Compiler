package AST;

import Util.position;
import Util.type.funType;

import java.util.ArrayList;

public class funDefNode extends ASTNode {
    public String name;
    public typeNode type;
    public blockStmtNode body;
    public boolean isConstructor,isClassMethod;
    public ArrayList<varDefNode> parameters;
    public funType decl;

    public funDefNode(String name, position pos, boolean isConstructor,
                  typeNode type, blockStmtNode body, ArrayList<varDefNode> parameters) {
        super(pos);
        this.name = name;
        this.isConstructor = isConstructor;
        this.type = type;
        this.body = body;
        this.parameters = parameters;
        this.isClassMethod = false;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
