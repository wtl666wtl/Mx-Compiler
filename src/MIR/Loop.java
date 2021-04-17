package MIR;

import java.util.HashSet;

public class Loop {

    public HashSet<Block> loopBlocks = new HashSet<>();
    public HashSet<Block> tails = new HashSet<>();
    public HashSet<Loop> childLoops = new HashSet<>();
    public Block preHead;

    public Loop(){

    }


}
