package Frontend;

import AST.*;
import Parser.MxBaseVisitor;
import Parser.MxParser;
import Util.position;
import Util.error.internalError;
import Util.error.semanticError;
import Util.error.syntaxError;
import org.antlr.v4.runtime.ParserRuleContext;
import AST.binaryExprNode.opType;

import java.util.ArrayList;

public class ASTBuilder extends MxBaseVisitor<ASTNode> {

    @Override public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        RootNode root = new RootNode(new position(ctx));
        if (!ctx.programSegment().isEmpty()) {
            for (ParserRuleContext segment : ctx.programSegment()) {
                ASTNode tmp = visit(segment);
                if (tmp instanceof varDefListNode) {
                    root.defs.addAll(((varDefListNode) tmp).varDefs);
                } else root.defs.add(tmp);
            }
        }
        return root;
    }

    @Override public ASTNode visitProgramSegment(MxParser.ProgramSegmentContext ctx) {
        if (ctx.classDef() != null) return visit(ctx.classDef());
        else if (ctx.funcDef() != null) return visit(ctx.funcDef());
        else return visit(ctx.varDef());
    }

    @Override public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
        boolean isConstructor = false;
        typeNode type = null;
        blockStmtNode body = (blockStmtNode)visit(ctx.suite());
        ArrayList<varDefNode> parameters = new ArrayList<>();
        if (ctx.type() != null) type = (typeNode)visit(ctx.type());
        else isConstructor = true;
        if (ctx.parameterList() != null) parameters = ((varDefListNode)visit(ctx.parameterList())).varDefs;
        return new funDefNode(ctx.Identifier().toString(), new position(ctx), isConstructor, type, body, parameters);
    }

    @Override public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        ArrayList<varDefNode> vars = new ArrayList<>();
        ArrayList<funDefNode> methods = new ArrayList<>();
        ArrayList<funDefNode> constructors = new ArrayList<>();
        boolean existConstructor = false;
        if(!ctx.varDef().isEmpty())
            ctx.varDef().forEach(vd -> vars.addAll(((varDefListNode) visit(vd)).varDefs));
        if (!ctx.funcDef().isEmpty()) {
            for (ParserRuleContext funcDef : ctx.funcDef()) {
                funDefNode tmp = (funDefNode)visit(funcDef);
                tmp.isClassMethod = true;
                if (tmp.isConstructor) {
                    existConstructor = true;
                    constructors.add(tmp);
                }
                else methods.add(tmp);
            }
        }
        return new classDefNode(new position(ctx), ctx.Identifier().toString(),
                vars, methods, constructors, existConstructor);
    }

    @Override public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
        varDefListNode node = new varDefListNode(new position(ctx));
        typeNode typeName = (typeNode)visit(ctx.type());
        if (!ctx.singleVarDef().isEmpty()) {
            for (ParserRuleContext singleVarDef : ctx.singleVarDef()) {
                varDefNode tmp = (varDefNode) visit(singleVarDef);
                tmp.typeName = typeName;
                node.varDefs.add(tmp);
            }
        }
        return node;
    }

    @Override public ASTNode visitSingleVarDef(MxParser.SingleVarDefContext ctx) {
        ExprNode expr=null;
        if (ctx.expression() != null) expr = (ExprNode)visit(ctx.expression());
        return new varDefNode(ctx.Identifier().toString(), expr, new position(ctx));
    }

    @Override public ASTNode visitParameterList(MxParser.ParameterListContext ctx) {
        varDefListNode parameters = new varDefListNode(new position(ctx));
        for (ParserRuleContext parameter : ctx.parameter()) {
            varDefNode param = (varDefNode) visit(parameter);
            parameters.varDefs.add(param);
        }
        return parameters;
    }

    @Override public ASTNode visitParameter(MxParser.ParameterContext ctx) {
        typeNode typeName = (typeNode) visit(ctx.type());
        varDefNode parameter = new varDefNode(ctx.Identifier().toString(), null, new position(ctx));
        parameter.typeName = typeName;
        return parameter;
    }

    @Override public ASTNode visitBasicType(MxParser.BasicTypeContext ctx) {
        if (ctx.Bool() != null) return new BasicTypeNode("bool",new position(ctx));
        else if(ctx.Int() != null) return new BasicTypeNode("int",new position(ctx));
        else if(ctx.String() != null) return new BasicTypeNode("string",new position(ctx));
        return visitChildren(ctx);
    }

    @Override public ASTNode visitType(MxParser.TypeContext ctx) {
        if (ctx.Void() != null) return new typeNode("void", 0, new position(ctx));
        int dim = ctx.LeftBracket() == null ? 0 : ctx.LeftBracket().size();
        String baseTypeName;
        if (ctx.Identifier() != null) baseTypeName = ctx.Identifier().toString();
        else baseTypeName = ((BasicTypeNode)visit(ctx.basicType())).type;
        return new typeNode(baseTypeName, dim, new position(ctx));
    }

    @Override public ASTNode visitSuite(MxParser.SuiteContext ctx) {
        blockStmtNode node = new blockStmtNode(new position(ctx));
        if (!ctx.statement().isEmpty()) {
            for (ParserRuleContext stmt : ctx.statement()) {
                StmtNode tmp = (StmtNode)visit(stmt);
                if (tmp instanceof emptyStmtNode) continue;
                if (tmp instanceof returnStmtNode || tmp instanceof continueStmtNode || tmp instanceof breakStmtNode) {
                    node.stmts.add(tmp);
                    break;
                }
                if (tmp instanceof varDefListNode) node.stmts.addAll(((varDefListNode) tmp).varDefs);
                else node.stmts.add(tmp);
            }
        }
        return node;
    }

    @Override public ASTNode visitBlock(MxParser.BlockContext ctx) {
        return visit(ctx.suite());
    }

    @Override public ASTNode visitVardefStmt(MxParser.VardefStmtContext ctx) {
        return visit(ctx.varDef());
    }

    @Override public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
        StmtNode thenStmt = (StmtNode)visit(ctx.trueStmt), elseStmt = null;
        ExprNode condition = (ExprNode)visit(ctx.expression());
        if (ctx.falseStmt != null) elseStmt = (StmtNode)visit(ctx.falseStmt);
        return new ifStmtNode(condition, thenStmt, elseStmt, new position(ctx));
    }

    @Override public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
        ExprNode init = null, cond = null, incr = null;
        if (ctx.init != null) init = (ExprNode)visit(ctx.init);
        if (ctx.cond != null) cond = (ExprNode)visit(ctx.cond);
        if (ctx.incr != null) incr = (ExprNode)visit(ctx.incr);
        StmtNode body = (StmtNode)visit(ctx.statement());
        return new forStmtNode(init, incr, cond, body, new position(ctx));
    }

    @Override public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
        ExprNode cond = (ExprNode)visit(ctx.expression());
        StmtNode block = (StmtNode)visit(ctx.statement());
        return new whileStmtNode(cond, block, new position(ctx));
    }

    @Override public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        ExprNode value = null;
        if (ctx.expression() != null) value = (ExprNode) visit(ctx.expression());
        return new returnStmtNode(value, new position(ctx));
    }

    @Override public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        return new breakStmtNode(new position(ctx));
    }

    @Override public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        return new continueStmtNode(new position(ctx));
    }

    @Override public ASTNode visitPureExprStmt(MxParser.PureExprStmtContext ctx) {
        return new exprStmtNode((ExprNode) visit(ctx.expression()), new position(ctx));
    }

    @Override public ASTNode visitEmptyStmt(MxParser.EmptyStmtContext ctx) {
        return new emptyStmtNode(new position(ctx));
    }

    @Override public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        return visit(ctx.creator());
    }

    @Override public ASTNode visitPrefixExpr(MxParser.PrefixExprContext ctx) {
        int opCode;
        if (ctx.Plus() != null) opCode = 0;
        else if (ctx.Minus() != null) opCode = 1;
        else if (ctx.Tilde() != null) opCode = 2;
        else if (ctx.PlusPlus() != null) opCode = 3;
        else if (ctx.MinusMinus() != null) opCode = 4;
        else if (ctx.Not() != null) opCode = 5;
        else throw new internalError("prefixExpr has no correct opCode", new position(ctx));
        return new prefixExprNode((ExprNode)visit(ctx.expression()), opCode, new position(ctx));
    }//+: 0, -: 1, ~:2, ++:3, --:4, !:5

    @Override public ASTNode visitSubscript(MxParser.SubscriptContext ctx) {
        return new arrayExprNode((ExprNode)visit(ctx.expression(0)),
                (ExprNode)visit(ctx.expression(1)), new position(ctx));
    }

    @Override public ASTNode visitMemberExpr(MxParser.MemberExprContext ctx) {
        return new memberExprNode((ExprNode)visit(ctx.expression()),
                ctx.Identifier().toString(), new position(ctx));
    }

    @Override public ASTNode visitSuffixExpr(MxParser.SuffixExprContext ctx) {
        int opCode;
        if (ctx.PlusPlus() != null) opCode = 0;
        else if (ctx.MinusMinus() != null) opCode = 1;
        else throw new internalError("suffixExpr has no correct opCode", new position(ctx));
        return new suffixExprNode((ExprNode)visit(ctx.expression()), opCode, new position(ctx));
    }

    @Override public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
        return visit(ctx.primary());
    }

    @Override public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        ExprNode lhs = (ExprNode) visit(ctx.expression(0)),
                 rhs = (ExprNode) visit(ctx.expression(1));
        opType opCode;
        if (ctx.Star() != null) opCode = opType.Star;
        else if (ctx.Div() != null) opCode = opType.Div;
        else if (ctx.Mod() != null) opCode = opType.Mod;
        else if (ctx.LeftShift() != null) opCode = opType.LeftShift;
        else if (ctx.RightShift() != null) opCode = opType.RightShift;
        else if (ctx.And() != null) opCode = opType.And;
        else if (ctx.Or() != null) opCode = opType.Or;
        else if (ctx.Caret() != null) opCode = opType.Caret;
        else if (ctx.Minus() != null) opCode = opType.Minus;
        else if (ctx.Plus() != null) opCode = opType.Plus;
        else if (ctx.Less() != null) opCode = opType.Less;
        else if (ctx.Greater() != null) opCode = opType.Greater;
        else if (ctx.LessEqual() != null) opCode = opType.LessEqual;
        else if (ctx.GreaterEqual() != null) opCode = opType.GreaterEqual;
        else if (ctx.AndAnd() != null) opCode = opType.AndAnd;
        else if (ctx.OrOr() != null) opCode = opType.OrOr;
        else if (ctx.Equal() != null) opCode = opType.Equal;
        else if (ctx.NotEqual() != null) opCode = opType.NotEqual;
        else throw new internalError("no correct opCode", new position(ctx));
        return new binaryExprNode(lhs, rhs, opCode, new position(ctx));
    }

    @Override public ASTNode visitFuncCall(MxParser.FuncCallContext ctx) {
        ExprNode origin = (ExprNode)visit(ctx.expression());
        exprListNode parameters = ctx.expressionList() == null ? null : (exprListNode)visit(ctx.expressionList());
        ExprNode caller;
        if (origin instanceof varNode)
            caller = new funNode(((varNode)origin).name, origin.pos);
        else if (origin instanceof memberExprNode)
            caller = new methodExprNode(((memberExprNode)origin).caller, ((memberExprNode)origin).member, origin.pos);
        else throw new semanticError("cannot be a function", origin.pos);
        return new funCallExprNode(caller, parameters, new position(ctx));
    }

    @Override public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
        ExprNode lhs = (ExprNode) visit(ctx.expression(0)),
                 rhs = (ExprNode) visit(ctx.expression(1));
        return new assignExprNode(lhs, rhs, rhs.canLeft, new position(ctx));
    }

    @Override public ASTNode visitExpressionList(MxParser.ExpressionListContext ctx) {
        exprListNode node = new exprListNode(new position(ctx));
        if (!ctx.expression().isEmpty())
            ctx.expression().forEach(ed -> node.exprs.add((ExprNode) visit(ed)));
        return node;
    }

    @Override public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
        if (ctx.expression() != null) return visit(ctx.expression());
        else if (ctx.This() != null) return new thisExprNode(new position(ctx));
        else if (ctx.Identifier() != null) return new varNode(ctx.Identifier().toString(), new position(ctx.Identifier()));
        else if (ctx.literal() != null) return visit(ctx.literal());
        else throw new syntaxError("not a primary", new position(ctx));
    }

    @Override public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        if (ctx.DecimalInteger() != null) return new intLiteralNode(Integer.parseInt(ctx.DecimalInteger().toString()), new position(ctx));
        else if (ctx.StringLiteral() != null) return new stringLiteralNode(ctx.StringLiteral().toString(), new position(ctx));
        else if (ctx.True() != null) return new boolLiteralNode(true, new position(ctx));
        else if (ctx.False() != null) return new boolLiteralNode(false, new position(ctx));
        else if (ctx.Null() != null) return new nullLiteralNode(new position(ctx));
        else throw new syntaxError("not a literal", new position(ctx));
    }

    @Override public ASTNode visitErrorCreator(MxParser.ErrorCreatorContext ctx) {
        throw new semanticError("error creator", new position(ctx));
    }

    @Override public ASTNode visitArrayCreator(MxParser.ArrayCreatorContext ctx) {
        String baseTypeName;
        ArrayList<ExprNode> exprs = new ArrayList<>();
        position typePos;
        if (ctx.basicType() != null) {
            typePos = new position(ctx.basicType());
            baseTypeName = ((BasicTypeNode) visit(ctx.basicType())).type;
        } else {
            typePos = new position(ctx.Identifier());
            baseTypeName = ctx.Identifier().toString();
        }
        if (!ctx.expression().isEmpty())
            ctx.expression().forEach(ed -> exprs.add((ExprNode) visit(ed)));
        return new newExprNode(new typeNode(baseTypeName, ctx.LeftBracket().size(), typePos), exprs, new position(ctx));
    }

    @Override public ASTNode visitClassCreator(MxParser.ClassCreatorContext ctx) {
        String baseTypeName;
        ArrayList<ExprNode> exprs = new ArrayList<>();
        position typePos;
        if (ctx.basicType() != null) {
            typePos = new position(ctx.basicType());
            baseTypeName = ((BasicTypeNode) visit(ctx.basicType())).type;
        } else {
            typePos = new position(ctx.Identifier());
            baseTypeName = ctx.Identifier().toString();
        }
        return new newExprNode(new typeNode(baseTypeName, 0, typePos), exprs, new position(ctx));
    }

    @Override public ASTNode visitBasicCreator(MxParser.BasicCreatorContext ctx) {
        String baseTypeName;
        ArrayList<ExprNode> exprs = new ArrayList<>();
        position typePos;
        if (ctx.basicType() != null) {
            typePos = new position(ctx.basicType());
            baseTypeName = ((BasicTypeNode) visit(ctx.basicType())).type;
        } else {
            typePos = new position(ctx.Identifier());
            baseTypeName = ctx.Identifier().toString();
        }
        return new newExprNode(new typeNode(baseTypeName, 0, typePos), exprs, new position(ctx));
    }

}
