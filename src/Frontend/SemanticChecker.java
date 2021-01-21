package Frontend;

import AST.*;
import Util.scope.*;
import Util.type.*;
import Util.error.*;
import Util.*;

import java.util.ArrayList;
import java.util.Stack;

public class SemanticChecker implements ASTVisitor {
    public Scope nowScope;
    public globalScope gScope;

    public classType nowClass = null;
    public Type nowReturnType = null;
    public funDefNode nowFun;
    public boolean hasReturn = false;
    public boolean forClassMember = false;

    public Stack<ASTNode> loops = new Stack<>();

    public SemanticChecker(globalScope gScope) {
        nowScope = this.gScope = gScope;
    }

    @Override public void visit(RootNode it) {
        nowScope = gScope;
        if (!gScope.containsMethod("main", true)) throw new semanticError("cannot find main()", it.pos);
        if (!it.defs.isEmpty()) {
            forClassMember = true;
            it.defs.forEach(dd -> { if (dd instanceof classDefNode) dd.accept(this); });
            forClassMember = false;
            it.defs.forEach(dd -> dd.accept(this));
        }
    }

    @Override public void visit(classDefNode it) {
        classType newClass = (classType) gScope.getTypeFromName(it.name, it.pos);
        nowScope = newClass.localScope;
        nowClass = newClass;
        if (forClassMember) it.members.forEach(md -> md.accept(this));
        if (!forClassMember) {
            it.methods.forEach(md -> md.accept(this));
            it.constructors.forEach(cd -> cd.accept(this));
        }
        nowClass = null;
        nowScope = nowScope.fScope;
    }

    @Override public void visit(funDefNode it) {
        if (it.isConstructor) {
            if (!it.name.equals(nowClass.name)) throw new semanticError("funDef error", it.pos);
            nowReturnType = gScope.voidType;
        } else nowReturnType = it.decl.funType;
        nowFun = it;
        hasReturn = false;
        nowScope = it.decl.localScope;
        it.body.accept(this);
        nowScope = nowScope.fScope;
        if (it.name.equals("main")) {
            hasReturn = true;
            if (it.parameters.size() > 0) throw new semanticError("mainDef error", it.pos);
            if (!nowReturnType.isInt()) throw new semanticError("mainDef error", it.pos);
        }
        if (!hasReturn && !nowReturnType.isVoid()) throw new semanticError("funDef error", it.pos);
        nowReturnType = null;
        nowFun = null;
    }

    @Override public void visit(varDefNode it) {
        Type varType = gScope.makeType(it.typeName);
        substance var = new substance(it.name, varType);
        it.varSubstance = var;
        if(var.type.isVoid()) throw new semanticError("varDef error", it.pos);
        if (it.init != null) {
            it.init.accept(this);
            if (!it.init.type.isSame(varType)) throw new semanticError("varDef error", it.init.pos);
        }
        nowScope.defineMember(it.name, var, it.pos);
    }

    @Override public void visit(varDefListNode it) {
        it.varDefs.forEach(vd -> vd.accept(this));
    }

    @Override public void visit(blockStmtNode it) {
        if (!it.stmts.isEmpty()) {
            nowScope = new Scope(nowScope);
            it.stmts.forEach(sd -> sd.accept(this));
            nowScope = nowScope.fScope;
        }
    }

    @Override public void visit(exprStmtNode it) {
        it.expr.accept(this);
    }

    @Override public void visit(returnStmtNode it) {
        hasReturn = true;
        if (it.value != null) {
            it.value.accept(this);
            if (!it.value.type.isSame(nowReturnType)) throw new semanticError("return error", it.pos);
        } else if(!nowReturnType.isVoid()) throw new semanticError("return error", it.pos);
        it.dest = nowFun;
    }

    @Override public void visit(breakStmtNode it) {
        if (loops.isEmpty()) throw new semanticError("break error", it.pos);
        it.dest = loops.peek();
    }

    @Override public void visit(continueStmtNode it) {
        if (loops.isEmpty()) throw new semanticError("continue error", it.pos);
        it.dest = loops.peek();
    }

    @Override public void visit(ifStmtNode it) {
        it.condition.accept(this);
        if (!it.condition.type.isBool()) throw new semanticError("if error: wrong type", it.condition.pos);
        it.thenStmt.accept(this);
        if (it.elseStmt != null) it.elseStmt.accept(this);
    }

    @Override public void visit(whileStmtNode it) {
        it.condition.accept(this);
        if (!it.condition.type.isBool()) throw new semanticError("while error: wrong type", it.condition.pos);
        loops.push(it);
        it.body.accept(this);
        loops.pop();
    }

    @Override public void visit(forStmtNode it) {
        if (it.init != null){
            it.init.accept(this);
        }
        if (it.incr != null){
            it.incr.accept(this);
        }
        if (it.condition != null) {
            it.condition.accept(this);
            if (!it.condition.type.isBool()) throw new semanticError("for error: wrong type", it.condition.pos);
        }
        loops.push(it);
        it.body.accept(this);
        loops.pop();
    }

    @Override public void visit(assignExprNode it) {
        it.rhs.accept(this);
        it.lhs.accept(this);
        if (!it.rhs.type.isSame(it.lhs.type)){
            throw new semanticError("assignExpr error1", it.pos);
        }
        if (!it.lhs.canLeft) throw new semanticError("assignExpr error2", it.lhs.pos);
        it.type = it.rhs.type;
    }

    @Override public void visit(arrayExprNode it) {
        it.base.accept(this);
        it.width.accept(this);
        if (!it.width.type.isInt()) throw new semanticError("arrayExpr error", it.width.pos);
        if (it.base.type.dim() < 1) throw new semanticError("arrayExpr error", it.base.pos);
        else if (it.base.type.dim() > 1) it.type = new arrayType(it.base.type);
        else it.type = ((arrayType)it.base.type).baseType;
    }

    @Override public void visit(binaryExprNode it) {
        it.lhs.accept(this);
        it.rhs.accept(this);
        Type lhsType = it.lhs.type, rhsType = it.rhs.type;
        int op = it.opCode.ordinal();
        if (op < 9) {
            if(!lhsType.isInt()) throw new semanticError("binaryExpr error", it.pos);
            if(!rhsType.isInt()) throw new semanticError("binaryExpr error", it.pos);
            it.type = gScope.intType;
        } else if (op < 14) {
            if(!((lhsType.isInt() || lhsType.isSame(gScope.getTypeFromName("string", it.pos))) && (lhsType.isSame(rhsType))))
                throw new semanticError("binaryExpr error", it.pos);
            if(op == 9) it.type = lhsType;
            else it.type = gScope.boolType;
        } else if (op < 16) {
            if(!lhsType.isBool()) throw new semanticError("binaryExpr error", it.pos);
            if(!rhsType.isBool()) throw new semanticError("binaryExpr error", it.pos);
            it.type = gScope.boolType;
        } else {
            if (!lhsType.isSame(rhsType)) throw new semanticError("binaryExpr error", it.pos);
            it.type = gScope.boolType;
        }
    }

    @Override public void visit(prefixExprNode it) {
        it.src.accept(this);
        Type srcType = it.src.type;
        int op = it.opCode;
        if(op < 5) {
            if(!srcType.isInt()) throw new semanticError("prefixExpr error", it.pos);
            if(op > 2 && (!it.src.canLeft)) throw new semanticError("prefixExpr error", it.pos);
            it.type = gScope.intType;
        } else {
            if(!srcType.isBool()) throw new semanticError("prefixExpr error", it.pos);
            it.type = gScope.boolType;
        }
    }

    @Override public void visit(suffixExprNode it) {
        it.src.accept(this);
        if(!it.src.type.isInt()) throw new semanticError("suffixExpr error", it.pos);
        if(!it.src.canLeft)  throw new semanticError("suffixExpr error", it.pos);
        it.type = gScope.intType;
    }

    @Override public void visit(thisExprNode it) {
        if(nowClass == null) throw new semanticError("thisExpr error ", it.pos);
        it.theClass = nowClass;
        it.type = nowClass;
    }

    @Override public void visit(funCallExprNode it) {
        it.callee.accept(this);
        if (it.callee.type instanceof funType) {
            funType fun = (funType) it.callee.type;
            ArrayList<substance> arguments = fun.localScope.parameters;
            ArrayList<ExprNode> parameters = it.parameters;
            parameters.forEach(pd -> pd.accept(this));
            if (parameters.size() != arguments.size()) throw new semanticError("funCallExpr error ", it.pos);
            for (int i = 0; i < parameters.size(); i++)
                if(!parameters.get(i).type.isSame(arguments.get(i).type)) throw new semanticError("funCallExpr error ", it.pos);
            it.type = fun.funType;
        } else throw new semanticError("funCallExpr error ", it.callee.pos);
    }

    @Override public void visit(methodExprNode it) {
        it.caller.accept(this);
        if (it.caller.type.isArray()) {
            if(!it.method.equals("size")) throw new semanticError("methodExpr error ", it.pos);
            it.type = gScope.getMethod("size", it.pos, false);
            return;
        }
        if (!it.caller.type.isClass()) throw new semanticError("methodExpr error ", it.pos);
        classType theClass = (classType) it.caller.type;
        if(!theClass.localScope.containsMethod(it.method, false)) throw new semanticError("methodExpr error ", it.pos);
        it.type = theClass.localScope.getMethod(it.method, it.pos, false);
    }

    @Override public void visit(memberExprNode it) {
        it.caller.accept(this);
        if (it.caller.type.isClass()) throw new semanticError("memberExpr error ", it.pos);
        classType theClass = (classType) it.caller.type;
        if(!theClass.localScope.containsMember(it.member, false)) throw new semanticError("funCallExpr error ", it.pos);
        it.memberSubstance = theClass.localScope.getMember(it.member, it.pos,false);
        it.type = it.memberSubstance.type;
    }

    @Override public void visit(newExprNode it) {
        if(!it.exprs.isEmpty())
            it.exprs.forEach(ed -> {
                ed.accept(this);
               if(!ed.type.isInt()) throw new semanticError("newExpr error ", it.pos);
            });
        it.type = gScope.makeType(it.newType);
    }

    @Override public void visit(funNode it){
        it.type = nowScope.getMethod(it.funName, it.pos, true);
    }

    @Override public void visit(varNode it){
        it.varSubstance = nowScope.getMember(it.name, it.pos, true);
        it.type = it.varSubstance.type;
    }

    @Override public void visit(intLiteralNode it) {
        it.type = gScope.intType;
    }

    @Override public void visit(boolLiteralNode it) {
        it.type = gScope.boolType;
    }

    @Override public void visit(nullLiteralNode it){
        it.type = gScope.nullType;
    }

    @Override public void visit(stringLiteralNode it){
        it.type = gScope.getTypeFromName("string", it.pos);
    }

    @Override public void visit(emptyStmtNode it) {}
    @Override public void visit(exprListNode it) {}
    @Override public void visit(typeNode it) {}

}
