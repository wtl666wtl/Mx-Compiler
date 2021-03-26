package Frontend;

import AST.*;
import Util.error.*;
import Util.scope.*;
import Util.type.*;
import Util.substance;

public class TypeCollector implements ASTVisitor {
    public globalScope gScope;
    public Scope nowScope;

    public TypeCollector(globalScope gScope) {
        nowScope = this.gScope = gScope;
    }

    @Override public void visit(RootNode it) {
        if (!it.defs.isEmpty())
            it.defs.forEach(dd -> { if(!(dd instanceof varDefNode)) dd.accept(this);});
    }

    @Override public void visit(classDefNode it) {
        classType nowClass = (classType)gScope.getTypeFromName(it.name, it.pos);
        nowScope = nowClass.localScope;
        it.methods.forEach(md -> md.accept(this));
        it.constructors.forEach(cd-> cd.accept(this));
        nowScope = nowScope.fScope;
    }

    @Override public void visit(funDefNode it) {
        funType fun = it.decl;
        if (it.isConstructor) fun.funType = new constructorType();
        else fun.funType = gScope.makeType(it.type);
        nowScope = new funScope(nowScope);
        it.parameters.forEach(pd -> pd.accept(this));
        fun.localScope = (funScope)nowScope;
        nowScope = nowScope.fScope;
    }

    @Override public void visit(varDefNode it) {
        substance var = new substance(it.name, gScope.makeType(it.typeName), false);
        if (var.type.isVoid()) throw new semanticError("variable is void", it.pos);
        it.varSubstance = var;
        ((funScope)nowScope).addParameter(var, it.pos);
    }

    @Override public void visit(varDefListNode it){}
    @Override public void visit(blockStmtNode it){}
    @Override public void visit(exprStmtNode it){}
    @Override public void visit(ifStmtNode it){}
    @Override public void visit(forStmtNode it){}
    @Override public void visit(whileStmtNode it){}
    @Override public void visit(returnStmtNode it){}
    @Override public void visit(breakStmtNode it){}
    @Override public void visit(continueStmtNode it){}
    @Override public void visit(emptyStmtNode it){}
    @Override public void visit(exprListNode it){}
    @Override public void visit(typeNode it){}
    @Override public void visit(arrayExprNode it){}
    @Override public void visit(assignExprNode it){}
    @Override public void visit(binaryExprNode it){}
    @Override public void visit(prefixExprNode it){}
    @Override public void visit(suffixExprNode it){}
    @Override public void visit(thisExprNode it){}
    @Override public void visit(funCallExprNode it){}
    @Override public void visit(methodExprNode it){}
    @Override public void visit(memberExprNode it){}
    @Override public void visit(newExprNode it){}
    @Override public void visit(funNode it){}
    @Override public void visit(varNode it){}
    @Override public void visit(intLiteralNode it){}
    @Override public void visit(boolLiteralNode it){}
    @Override public void visit(nullLiteralNode it){}
    @Override public void visit(stringLiteralNode it){}
}
