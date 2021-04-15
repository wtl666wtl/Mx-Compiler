package Backend;

import MIR.Block;
import MIR.Function;

import java.util.ArrayList;
import java.util.HashMap;

public class DomGen {

    public Function func;
    public int total = 0;
    public ArrayList<Block> dfsIndex = new ArrayList<>();
    public HashMap<Block, Integer> dfsOrder = new HashMap<>();
    public ArrayList<ArrayList<Block>> tub = new ArrayList<>();
    public HashMap<Block, Block> father = new HashMap<>();

    public HashMap<Block, Block> sDom = new HashMap<>();
    public HashMap<Block, Block> union = new HashMap<>();
    public HashMap<Block, Block> minVer = new HashMap<>();

    public DomGen(Function func){
        this.func = func;
    }

    public void dfs(Block blk){
        if(dfsOrder.containsKey(blk))return;
        //init
        dfsIndex.add(blk);
        sDom.put(blk, blk);
        dfsOrder.put(blk, ++total);
        union.put(blk, blk);
        minVer.put(blk, blk);

        blk.sucblks.forEach(sucblk -> {
            //tem.out.println("F@Q" + sucblk.name);
            if(!dfsOrder.containsKey(sucblk)){
                dfs(sucblk);
                father.put(sucblk, blk);
            }
        });
    }

    public void getOrder(Block inblk) {
        total = 0;
        dfsIndex.add(null);//1-base
        dfs(inblk);
        father.put(inblk, null);
    }

    public Block eval(Block it){
        //System.out.println("eval: " + it.name);
        //System.out.println("eval: " + minVer.get(it));
        if(union.get(it) != union.get(union.get(it))){
            if(dfsOrder.get(sDom.get(minVer.get(it))) > dfsOrder.get(sDom.get(eval(union.get(it)))))
                minVer.put(it, eval(union.get(it)));
            union.put(it, union.get(union.get(it)));
        }
        return minVer.get(it);
    }

    public void workFunc(){
        Block inblk = func.inblk;

        tub = new ArrayList<>();
        dfsIndex = new ArrayList<>();
        total = 0;

        getOrder(inblk);

        for (int i = 0; i <= total; i++) {
            tub.add(new ArrayList<>());
            //if(i > 0)System.out.print(" " + dfsIndex.get(i).name);
        }
        //System.out.println("");

        for (int i = total; i > 1; i--) {
            Block blk = dfsIndex.get(i);
            for(Block preblk : blk.preblks){
                //System.out.println(blk.name);
                //System.out.println(preblk.name);
                //System.out.println(func.funcBlocks.contains(preblk));
                //System.out.println(dfsOrder.get(preblk));
                Block evalblk = eval(preblk);
                //System.out.println(sDom.get(blk).name);
                //System.out.println(evalblk.name);
                //System.out.println(sDom.get(evalblk).name);
                if(dfsOrder.get(sDom.get(blk)) > dfsOrder.get(sDom.get(evalblk)))
                    sDom.put(blk, sDom.get(evalblk));
            }
            tub.get(dfsOrder.get(sDom.get(blk))).add(blk);
            Block fa = father.get(blk);
            union.put(blk, fa);
            for(Block j : tub.get(dfsOrder.get(fa))){
                Block b = eval(j);
                j.iDom = dfsOrder.get(sDom.get(b)) < dfsOrder.get(fa) ? b : fa;
            }
            tub.get(dfsOrder.get(fa)).clear();
        }
        for (int i = 2; i <= total; i++) {
            Block blk = dfsIndex.get(i);
            if(blk.iDom != sDom.get(blk))
                blk.iDom = blk.iDom.iDom;
        }

        for (int i = 1; i < dfsIndex.size(); i++) {
            Block blk = dfsIndex.get(i);
            if(blk.preblks.size() > 1){
                for (Block preblk : blk.preblks){
                    while(preblk != blk.iDom){
                        //System.out.println(preblk.name);
                        preblk.domFrontiers.add(blk);
                        preblk = preblk.iDom;
                    }
                }
            }
        }

        /*for (int i = 1; i < dfsIndex.size(); i++) {
            //System.out.println("block : " + dfsIndex.get(i).name);
            if(dfsIndex.get(i).iDom != null)System.out.println("iDom : " + dfsIndex.get(i).iDom.name);
        }*/
        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
