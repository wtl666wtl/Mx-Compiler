package Backend;

import AST.*;
import AST.binaryExprNode.opType;
import MIR.IRinstruction.Binary.binaryOpType;
import MIR.IRinstruction.Icmp.IcmpOpType;
import MIR.IRinstruction.*;
import MIR.IRtype.*;
import MIR.IRoperand.*;
import MIR.*;
import Util.scope.*;
import Util.type.*;
import Util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class IRBuilder implements ASTVisitor {

    public globalScope gScope;
    public rootNode rt;
    public Block curblk = null;
    public ArrayList<Ret> returnList = new ArrayList<>();
    public HashMap<Block, phiInfo>phiMap =new HashMap<>();
    public Function curFunc = null;
    public classType curClass = null;
    public boolean isParameter = false;
    public int cnt = 0;
    public static ConstInt number_1 = new ConstInt(1, 32);
    public static ConstInt number_0 = new ConstInt(1, 32);
    public static ConstInt number_neg1 = new ConstInt(-1, 32);
    public static ConstBool bool_true = new ConstBool(true);
    public static ConstBool bool_false = new ConstBool(false);

    public void init_builtInFuncs(String funcName){
        gScope.getMethod(funcName, new position(0, 0), false).IRFunc =
                rt.builtInFuncs.get("builtIn_" + funcName);
    }

    public void init_builtInFuncs(Scope nowScope, String funcName){
        nowScope.getMethod(funcName, new position(0, 0), false).IRFunc =
                rt.builtInFuncs.get("builtIn_string_" + funcName);
    }

    public IRBuilder(globalScope gScope, rootNode rt){
        this.gScope = gScope;
        this.rt = rt;
        init_builtInFuncs("print");
        init_builtInFuncs("println");
        init_builtInFuncs("printInt");
        init_builtInFuncs("printlnInt");
        init_builtInFuncs("getString");
        init_builtInFuncs("getInt");
        init_builtInFuncs("toString");

        classType stringType = (classType) gScope.getTypeFromName("string", new position(0, 0));
        Scope stringScope = stringType.localScope;

        init_builtInFuncs(stringScope, "length");
        init_builtInFuncs(stringScope, "substring");
        init_builtInFuncs(stringScope, "parseInt");
        init_builtInFuncs(stringScope, "ord");
    }

    public String getName(BaseOperand it){
        if(it instanceof Register)return ((Register) it).name;
        else if(it instanceof Parameter)return ((Parameter) it).name;
        else if(it instanceof GlobalVar)return ((GlobalVar) it).name;
        else return "constant";
    }

    public BaseOperand INT8_to_BOOL(BaseOperand it){
        Register zextrd = new Register("zext_INT8_to_BOOL_" + it.toString(), rootNode.BOOL);
        curblk.addInst(new Zext(zextrd, curblk, it));
        return zextrd;
    }

    public BaseOperand BOOL_TO_INT(BaseOperand it){
        Register zextrd = new Register("zext_BOOL_to_INT_" + it.toString(), rootNode.INT);
        curblk.addInst(new Zext(zextrd, curblk, it));
        return zextrd;
    }

    public BaseOperand getPointer(BaseOperand it, boolean solveBOOL){
        if(it.type.MMflag){
            String pointerName = getName(it);
            Register rd = new Register("getFromMM_" + pointerName, ((IRPointerType)it.type).pointTo);
            curblk.addInst(new Load(rd, curblk, it));
            if(solveBOOL && rd.type instanceof IRIntType && rd.type.width == 8)return INT8_to_BOOL(rd);
            else return rd;
        } else return it;
    }

    /*public BaseOperand getStringPointer(BaseOperand it) {
        assert it.type instanceof IRPointerType;
        if(((IRPointerType)it.type).pointTo instanceof IRPointerType){
            return getPointer(it, false);
        }else return it;
    }*/

    public void solveAssign(BaseOperand addr, ExprNode expr){
        expr.accept(this);
        BaseOperand val = getPointer(expr.operand, false);
        curblk.addInst(new Store(curblk, addr, val));
    }

    public void manageBrPhi(ExprNode it){
        if(it.operand == null)return;
        if(it.trueblk != null){
            BaseOperand tmp = getPointer(it.operand, true);
            curblk.addTerminator(new Br(curblk, tmp, it.trueblk, it.falseblk));
            if(phiMap.containsKey(it.trueblk)){
                phiInfo phi = phiMap.get(it.trueblk);
                phi.vals.add(new ConstBool(true));
                phi.blks.add(curblk);
            }
            if(phiMap.containsKey(it.falseblk)){
                phiInfo phi = phiMap.get(it.falseblk);
                phi.vals.add(new ConstBool(false));
                phi.blks.add(curblk);
            }
        }
    }

    public String getRealString(String it){
        String realString = it.replace("\\n", "\n");
        realString = realString.replace("\\t", "\t");
        realString = realString.replace("\\\"", "\"");
        realString = realString.replace("\\\\", "\\");
        realString += "\0";
        return realString;
    }

    public void newArray(int dim, newExprNode it, Register res){
        if(dim == it.exprs.size())return;
        Register newPtr;
        if(dim == 0)newPtr = res;
        else newPtr = new Register("newArrayPtr_Reg", ((IRPointerType)res.type).pointTo);
        IRBaseType ptrPointTo = ((IRPointerType)newPtr.type).pointTo;

        it.exprs.get(dim).accept(this);
        BaseOperand dimLength = getPointer(it.exprs.get(dim).operand, false);
        BaseOperand typeWidth = new ConstInt(ptrPointTo.width / 8, 32);

        Register width = new Register("width_Reg", rootNode.INT);
        Register metaWidth = new Register("metaWidth_Reg", rootNode.INT);
        curblk.addInst(new Binary(width, curblk, binaryOpType.mul, dimLength, typeWidth));
        curblk.addInst(new Binary(metaWidth, curblk, binaryOpType.add, width, new ConstInt(4, 32)));

        Register allocPtr = new Register("allocPtr_Reg", rootNode.STRING);
        Register allocBitCast = new Register("allocBitCast_Reg", new IRPointerType(rootNode.INT, false));
        curblk.addInst(new Malloc(allocPtr, curblk, metaWidth));
        curblk.addInst(new BitCast(allocBitCast, curblk, allocPtr));
        curblk.addInst(new Store(curblk, allocBitCast, dimLength));

        if(ptrPointTo instanceof IRIntType && ptrPointTo.width == 32){
            curblk.addInst(new GetElementPtr(newPtr, curblk, rootNode.INT, allocBitCast, number_1, null));
        }else{
            Register tmp = new Register("allocTmp_Reg", new IRPointerType(rootNode.INT, false));
            curblk.addInst(new GetElementPtr(tmp, curblk, rootNode.INT, allocBitCast, number_1, null));
            curblk.addInst(new BitCast(newPtr, curblk, tmp));
        }
        if(dim != 0)curblk.addInst(new Store(curblk, res, newPtr));

        if(dim < it.exprs.size() - 1){
            Block incrBlk = new Block("IncrBlk_for_array");
            Block bodyBlk = new Block("BodyBlk_for_array");
            Block terminalBlk = new Block("TerminalBlk_for_array");
            Register ptr = new Register("pointer_Reg", new IRPointerType(rootNode.INT, false));
            Register BCPtr = new Register("BCPointer_Reg", newPtr.type);
            Register counter = new Register("counter_Reg", rootNode.INT);
            Register counterAdd1 = new Register("counterAdd1_Reg", rootNode.INT);
            Register isOutOfBound = new Register("isOutOfBound_Reg", rootNode.BOOL);

            phiInfo phi = new phiInfo();
            curblk.addTerminator(new Br(curblk, null, incrBlk, null));

            curblk = incrBlk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);
            curblk.addInst(new Binary(counterAdd1, curblk, binaryOpType.add, counter, number_1));
            curblk.addInst(new Icmp(isOutOfBound, curblk, IcmpOpType.sle, counter, dimLength));
            curblk.addTerminator(new Br(curblk, isOutOfBound, bodyBlk, terminalBlk));

            curblk = bodyBlk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);
            curblk.addInst(new GetElementPtr(ptr, curblk, rootNode.INT, allocBitCast, counter, null));
            curblk.addInst(new BitCast(BCPtr, curblk, ptr));

            newArray(dim + 1, it, BCPtr);
            curblk.addTerminator(new Br(curblk, null, incrBlk, null));
            phi.vals.add(counterAdd1);
            phi.blks.add(curblk);

            curblk = incrBlk;
            curblk.addPhi(new Phi(counter, curblk, phi));

            curblk = terminalBlk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);
        }
    }

    @Override public void visit(RootNode it){
        it.defs.forEach(def -> {
            if(def instanceof classDefNode){
                String className = ((classDefNode)def).name;
                ((classDefNode)def).methods.forEach(method -> {
                    String funcName = "ClassMethod_" + className + "_" + method.name;
                    Function func = new Function(funcName);
                    method.decl.IRFunc = func;
                    rt.funcs.put(func.name, func);
                });
                //Maybe need something
                ((classDefNode)def).constructors.forEach(constructor -> {
                    String funcName = "ClassConstructor_" + className;// + "_" + constructor.name;
                    Function func = new Function(funcName);
                    constructor.decl.IRFunc = func;
                    rt.funcs.put(func.name, func);
                });
            }else if(def instanceof funDefNode){
                String funcName = ((funDefNode)def).name;
                if(!funcName.equals("main")) funcName = "globalFunction_" + funcName;
                Function func = new Function(funcName);
                ((funDefNode)def).decl.IRFunc = func;
                rt.funcs.put(func.name, func);
            }
        });
        it.defs.forEach(def -> def.accept(this));
        rt.funcs.get("__init").outblk.addTerminator(new Ret(rt.funcs.get("__init").outblk, null));

        //maybe ok?
    }

    @Override public void visit(classDefNode it){
        curClass = (classType)gScope.getTypeFromName(it.name, it.pos);
        it.members.forEach(mem -> mem.accept(this));
        it.methods.forEach(met -> met.accept(this));
        it.constructors.forEach(con -> con.accept(this));
    }

    @Override public void visit(funDefNode it){
        returnList.clear();
        funType func = it.decl;
        curFunc = func.IRFunc;
        curblk = curFunc.inblk;
        curFunc.funcBlocks.add(curblk);

        if(it.isClassMethod)
            curFunc.setClassPtr(new Parameter(rt.createIRType(curClass, false), "this"));
        curFunc.funType.retType = rt.createIRType(func.funType, false);

        isParameter = true;
        it.parameters.forEach(p -> p.accept(this));
        isParameter = false;

        if(curFunc.name.equals("main"))
            curblk.addInst(new Call(null, curblk, rt.funcs.get("__init"), new ArrayList<>()));

        it.body.accept(this);

        if(!curblk.isTerminated){
            IRBaseType retType = curFunc.funType.retType;
            if(curFunc.name.equals("main")){
                curblk.addTerminator(new Ret(curblk, number_0));
            }else if(retType instanceof IRIntType){
                curblk.addTerminator(new Ret(curblk, new ConstInt(0, retType.width)));
            }else if(retType instanceof IRBoolType){
                curblk.addTerminator(new Ret(curblk, new ConstBool(false)));
            }else if(retType instanceof IRVoidType){
                curblk.addTerminator(new Ret(curblk, null));
            }else if(curFunc.funType.retType instanceof IRPointerType){
                curblk.addTerminator(new Ret(curblk, new ConstNull()));
            }
            returnList.add((Ret)curblk.getTerminator());
        }
        //maybe need something! DFS?
        //I add every blocks when create them

        if(returnList.size() > 1){
            Block rtRet = new Block("rtRet_blk");
            BaseOperand retVal0 = returnList.get(0).retVal;
            Register retVal = retVal0 == null ? null : new Register("rtRet_Reg", retVal0.type);
            if(retVal != null){
               phiInfo phi = new phiInfo();
               returnList.forEach(ret -> {
                    ret.blk.deleteTerminator();
                    phi.vals.add(ret.retVal);
                    phi.blks.add(ret.blk);
                    ret.blk.addTerminator(new Br(ret.blk, null, rtRet, null));
               });
               rtRet.addPhi(new Phi(retVal, rtRet, phi));
            }else {
                returnList.forEach(ret -> {
                    ret.blk.deleteTerminator();
                    ret.blk.addTerminator(new Br(ret.blk, null, rtRet, null));
                });
            }
            rtRet.addTerminator(new Ret(rtRet, retVal));
            curFunc.outblk = rtRet;
            curFunc.funcBlocks.add(rtRet);
        }else curFunc.outblk = returnList.get(0).blk;

        curFunc.varPtrs.forEach(varPtr -> {
            if( ((IRPointerType)varPtr.type).pointTo instanceof IRPointerType )
                curFunc.inblk.addInstAtStart( new Store(curFunc.inblk, varPtr, new ConstNull()) );
            else curFunc.inblk.addInstAtStart( new Store(curFunc.inblk, varPtr,
                    new ConstInt(65536, ((IRPointerType)varPtr.type).pointTo.width)) );//???
        });

        returnList.clear();
        curFunc = null;
        curblk = null;
    }

    @Override public void visit(varDefNode it){
        substance var = it.varSubstance;
        IRBaseType type = rt.createIRType(var.type, true);
        if(var.isGlobalVar){
            var.operand = new GlobalVar("GlobalVar_" + var.name + "_addr",
                    new IRPointerType(type, true));
            rt.globalVars.add((GlobalVar) var.operand);
            if(it.init != null){
                curblk = rt.funcs.get("__init").outblk;
                curFunc = rt.funcs.get("__init");
                solveAssign(var.operand, it.init);
                curFunc.outblk = curblk;
                curblk = null;
                curFunc = null;
            }
        }else{
            if(isParameter){
                Parameter p = new Parameter(type, "Parameter_" + var.name);
                curFunc.funType.paramList.add(p);
                var.operand = new Register("Parameter_" + var.name + "_addr",
                        new IRPointerType(type, true));
                curFunc.varPtrs.add((Register) var.operand);
                curblk.addInst(new Store(curblk, var.operand, p));
            }else {
                if(curFunc == null){
                    if(type instanceof IRClassType)
                        type = new IRPointerType(type, false);
                    var.operand = new Register("ClassMember_" + it.name + "_addr",
                            new IRPointerType(type, true));
                }else{
                    var.operand = new Register("TemporaryVar_" + it.name + "_addr",
                            new IRPointerType(type, true));
                    if(it.init != null)solveAssign(var.operand, it.init);
                    curFunc.varPtrs.add((Register)var.operand);
                }
            }
        }
    }

    @Override public void visit(varDefListNode it){}

    @Override public void visit(blockStmtNode it){
        for(StmtNode sd : it.stmts) {
            sd.accept(this);
            if(curblk.isTerminated)break;
        }
    }

    @Override public void visit(exprStmtNode it){
        it.expr.accept(this);
    }

    @Override public void visit(exprListNode it){}

    @Override public void visit(ifStmtNode it) {
        Block trueblk = new Block("if_true");
        Block falseblk = new Block("if_false");
        Block terminalblk = new Block("if_terminal");

        if (it.elseStmt == null) falseblk = terminalblk;
        it.condition.trueblk = trueblk;
        it.condition.falseblk = falseblk;

        it.condition.accept(this);

        curblk = trueblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);

        it.thenStmt.accept(this);
        if(!curblk.isTerminated)curblk.addTerminator(new Br(curblk, null, terminalblk, null));

        if (it.elseStmt != null) {
            curblk = falseblk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);

            it.elseStmt.accept(this);
            if(!curblk.isTerminated)curblk.addTerminator(new Br(curblk, null, terminalblk, null));
        }
        if (trueblk.returnTerminated() && falseblk.returnTerminated()) return;

        curblk = terminalblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);
    }

    @Override public void visit(forStmtNode it){
        Block bodyblk = new Block("for_body");
        Block condblk = new Block("for_cond");
        Block incrblk = new Block("for_incr");
        Block terminalblk = new Block("for_terminal");

        it.incrblk = incrblk;
        it.terminalblk = terminalblk;
        if(it.init != null)it.init.accept(this);

        if(it.condition != null) {
            curblk.addTerminator(new Br(curblk, null, condblk, null));
            curblk = condblk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);

            it.condition.trueblk = bodyblk;
            it.condition.falseblk = terminalblk;
            it.condition.accept(this);
        } else {
            curblk.addTerminator(new Br(curblk, null, bodyblk, null));
            condblk = bodyblk;
        }

        curblk = bodyblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);
        System.out.println("` " + curFunc.name);
        System.out.println("` " + curblk.name);
        it.body.accept(this);
        System.out.println(curblk.isTerminated);
        if(!curblk.isTerminated) curblk.addTerminator(new Br(curblk, null, incrblk, null));
        else {
            curblk = terminalblk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);return;
        }
        curblk = incrblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);
        System.out.println("~" + curFunc.name);
        System.out.println("~" + curblk.name);

        if(it.incr != null)it.incr.accept(this);
        System.out.println("~" + curFunc.name);
        System.out.println("~" + curblk.name);
        if(!curblk.isTerminated) curblk.addTerminator(new Br(curblk, null, condblk, null));

        curblk = terminalblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);
    }

    @Override public void visit(whileStmtNode it) {
        Block bodyblk = new Block("for_body");
        Block condblk = new Block("for_cond");
        Block terminalblk = new Block("for_terminal");

        it.terminalblk = terminalblk;

        if(it.condition != null) {
            curblk.addTerminator(new Br(curblk, null, condblk, null));
            curblk = condblk;
            if(curFunc != null)curFunc.funcBlocks.add(curblk);

            it.condblk = condblk;

            it.condition.trueblk = bodyblk;
            it.condition.falseblk = terminalblk;
            it.condition.accept(this);
        } else {
            curblk.addTerminator(new Br(curblk, null, bodyblk, null));
            condblk = bodyblk;
            it.condblk = bodyblk;
        }

        curblk = bodyblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);

        it.body.accept(this);
        if(!curblk.isTerminated) curblk.addTerminator(new Br(curblk, null, condblk, null));

        curblk = terminalblk;
        if(curFunc != null)curFunc.funcBlocks.add(curblk);
    }

    @Override public void visit(continueStmtNode it){
        if(it.dest instanceof forStmtNode) {
            curblk.addTerminator(new Br(curblk, null, ((forStmtNode)it.dest).incrblk, null));
        } else {
            curblk.addTerminator(new Br(curblk, null, ((whileStmtNode)it.dest).condblk, null));
        }
    }

    @Override public void visit(breakStmtNode it){
        if(it.dest instanceof forStmtNode) {
            curblk.addTerminator(new Br(curblk, null, ((forStmtNode)it.dest).terminalblk, null));
        } else {
            curblk.addTerminator(new Br(curblk, null, ((whileStmtNode)it.dest).terminalblk, null));
        }
    }

    @Override public void visit(returnStmtNode it){
        Ret retInst;
        if(it.value == null) {
            retInst = new Ret(curblk, null);
        } else {
            it.value.accept(this);
            BaseOperand retOperand;
            if(it.value.operand.type.dim > curFunc.funType.retType.dim) {//Pointer
                retOperand = getPointer(it.value.operand, true);
            } else {
                retOperand = it.value.operand;
            }
            retInst = new Ret(curblk, retOperand);
        }
        curblk.addTerminator(retInst);
        returnList.add(retInst);
    }

    @Override public void visit(emptyStmtNode it){}

    @Override public void visit(typeNode it){}

    @Override public void visit(arrayExprNode it){
        it.base.accept(this);
        it.width.accept(this);

        BaseOperand basePointer = getPointer(it.base.operand, false);
        BaseOperand width = getPointer(it.width.operand, false);

        it.operand = new Register("arrayBasePointer",
                new IRPointerType(rt.createIRType(it.type, true), true));
        curblk.addInst(new GetElementPtr((Register)it.operand, curblk,
                ((IRPointerType)basePointer.type).pointTo,basePointer, width, null));

        manageBrPhi(it);
    }

    @Override public void visit(assignExprNode it){
        it.lhs.accept(this);
        solveAssign(it.lhs.operand, it.rhs);
        it.operand = it.rhs.operand;
    }

    @Override public void visit(binaryExprNode it){

        binaryOpType bop = null;
        IcmpOpType cop = null;
        Function stringFunc = null;

        if(it.opCode == opType.Star){
            bop = binaryOpType.mul;
        } else if(it.opCode == opType.Div) {
            bop = binaryOpType.sdiv;
        } else if(it.opCode == opType.Mod) {
            bop = binaryOpType.srem;
        } else if(it.opCode == opType.And) {
            bop = binaryOpType.and;
        } else if(it.opCode == opType.Or) {
            bop = binaryOpType.or;
        } else if(it.opCode == opType.Caret) {
            bop = binaryOpType.xor;
        } else if(it.opCode == opType.LeftShift) {
            bop = binaryOpType.shl;
        } else if(it.opCode == opType.RightShift) {
            bop = binaryOpType.ashr;
        } else if(it.opCode == opType.Minus) {
            bop = binaryOpType.sub;
        } else if(it.opCode == opType.Plus) {
            if(it.lhs.type.isInt()) bop = binaryOpType.add;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_add");
        } else if(it.opCode == opType.Less) {
            if(it.lhs.type.isInt()) cop = IcmpOpType.slt;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_Less");
        } else if(it.opCode == opType.Greater) {
            if(it.lhs.type.isInt()) cop = IcmpOpType.sgt;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_Greater");
        } else if(it.opCode == opType.LessEqual) {
            if(it.lhs.type.isInt()) cop = IcmpOpType.sle;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_LessEqual");
        } else if(it.opCode == opType.GreaterEqual) {
            if(it.lhs.type.isInt()) cop = IcmpOpType.sge;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_GreaterEqual");
        } else if(it.opCode == opType.Equal) {
            if(it.lhs.type.isInt()) cop = IcmpOpType.eq;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_Equal");
        } else if(it.opCode == opType.NotEqual){
            if(it.lhs.type.isInt()) cop = IcmpOpType.ne;
            else stringFunc = rt.builtInFuncs.get("builtIn_string_NotEqual");
        }

        BaseOperand lhs, rhs;

        if(it.opCode.ordinal() <= 9){
            it.lhs.accept(this);
            it.rhs.accept(this);
            if(bop != null){
                lhs = getPointer(it.lhs.operand, false);
                rhs = getPointer(it.rhs.operand, false);

                it.operand = new Register("binary_" + bop.toString() + "_Reg", rootNode.INT);
                curblk.addInst(new Binary((Register)it.operand, curblk, bop, lhs, rhs));
            } else {
                it.operand = new Register("string_binary_" + it.opCode.toString() + "_Reg", rootNode.STRING);//only plus
                ArrayList<BaseOperand> params = new ArrayList<>();

                lhs = getPointer(it.lhs.operand, false);
                rhs = getPointer(it.rhs.operand, false);

                params.add(lhs);
                params.add(rhs);
                curblk.addInst(new Call((Register)it.operand, curblk, stringFunc, params));
            }
        } else if(it.opCode.ordinal() <= 13 || it.opCode.ordinal() >= 16) {
            it.lhs.accept(this);
            it.rhs.accept(this);
            if(cop != null){
                lhs = getPointer(it.lhs.operand, false);
                rhs = getPointer(it.rhs.operand, false);

                it.operand = new Register("cmp_" + cop.toString() + "_Reg", rootNode.BOOL);
                curblk.addInst(new Icmp((Register)it.operand, curblk, cop, lhs, rhs));
            } else {
                it.operand = new Register("string_cmp_" + it.opCode.toString() + "_Reg", rootNode.BOOL);
                ArrayList<BaseOperand> params = new ArrayList<>();

                lhs = getPointer(it.lhs.operand, false);
                rhs = getPointer(it.rhs.operand, false);

                params.add(lhs);
                params.add(rhs);
                curblk.addInst(new Call((Register)it.operand, curblk, stringFunc, params));
            }
            manageBrPhi(it);
        } else if (it.opCode.ordinal() == 14){//AndAnd
            if(it.trueblk != null){
                Block tmpblk = new Block("AndAnd_tmpblk");

                it.lhs.trueblk = tmpblk;
                it.lhs.falseblk = it.falseblk;
                it.lhs.accept(this);

                curblk = tmpblk;
                if(curFunc != null)curFunc.funcBlocks.add(curblk);

                it.rhs.trueblk = it.trueblk;
                it.rhs.falseblk = it.falseblk;
                it.rhs.accept(this);
            } else {
                Block condblk = new Block("AndAnd_condblk");
                Block destblk = new Block("AndAnd_destblk");

                phiInfo AndAnd_phi = new phiInfo();
                phiMap.put(destblk, AndAnd_phi);

                it.operand = new Register("AndAnd_Reg", rootNode.BOOL);

                it.lhs.trueblk = condblk;
                it.lhs.falseblk = destblk;
                it.lhs.accept(this);

                curblk = condblk;
                if(curFunc != null)curFunc.funcBlocks.add(curblk);

                it.rhs.accept(this);

                BaseOperand opr = getPointer(it.rhs.operand, true);
                curblk.addTerminator(new Br(curblk, null, destblk, null));
                AndAnd_phi.vals.add(opr);
                AndAnd_phi.blks.add(curblk);

                curblk = destblk;
                if(curFunc != null)curFunc.funcBlocks.add(curblk);
                destblk.addPhi(new Phi((Register)it.operand, curblk, AndAnd_phi));
            }
        } else{//OrOr
            if(it.trueblk != null){
                Block tmpblk = new Block("OrOr_tmpblk");

                it.lhs.trueblk = it.trueblk;
                it.lhs.falseblk = tmpblk;
                it.lhs.accept(this);

                curblk = tmpblk;
                if(curFunc != null)curFunc.funcBlocks.add(curblk);

                it.rhs.trueblk = it.trueblk;
                it.rhs.falseblk = it.falseblk;
                it.rhs.accept(this);
            } else {
                Block condblk = new Block("OrOr_condblk");
                Block destblk = new Block("OrOr_destblk");

                phiInfo OrOr_phi = new phiInfo();
                phiMap.put(destblk, OrOr_phi);

                it.operand = new Register("OrOr_Reg", rootNode.BOOL);

                it.lhs.trueblk = destblk;
                it.lhs.falseblk = condblk;
                it.lhs.accept(this);
                OrOr_phi.vals.add(new ConstBool(true));
                OrOr_phi.blks.add(curblk);

                curblk = condblk;
                if(curFunc != null)curFunc.funcBlocks.add(curblk);

                it.rhs.accept(this);

                BaseOperand opr = getPointer(it.rhs.operand, true);
                curblk.addTerminator(new Br(curblk, null, destblk, null));
                OrOr_phi.vals.add(opr);
                OrOr_phi.blks.add(curblk);

                curblk = destblk;
                if(curFunc != null)curFunc.funcBlocks.add(curblk);

                destblk.addPhi(new Phi((Register)it.operand, curblk, OrOr_phi));
            }
        }
    }

    @Override public void visit(prefixExprNode it){
        IRBaseType type = it.opCode < 5 ? rootNode.INT : rootNode.BOOL;
        it.operand = new Register("prefix_" + it.opCode + "_Reg", type);
        if(it.opCode == 5 && it.trueblk != null){
            it.src.trueblk = it.falseblk;
            it.src.falseblk = it.trueblk;
            it.src.accept(this);
            return;
        }
        it.src.accept(this);
        BaseOperand src = getPointer(it.src.operand, true);
        if(it.opCode == 0){
            it.operand = it.src.operand;
        }else if(it.opCode == 1){
            curblk.addInst(new Binary((Register)it.operand, curblk, binaryOpType.sub, number_0, src));
        }else if(it.opCode == 2){
            curblk.addInst(new Binary((Register)it.operand, curblk, binaryOpType.xor, src, number_neg1));
        }else if(it.opCode == 3){
            curblk.addInst(new Binary((Register)it.operand, curblk, binaryOpType.add, src, number_1));
            curblk.addInst(new Store(curblk, it.src.operand, it.operand));
        }else if(it.opCode == 4){
            curblk.addInst(new Binary((Register)it.operand, curblk, binaryOpType.sub, src, number_1));
            curblk.addInst(new Store(curblk, it.src.operand, it.operand));
        }else curblk.addInst(new Binary((Register)it.operand, curblk, binaryOpType.xor, src, bool_true));
        manageBrPhi(it);
    }

    @Override public void visit(suffixExprNode it){
        it.src.accept(this);

        Register tmp = new Register("suffix_tmp_Reg", rootNode.INT);
        it.operand = getPointer(it.src.operand, false);
        //System.out.println("!" + it.operand);

        if(it.opCode == 0)curblk.addInst(new Binary(tmp, curblk, binaryOpType.add, it.operand, number_1));
        else curblk.addInst(new Binary(tmp, curblk, binaryOpType.sub, it.operand, number_1));
        //System.out.println(it.operand);
        curblk.addInst(new Store(curblk, it.src.operand, tmp));
    }

    @Override public void visit(thisExprNode it){
        it.operand = curFunc.classPtr;
    }

    @Override public void visit(funCallExprNode it){
        it.callee.accept(this);
        funType func = (funType)it.callee.type;
        if(func.funName.equals("size") && !func.isMethod){
            it.operand = new Register("funCall_size", rootNode.INT);
            BaseOperand calleePtr = getPointer(it.callee.operand, false);
            Register addrPtr = new Register("addrPtr", new IRPointerType(rootNode.INT, false));
            BaseOperand bitCastPtr = new Register("bitCastPtr", new IRPointerType(rootNode.INT, false));
            IRBaseType type = ((IRPointerType)calleePtr.type).pointTo;
            if(type == rootNode.INT && type.width == 32)bitCastPtr = calleePtr;
            else curblk.addInst(new BitCast((Register)bitCastPtr, curblk, calleePtr));
            curblk.addInst(new GetElementPtr(addrPtr, curblk, rootNode.INT,
                    bitCastPtr, number_neg1, null));
            curblk.addInst(new Load((Register)it.operand, curblk, addrPtr));
        } else {
            if(it.type.isVoid())it.operand = null;
            else it.operand = new Register("funCall_" + func.funName,
                    rt.createIRType(func.funType, false));

            ArrayList<BaseOperand> params = new ArrayList<>();

            if(func.isMethod)
                params.add(getPointer(it.callee.operand, false));//get this
            it.parameters.forEach(pd ->{
                pd.accept(this);
                params.add(getPointer(pd.operand, true));
            });

            curblk.addInst(new Call((Register)it.operand, curblk, func.IRFunc, params));
            if(!rt.builtInFuncs.containsKey(func.IRFunc.name))curFunc.callFuncs.add(func.IRFunc);
        }

        manageBrPhi(it);
    }

    @Override public void visit(methodExprNode it){
        it.caller.accept(this);
        it.operand = it.caller.operand;
    }

    @Override public void visit(memberExprNode it){
        it.caller.accept(this);
        BaseOperand classPtr = getPointer(it.caller.operand, false);
        it.operand = new Register("member_" + it.member + "_Reg", it.memberSubstance.operand.type);
        curblk.addInst(new GetElementPtr((Register)it.operand, curblk,
                ((IRPointerType)classPtr.type).pointTo, classPtr,
                new ConstInt(0, 32), it.memberSubstance.index));
        manageBrPhi(it);
    }

    @Override public void visit(newExprNode it){
        if(it.type instanceof arrayType){
            it.operand = new Register("newArrayPtr_Reg", rt.createIRType(it.type, true));
            newArray(0, it, (Register)it.operand);
        }else{
            it.operand = new Register("newClassPtr_Reg", rt.createIRType(it.type, false));
            Register tmp = new Register("mallocTmp_Reg", rootNode.STRING);
            curblk.addInst(new Malloc(tmp, curblk,
                    new ConstInt(((classType)it.type).memberAllocSize / 8, 32)));
            curblk.addInst(new BitCast((Register)it.operand, curblk, tmp));
            if(((classType)it.type).localScope.constructor != null){
                ArrayList<BaseOperand> params = new ArrayList<>();
                params.add(it.operand);
                curblk.addInst(new Call(null, curblk, ((classType)it.type).localScope.constructor.IRFunc, params));
            }
        }
    }

    @Override public void visit(funNode it){
        funType fun = (funType)it.type;
        if(fun.isMethod)it.operand = curFunc.classPtr;
    }

    @Override public void visit(varNode it){
        substance var = it.varSubstance;
        if(var.inClass){
            BaseOperand classPtr = curFunc.classPtr;
            it.operand = new Register("this." + it.name + "_Reg", var.operand.type);
            curblk.addInst(new GetElementPtr((Register)it.operand, curblk,
                    ((IRPointerType)classPtr.type).pointTo, classPtr,
                    new ConstInt(0, 32), var.index));
        }else it.operand = var.operand;
        //System.out.println(it.name);
        //System.out.println(it.operand.type);
        manageBrPhi(it);
    }

    @Override public void visit(intLiteralNode it){
        it.operand = new ConstInt(it.value, 32);
    }

    @Override public void visit(nullLiteralNode it){
        it.operand = new ConstNull();
    }

    @Override public void visit(boolLiteralNode it){
        it.operand = new ConstBool(it.value);
        manageBrPhi(it);
    }

    @Override public void visit(stringLiteralNode it){
        String realString = getRealString(it.value.substring(1, it.value.length() - 1));
        ConstString string = null;
        String name;
        if(rt.constStringMap.containsKey(realString))string = rt.constStringMap.get(realString);
        if(string == null){
            name = "constString_" + curFunc.name + cnt++;//maybe ok
            string = rt.addConstString(name, realString);
        }else name = string.name;
        it.operand = new Register(name, rootNode.STRING);
        curblk.addInst(new GetElementPtr((Register)it.operand, curblk,
                new IRArrayType(realString.length(), rootNode.INT8), string,
                new ConstInt(0, 32), new ConstInt(0, 32)));
    }

}
