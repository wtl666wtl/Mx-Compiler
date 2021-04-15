package Backend;

import MIR.Block;
import MIR.IRoperand.BaseOperand;

import java.util.HashMap;

public class inlineCorrespond {

    public HashMap<BaseOperand, BaseOperand> correspondOperand = new HashMap<>();
    public HashMap<Block, Block> correspondBlk = new HashMap<>();

    public inlineCorrespond(HashMap<BaseOperand, BaseOperand> correspondOperand, HashMap<Block, Block> correspondBlk){
        this.correspondBlk = correspondBlk;
        this.correspondOperand = correspondOperand;
    }

    public BaseOperand get(BaseOperand x){
        if(!correspondOperand.containsKey(x)){
            correspondOperand.put(x, x.inlineCopy());
        }
        return correspondOperand.get(x);
    }

    public Block get(Block x){
        return correspondBlk.get(x);
    }

}
