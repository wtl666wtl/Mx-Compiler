package AST;

import Util.position;

public class binaryExprNode extends ExprNode {
    public ExprNode lhs, rhs;
    public enum opType {
        Star, Div, Mod, LeftShift, RightShift, And, Or, Caret,
        //*,/,%,<<,>>,&,|,^
        Minus, Plus, Less, Greater, LessEqual, GreaterEqual,
        //-,+,==,!=,<,>,<=,>=
         AndAnd, OrOr, Equal, NotEqual,
        //&&,||,==,!=
    }
    public opType opCode;

    public binaryExprNode(ExprNode lhs, ExprNode rhs, opType opCode, position pos) {
        super(pos, false);
        this.lhs = lhs;
        this.rhs = rhs;
        this.opCode = opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
