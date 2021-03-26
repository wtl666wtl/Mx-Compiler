package Frontend;

import MIR.*;
import MIR.IRtype.*;
import AST.*;
import Util.error.*;
import Util.type.*;
import Util.scope.*;
import Util.*;

import java.util.HashMap;

public class SymbolCollector implements ASTVisitor {

    public globalScope gScope;
    public Scope nowScope = null;
    public rootNode rt;

    public SymbolCollector(globalScope gScope, rootNode rt) {
        nowScope = this.gScope = gScope;
        this.rt = rt;
    }

    @Override public void visit(RootNode it) {
        it.defs.forEach(dd -> dd.accept(this));
    }

    @Override public void visit(classDefNode it) {
        if (!(nowScope instanceof globalScope)) throw new internalError("class not define in globalScope", it.pos);
        classType newClass = new classType(it.name);
        nowScope = new classScope(nowScope);
        rt.newClassTypes.put(it.name, new IRClassType(it.name));
        it.members.forEach(md -> md.accept(this));
        it.methods.forEach(md -> md.accept(this));
        it.constructors.forEach(cd -> cd.accept(this));
        newClass.localScope = nowScope;
        nowScope = nowScope.fScope;
        if(gScope.containsMethod(it.name, false)) throw new semanticError("same name", it.pos);
        gScope.defineClass(it.name, newClass, it.pos);
    }

    @Override public void visit(funDefNode it) {
        funType newFun = new funType(it.name);
        if (nowScope != gScope) newFun.isMethod = true;
        if (nowScope == gScope && gScope.hasType(it.name)) throw new semanticError("same name", it.pos);
        it.decl = newFun;
        if (it.isConstructor) nowScope.defineConstructor(newFun, it.pos);
        else nowScope.defineMethod(it.name, newFun, it.pos);
    }

    @Override public void visit(varDefNode it) {}
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
