package Backend;

import MIR.*;
import MIR.IRinstruction.BaseInstruction;
import MIR.IRinstruction.Load;
import MIR.IRinstruction.Phi;
import MIR.IRinstruction.Store;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;

public class Mem2Reg {

    public rootNode rt;
    HashSet<Block> defblk = new HashSet<>();
    HashMap<Block, HashSet<Load>> allocLdMap = new HashMap<>();
    HashMap<Block, HashMap<Register, Phi>> allocPhiMap = new HashMap<>();
    HashMap<Block, HashMap<Register, BaseOperand>> allocStMap = new HashMap<>();

    HashMap<BaseOperand, BaseOperand> substitutes = new HashMap<>();

    public Mem2Reg(rootNode rt){
        this.rt = rt;
    }

    public void work(){
        rt.funcs.forEach((funcName, func) -> workFunc(func));
    }

    public void workFunc(Function func){
        //System.out.println("#########################");
        //System.out.println(func.name);
        new DomGen(func).workFunc();//new DomGen(func).workFunc();

        func.funcBlocks.forEach(blk -> {
            allocLdMap.put(blk, new HashSet<>());
            allocPhiMap.put(blk, new HashMap<>());
            allocStMap.put(blk, new HashMap<>());
        });

        //resolve load/store
        for(Block blk : func.funcBlocks){
            //System.out.println("blk name : " + blk.name);
            for(ListIterator<BaseInstruction> p = blk.stmts.listIterator(); p.hasNext();){
            //for (BaseInstruction inst : blk.stmts) {
                //BaseInstruction inst = blk.stmts.get(i);
                BaseInstruction inst = p.next();
                if(inst instanceof Load){
                    BaseOperand addr = ((Load)inst).addr;
                    if(addr instanceof Register && func.varPtrs.contains(addr)){
                      //  System.out.println(inst.blk.name);
                      //  System.out.println(addr);
                        if(allocStMap.get(inst.blk).containsKey(addr)){
                            substitutes.put(inst.rd, allocStMap.get(inst.blk).get(addr));
                            p.remove();
                            //inst.deleteFlag = true;
                            inst.deleteSelf(false);
                        }else{
                            //((Load)inst).p = p;
                            //System.out.println(inst.blk.name);
                            allocLdMap.get(inst.blk).add((Load) inst);
                        }
                    }
                }else if(inst instanceof Store){
                    BaseOperand addr = ((Store)inst).addr;
                    if(addr instanceof Register && func.varPtrs.contains(addr)){
                        //System.out.println(((Store) inst).storeVal);
                       // System.out.println(((Store) inst).addr);
                        //System.out.println(inst.blk.name);
                        defblk.add(inst.blk);
                        allocStMap.get(inst.blk).put((Register) addr, ((Store) inst).storeVal);
                        p.remove();
                        //inst.deleteFlag = true;
                        inst.deleteSelf(false);
                    }
                }
            }
        }
        //System.out.println("????" + defblk.size());
        //Phi
        while(defblk.size() > 0){
            HashSet<Block> nowblks = defblk;
            defblk = new HashSet<>();
            for(Block blk : nowblks){
                //System.out.println(blk.name);
                HashMap<Register, BaseOperand> nowAlloc = allocStMap.get(blk);
                //System.out.println(nowAlloc.size());
                if(nowAlloc.size() > 0){
                    for(Block df : blk.domFrontiers){
                        //System.out.println("Yes!");
                        for(Map.Entry<Register, BaseOperand> entry : nowAlloc.entrySet()){
                            Register addr = entry.getKey();
                            BaseOperand storeVal = entry.getValue();

                            if(!allocPhiMap.get(df).containsKey(addr)){
                                Register rd = new Register(addr.name + "_phi_Reg", storeVal.type);
                                Phi phi = new Phi(rd, df, new phiInfo());
                                //System.out.println("####");
                                //System.out.println(df.name);
                                //System.out.println(rd.name);
                                //System.out.println("####");
                                df.addPhi(phi);
                                if(!allocStMap.get(df).containsKey(addr)){
                                    allocStMap.get(df).put(addr, rd);
                                    defblk.add(df);
                                }
                                allocPhiMap.get(df).put(addr, phi);
                            }
                        }
                    }
                }
            }
        }

        //rename
        func.funcBlocks.forEach(blk -> {
            if(!allocPhiMap.get(blk).isEmpty()){
                allocPhiMap.get(blk).forEach((addr, phi) -> blk.preblks.forEach(preblk -> {
                    Block nowblk = preblk;
                    //System.out.println(func.name);
                    //System.out.println(allocStMap.get(nowblk));
                    //System.out.println(blk.name);
                    //System.out.println(nowblk.name);
                    while (!allocStMap.get(nowblk).containsKey(addr)) {
                        nowblk = nowblk.iDom;
                        //System.out.println(nowblk.name);
                    }
                    phi.addOrigin(allocStMap.get(nowblk).get(addr), preblk);
                }));
            }
            if(!allocLdMap.get(blk).isEmpty()){
                allocLdMap.get(blk).forEach(ld -> {
                    //System.out.println("ld: " + ld.addr);
                    //System.out.println("ld: " + ld.rd);
                    //System.out.println("ld: " + ld.blk.name);
                    Register reg = ld.rd;
                    Register addr = (Register)ld.addr;
                    BaseOperand replace;
                    //System.out.println(blk.name);
                    if(allocPhiMap.get(blk).containsKey(addr))
                        replace = allocPhiMap.get(blk).get(addr).rd;
                    else{
                        Block curblk = blk.iDom;
                        //System.out.println(addr.name);
                        while(true){
                            //System.out.println(blk.name);
                            //System.out.println(curblk.name);
                            if(allocStMap.containsKey(curblk))
                                if(allocStMap.get(curblk).containsKey(addr)){
                                replace = allocStMap.get(curblk).get(addr);
                                break;
                            } else curblk = curblk.iDom;
                        }
                    }
                    substitutes.put(reg, getReplace(replace));
                    ld.deleteFlag = true;
                    ld.deleteSelf(false);
                });
            }
        });

        //substitute All
        substitutes.forEach((oldOperand, newOperand) ->
                ((Register)oldOperand).replaceAllUse(getReplace(newOperand)));
    }

    public BaseOperand getReplace(BaseOperand origin){
        BaseOperand it = origin;
        while(substitutes.containsKey(it))
            it = substitutes.get(it);
        return it;
    }

}
