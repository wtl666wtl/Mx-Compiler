package AST;

import Util.position;

import java.util.ArrayList;

public class classDefNode extends ASTNode {
    public String name;
    public ArrayList<varDefNode> members = new ArrayList<>();
    public ArrayList<funDefNode> methods = new ArrayList<>();
    public ArrayList<funDefNode> constructors = new ArrayList<>();
    public boolean existConstructor;

    public classDefNode(position pos, String name,
                        ArrayList<varDefNode> members, ArrayList<funDefNode> methods,
                        ArrayList<funDefNode> constructors, boolean existConstructor
                        ) {
        super(pos);
        this.name = name;
        this.members = members;
        this.methods = methods;
        this.constructors = constructors;
        this.existConstructor = existConstructor;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
