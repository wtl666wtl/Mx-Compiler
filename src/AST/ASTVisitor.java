package AST;

public interface ASTVisitor {
    void visit(RootNode it);

    void visit(classDefNode it);
    void visit(funDefNode it);
    void visit(varDefNode it);
    void visit(varDefListNode it);

    void visit(blockStmtNode it);
    void visit(exprStmtNode it);
    void visit(returnStmtNode it);
    void visit(breakStmtNode it);
    void visit(continueStmtNode it);
    void visit(ifStmtNode it);
    void visit(forStmtNode it);
    void visit(whileStmtNode it);
    void visit(emptyStmtNode it);

    void visit(exprListNode it);
    void visit(typeNode it);

    void visit(arrayExprNode it);
    void visit(assignExprNode it);
    void visit(binaryExprNode it);
    void visit(prefixExprNode it);
    void visit(suffixExprNode it);

    void visit(thisExprNode it);
    void visit(funCallExprNode it);
    void visit(methodExprNode it);
    void visit(memberExprNode it);
    void visit(newExprNode it);

    void visit(funNode it);
    void visit(varNode it);

    void visit(intLiteralNode it);
    void visit(boolLiteralNode it);
    void visit(nullLiteralNode it);
    void visit(stringLiteralNode it);
}
