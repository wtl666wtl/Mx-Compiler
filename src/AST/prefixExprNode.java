package AST;

import Util.position;

public class prefixExprNode extends ExprNode {
    public ExprNode src;
    public int opCode;
    //+: 0, -: 1, ~:2, ++:3, --:4, !:5

    public prefixExprNode(ExprNode src, int opCode, position pos) {
        super(pos, (opCode==3)||(opCode==4));
        this.src = src;
        this.opCode = opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
