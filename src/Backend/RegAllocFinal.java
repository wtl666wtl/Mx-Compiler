package Backend;

import Assembly.AsmBlock;
import Assembly.AsmFunction;
import Assembly.AsmInstruction.*;
import Assembly.AsmOperand.*;
import Assembly.AsmRootNode;

import java.util.*;

public class RegAllocFinal {

    public AsmRootNode AsmRt;
    public int stackLength = 0;
    public AsmFunction curFunc;
    public HashSet<Reg> colors;

    public HashSet<Mv> MvWorkList = new LinkedHashSet<>();
    public HashSet<Mv> activeMvSet = new HashSet<>();
    public HashSet<Mv> mergedMvSet = new HashSet<>();
    public HashSet<Mv> bindedMvSet = new HashSet<>();
    public HashSet<Mv> frozenMvSet = new HashSet<>();

    public HashSet<Reg> spillWorkList = new LinkedHashSet<>();
    public HashSet<Reg> freezeWorkList = new HashSet<>();
    public HashSet<Reg> simplifyWorkList = new LinkedHashSet<>();
    public HashSet<Reg> spilledNodes = new LinkedHashSet<>();
    public HashSet<Reg> coloredNodes = new HashSet<>();
    public HashSet<Reg> mergedNodes = new LinkedHashSet<>();
    public HashSet<Reg> spillIntroduce = new HashSet<>();
    public HashSet<Reg> initial = new LinkedHashSet<>();

    public HashSet<edge> Edge = new HashSet<>();
    public Stack<Reg> selectStack = new Stack<>();
    public int tot;

    public static int inf = 2147483647;

    public PhyReg sp;

    public RegAllocFinal(AsmRootNode AsmRt){
        this.AsmRt = AsmRt;
        colors = new HashSet<>(AsmRt.phyRegs);
        tot = AsmRt.freeRegNum;
        sp = AsmRt.phyRegs.get(2);
    }

    public void work(){
        AsmRt.funcs.forEach(func -> {
            stackLength = 0;
            curFunc = func;
            //System.out.println("?");
            workFunc(func);
            //System.out.println("?");
            stackLength += func.paramStSize;
            if(stackLength % 16 != 0)stackLength += 16 - stackLength % 16;
            finalProcess();
        });
    }

    public void finalProcess(){
        curFunc.blks.forEach(blk ->{
                blk.stmts.removeIf(inst -> inst instanceof Mv && ((Mv) inst).rs.color == inst.rd.color);
                blk.stmts.forEach(inst -> inst.resolveSLImm(stackLength));
        });

        HashSet<AsmBlock> jmpOnlySet = new HashSet<>();
        AsmFunction func = curFunc;
        func.blks.forEach(blk -> {
            BaseAsmInstruction it = blk.stmts.getFirst();
            if(it instanceof Jp)jmpOnlySet.add(blk);
        });

        jmpOnlySet.forEach(blk -> {
            AsmBlock sucblk = blk;
            boolean dangerous = false;
            while (jmpOnlySet.contains(sucblk)) {
                sucblk = ((Jp)sucblk.stmts.getFirst()).destBlk;
                if(sucblk == blk){
                    dangerous = true;
                    break;
                }
            }
            if(!dangerous) {
                HashSet<AsmBlock> preblks = new HashSet<>(blk.preblks);
                for (AsmBlock preblk : preblks) {
                    for (BaseAsmInstruction inst : preblk.stmts) {
                        if (inst instanceof Jp && ((Jp) inst).destBlk == blk) ((Jp) inst).destBlk = sucblk;
                        else if (inst instanceof Bz && ((Bz) inst).destblk == blk) ((Bz) inst).destblk = sucblk;
                        else if (inst instanceof Br && ((Br) inst).destblk == blk) ((Br) inst).destblk = sucblk;
                    }
                    preblk.sucblks.remove(blk);
                    preblk.sucblks.add(sucblk);
                }
                sucblk.preblks.remove(blk);
                sucblk.sucblks.addAll(blk.preblks);
                if (blk == func.inblk)
                    func.inblk = sucblk;
            } else jmpOnlySet.remove(blk);
        });
        func.blks.removeAll(jmpOnlySet);

        /*HashSet<AsmBlock> BranchOnlySet = new HashSet<>();

        func.blks.forEach(blk -> {
            BaseAsmInstruction it = blk.stmts.getFirst();
            if(it instanceof Br || it instanceof Bz)BranchOnlySet.add(blk);
        });

        BranchOnlySet.forEach(blk -> {
            HashSet<AsmBlock> preblks = new HashSet<>(blk.preblks);
            if(preblks.size()==0)System.out.println(preblks.size());
            for (AsmBlock preblk : preblks) {
                if(BranchOnlySet.contains(preblk) || preblk.stmts.size() < 2)continue;
                BaseAsmInstruction inst2 = preblk.stmts.get(preblk.stmts.size() - 2);
                BaseAsmInstruction inst = preblk.stmts.getLast();
                if (inst2 instanceof Br || inst2 instanceof Bz)continue;
                if (inst instanceof Jp && ((Jp) inst).destBlk == blk){
                    if(blk.name.equals(".main_if_terminal2_inline41_inline43"))System.out.println(inst2);
                    preblk.stmts.removeLast();
                    if(blk.stmts.getFirst() instanceof Br){
                        preblk.stmts.add(new Br(preblk, ((Br)blk.stmts.getFirst()).rs1,
                                ((Br)blk.stmts.getFirst()).rs2, ((Br)blk.stmts.getFirst()).opCode, ((Br)blk.stmts.getFirst()).destblk));
                    } else {
                        preblk.stmts.add(new Bz(preblk, ((Bz)blk.stmts.getFirst()).rs,
                                ((Bz)blk.stmts.getFirst()).opCode, ((Bz)blk.stmts.getFirst()).destblk));
                    }
                    preblk.stmts.add(new Jp(preblk, ((Jp)blk.stmts.getLast()).destBlk));
                    preblk.sucblks.remove(blk);
                    preblk.sucblks.addAll(blk.sucblks);
                    blk.sucblks.forEach(sucblk -> sucblk.preblks.add(preblk));
                    blk.preblks.remove(preblk);
                }
            }
            //if(blk.preblks.size()==0)System.out.println(blk.name);
            if (blk.preblks.size() == 0){
                blk.sucblks.forEach(sucblk -> sucblk.preblks.remove(blk));
                func.blks.remove(blk);
            }
        });

        HashSet<AsmBlock> uselessSet = new HashSet<>();

        func.blks.forEach(blk -> {
            if (blk.preblks.size() == 0){
                blk.sucblks.forEach(sucblk -> sucblk.preblks.remove(blk));
                uselessSet.add(blk);
            }
        });
        func.blks.removeAll(uselessSet);*/
    }

    public void clearAll(){
        spillWorkList.clear();
        freezeWorkList.clear();
        spilledNodes.clear();
        coloredNodes.clear();
        mergedNodes.clear();
        simplifyWorkList.clear();
        initial.clear();

        MvWorkList.clear();
        activeMvSet.clear();
        mergedMvSet.clear();
        bindedMvSet.clear();
        frozenMvSet.clear();

        selectStack.clear();
        Edge.clear();
    }

    public void init(){
        curFunc.blks.forEach(blk -> blk.stmts.forEach(inst -> {
                initial.addAll(inst.defs());
                initial.addAll(inst.uses());
        }));
        initial.removeAll(colors);
        initial.forEach(Reg::clear);

        colors.forEach(phyReg -> {
            phyReg.degree = inf;
            phyReg.color = (PhyReg) phyReg;
            phyReg.alias = null;
            phyReg.edgeSet.clear();
            phyReg.MvSet.clear();
        });

        curFunc.blks.forEach(blk -> {
            double weight = Math.pow(10, blk.loopLayers);
            for(BaseAsmInstruction inst : blk.stmts){
                inst.uses().forEach(reg -> reg.weight += weight);
                if(inst.rd != null)
                    inst.rd.weight += weight;
            }
        });
    }

    public void addEdge(Reg x, Reg y){
        edge e = new edge(x, y);
        if(x != y && !Edge.contains(e)){
            Edge.add(new edge(x, y));
            Edge.add(new edge(y, x));
            if(!(colors.contains(x))){
                x.degree++;
                x.edgeSet.add(y);
            }
            if(!(colors.contains(y))){
                y.degree++;
                y.edgeSet.add(x);
            }
        }
    }

    //public int cnt = 0, num = 0;
    public void build(){
        curFunc.blks.forEach(blk -> {
            //cnt=0;
            HashSet<Reg> curLive = new HashSet<>(blk.liveOut);
            for(ListIterator<BaseAsmInstruction> p = blk.stmts.listIterator(blk.stmts.size()); p.hasPrevious();) {
                BaseAsmInstruction inst = p.previous();
                /*if(cnt==1){
                    System.out.println(inst);
                    System.out.println(p.nextIndex());}*/
                //System.out.println();
                if(inst instanceof Mv){
                    curLive.removeAll(inst.uses());
                    HashSet<Reg> MvAbout = inst.uses();
                    MvAbout.addAll(inst.defs());
                    MvAbout.forEach(reg -> reg.MvSet.add((Mv) inst));
                    MvWorkList.add((Mv) inst);
                }
                HashSet<Reg> defs = inst.defs();
                curLive.add(AsmRt.phyRegs.get(0));
                curLive.addAll(defs);

                defs.forEach(def -> curLive.forEach(reg -> addEdge(reg, def)));

                curLive.removeAll(defs);
                curLive.addAll(inst.uses());
            }
            //System.out.println(cnt);
        });
    }

    public HashSet<Mv> MvInstSet(Reg it){
        HashSet<Mv> Mvs = new HashSet<>(MvWorkList);
        Mvs.addAll(activeMvSet);
        Mvs.retainAll(it.MvSet);
        return Mvs;
    }

    public boolean usedInMv(Reg it){
        return !MvInstSet(it).isEmpty();
    }

    public Reg getAlias(Reg it){
        if(mergedNodes.contains(it)){
            it.alias = getAlias(it.alias);
            return it.alias;
        }else return it;
    }

    public void assignColor(){
        while(!selectStack.isEmpty()){
            Reg it = selectStack.pop();
            ArrayList<PhyReg> freeColors = new ArrayList<>(AsmRt.freeRegs);
            HashSet<Reg> colored = new HashSet<>(coloredNodes);
            colored.addAll(colors);
            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
            //System.out.println(((VirtualReg)it).index);
            it.edgeSet.forEach(e -> {
                //System.out.println(e);
                //if(e instanceof VirtualReg)System.out.println(((VirtualReg)e).index);
                Reg w = getAlias(e);
                if(colored.contains(w))
                    freeColors.remove(w.color);
            });
            if(freeColors.isEmpty())spilledNodes.add(it);
            else{
                coloredNodes.add(it);
                it.color = freeColors.get(0);
                //System.out.println(it.color);
            }
        }
        mergedNodes.forEach(it -> it.color = getAlias(it).color);
    }

    public HashSet<Reg> edges(Reg it){
        HashSet<Reg> e = new HashSet<>(it.edgeSet);
        selectStack.forEach(e::remove);
        e.removeAll(mergedNodes);
        return e;
    }

    public HashSet<Reg> edges(Reg x, Reg y){
        HashSet<Reg> e = new HashSet<>(edges(x));
        e.addAll(edges(y));
        return e;
    }

    public void decreaseDegree(Reg it){
        int degree = it.degree;
        it.degree--;
        if(degree == tot){
           // HashSet<Reg> nodes = new HashSet<>(edges(it));
           // nodes.add(it);
            enableMv(it);
            spillWorkList.remove(it);
            if(usedInMv(it))freezeWorkList.add(it);
            else simplifyWorkList.add(it);
        }
    }

    public void enableMv(Reg node){
        MvInstSet(node).forEach(mv -> {
            if(activeMvSet.contains(mv)){
                activeMvSet.remove(mv);
                MvWorkList.add(mv);
            }
        });
    }

    public void enableMv(HashSet<Reg> nodes){
        nodes.forEach(node -> MvInstSet(node).forEach(mv -> {
            if(activeMvSet.contains(mv)){
                activeMvSet.remove(mv);
                MvWorkList.add(mv);
            }
        }));
    }

    public void addToSimplify(Reg it){
        if(!colors.contains(it) && !usedInMv(it) && it.degree < tot){
            freezeWorkList.remove(it);
            simplifyWorkList.add(it);
        }
    }

    public boolean checkEach(Reg z, Reg x){
        return z.degree < tot || colors.contains(z) || Edge.contains(new edge(z, x));
    }

    public boolean check(Reg x, Reg y){
        boolean flag = true;
        for(Reg z : edges(y)) flag &= checkEach(z, x);
        return flag;
    }

    public boolean conservative(HashSet<Reg> nodes){
        int num = 0;
        for(Reg x : nodes)num += x.degree >= tot ? 1 : 0;
        return num < tot;
    }

    public void combine(Reg x, Reg y){
        if(freezeWorkList.contains(y))freezeWorkList.remove(y);
        else spillWorkList.remove(y);
        mergedNodes.add(y);
        y.alias = x;
        x.MvSet.addAll(y.MvSet);
        //HashSet<Reg> tmp = new HashSet<>();
        //tmp.add(y);
        enableMv(y);
        edges(y).forEach(z -> {
            addEdge(z, x);
            decreaseDegree(z);
        });
        if(x.degree >= tot && freezeWorkList.contains(x)){
            freezeWorkList.remove(x);
            spillWorkList.add(x);
        }
    }

    public void simplify(){
        Reg it = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(it);
        selectStack.push(it);
        edges(it).forEach(this::decreaseDegree);
    }

    public void merge(){
        Mv mv = MvWorkList.iterator().next();
        Reg rd = getAlias(mv.rd), rs = getAlias(mv.rs);
        Reg x, y;
        if(colors.contains(rs)){
            x = rs;
            y = rd;
        }else {
            x = rd;
            y = rs;
        }
        MvWorkList.remove(mv);
        if(x == y){
            mergedMvSet.add(mv);
            addToSimplify(x);
        } else if(colors.contains(y) || Edge.contains(new edge(x, y))){
            //bindedMvSet.add(mv);
            addToSimplify(x);
            addToSimplify(y);
        } else {
            if((colors.contains(x) && check(x, y) || (!colors.contains(x) && conservative(edges(x, y))))){
                mergedMvSet.add(mv);
                combine(x, y);
                addToSimplify(x);
            } else activeMvSet.add(mv);
        }
    }

    public void freezeMv(Reg it){
        MvInstSet(it).forEach(mv -> {
            Reg rd = mv.rd, rs = mv.rs, t;
            if(getAlias(it) == getAlias(rs))t = getAlias(rd);
            else t= getAlias(rs);
            activeMvSet.remove(mv);
            frozenMvSet.add(mv);
            if(t.degree < tot && MvInstSet(t).isEmpty()){
                freezeWorkList.remove(t);
                simplifyWorkList.add(t);
            }
        });
    }

    public void freeze(){
        Reg it = freezeWorkList.iterator().next();
        freezeWorkList.remove(it);
        simplifyWorkList.add(it);
        freezeMv(it);
    }

    public Reg getSpill(){
        Reg min = null;
        double minCost = inf;
        for (Reg x : spillWorkList) {
            if (!spillIntroduce.contains(x) && (x.weight / x.degree < minCost || min == null)) {
                min = x;
                minCost = x.weight / x.degree;
            }
        }
        return min;
    }

    public void spill(){
        Reg it = getSpill();
        spillWorkList.remove(it);
        simplifyWorkList.add(it);
        freezeMv(it);
    }

    public void rewrite(){
        spilledNodes.forEach(node -> {
            node.stackOffset = new StackLengthImm(-1 * stackLength - 4);
            stackLength += 4;
        });
        curFunc.blks.forEach(blk -> {
            for (BaseAsmInstruction inst : blk.stmts){
                if(inst.rd instanceof VirtualReg)
                    getAlias(inst.rd);
            }
        });

        curFunc.blks.forEach(blk -> {
            for(ListIterator<BaseAsmInstruction> p = blk.stmts.listIterator(); p.hasNext();){
                BaseAsmInstruction inst = p.next();
                for(Reg reg: inst.uses()) {
                    if (reg.stackOffset != null) {
                        if (inst.defs().contains(reg)) {
                            VirtualReg tmp = new VirtualReg(++curFunc.vregCounter, ((VirtualReg) reg).width);
                            spillIntroduce.add(tmp);
                            inst.changeUse(reg, tmp);
                            inst.changeRd(reg, tmp);
                            p.previous();
                            p.add(new Ld(tmp, blk, sp, reg.stackOffset, tmp.width));
                            p.next();
                            p.add(new St(blk, sp, tmp, reg.stackOffset, tmp.width));
                            p.previous();
                        } else {
                            if (inst instanceof Mv && ((Mv) inst).rs == reg && inst.rd.stackOffset == null) {
                                BaseAsmInstruction changeInst =
                                        new Ld(inst.rd, blk, sp, reg.stackOffset, ((VirtualReg) reg).width);
                                //p.remove();
                                //p.add(changeInst);
                                p.set(changeInst);
                                inst = changeInst;
                            } else {
                                VirtualReg tmp = new VirtualReg(++curFunc.vregCounter, ((VirtualReg) reg).width);
                                spillIntroduce.add(tmp);
                                p.previous();
                                p.add(new Ld(tmp, blk, sp, reg.stackOffset, tmp.width));
                                p.next();
                                inst.changeUse(reg, tmp);
                            }
                        }
                    }
                }
                for(Reg def : inst.defs()){
                    if(def.stackOffset != null && !inst.uses().contains(def)){
                        if(inst instanceof Mv && ((Mv) inst).rs.stackOffset == null){
                            BaseAsmInstruction changeInst =
                                    new St(blk, sp, ((Mv) inst).rs, def.stackOffset, ((VirtualReg) def).width);
                            //p.remove();
                            //p.add(changeInst);
                            p.set(changeInst);
                            inst = changeInst;
                        } else {
                            VirtualReg tmp = new VirtualReg(++curFunc.vregCounter, ((VirtualReg)def).width);
                            spillIntroduce.add(tmp);
                            inst.changeRd(def, tmp);
                            p.add(new St(blk, sp, tmp, def.stackOffset, tmp.width));
                            p.previous();
                        }
                    }
                }
            }
        });
    }

    public void workFunc(AsmFunction func){
        boolean flag = false;
        while(!flag){
            //System.out.println("START");
            clearAll();
            init();
            funcCollector(func);
            build();

            initial.forEach(it -> {
                if(it.degree >= tot)spillWorkList.add(it);
                else if(usedInMv(it))freezeWorkList.add(it);
                else simplifyWorkList.add(it);
            });

            //System.out.println("??");
            while(!(freezeWorkList.isEmpty() && simplifyWorkList.isEmpty()
                    && MvWorkList.isEmpty() && spillWorkList.isEmpty())){
                if(!simplifyWorkList.isEmpty())simplify();
                else if(!MvWorkList.isEmpty())merge();
                else if(!freezeWorkList.isEmpty())freeze();
                else spill();
            }
            //System.out.println("MID");
            assignColor();
            //System.out.println("MID2");
            flag = spilledNodes.isEmpty();
            if(!flag)rewrite();
            //System.out.println("END");
        }
    }

    public HashMap<AsmBlock, HashSet<Reg> > blkUses = new HashMap<>(), blkDefs = new HashMap<>();
    public HashSet<AsmBlock> hasVisited = new HashSet<>();
    public Queue<AsmBlock> Q = new LinkedList<>();

    public void collectorClear(){
        //blkDefs.clear();
        //blkUses.clear();
        hasVisited.clear();
        Q.clear();
    }

    public void funcCollector(AsmFunction func){
        collectorClear();
        func.blks.forEach(this::blockCollector);
        Q.offer(func.outblk);
        hasVisited.add(func.outblk);
        while(!Q.isEmpty())
            liveCollector(Q.poll());
    }

    public void blockCollector(AsmBlock blk){
        HashSet<Reg> uses = new HashSet<>();
        HashSet<Reg> defs = new HashSet<>();
        for(BaseAsmInstruction inst : blk.stmts){
            HashSet<Reg> instUses = inst.uses();
            instUses.removeAll(defs);
            uses.addAll(instUses);
            defs.addAll(inst.defs());
        }
        blkUses.put(blk, uses);
        blkDefs.put(blk, defs);
        blk.liveIn.clear();
        blk.liveOut.clear();
    }

    public void liveCollector(AsmBlock blk){
        hasVisited.add(blk);
        HashSet<Reg> liveOut = new HashSet<>();
        blk.sucblks.forEach(sucblk -> liveOut.addAll(sucblk.liveIn));
        HashSet<Reg> liveIn = new HashSet<>(liveOut);
        liveIn.removeAll(blkDefs.get(blk));
        liveIn.addAll(blkUses.get(blk));
        blk.liveOut.addAll(liveOut);
        liveIn.removeAll(blk.liveIn);
        if(!liveIn.isEmpty()){
            //blk.liveIn.forEach(System.out::println);
            blk.liveIn.addAll(liveIn);
            blk.preblks.forEach(hasVisited::remove);
        }
        blk.preblks.forEach(preblk -> {
            if(!hasVisited.contains(preblk)) {
                Q.offer(preblk);
                hasVisited.add(preblk);
            }
        });
    }

}
