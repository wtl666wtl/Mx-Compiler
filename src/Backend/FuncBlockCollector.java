package Backend;

import MIR.Block;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class FuncBlockCollector {

    static LinkedHashSet<Block> reachableSet;

    public static void DFS(Block blk){
        if(reachableSet.contains(blk))return;
        reachableSet.add(blk);
        blk.sucblks.forEach(FuncBlockCollector::DFS);
    }

    public static LinkedHashSet<Block> work(Block in){
        reachableSet = new LinkedHashSet<>();
        DFS(in);
        return reachableSet;
    }
}
