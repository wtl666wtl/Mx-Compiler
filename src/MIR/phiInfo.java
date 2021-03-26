package MIR;

import MIR.IRoperand.BaseOperand;

import java.util.ArrayList;

public class phiInfo{

    public ArrayList<BaseOperand> vals = new ArrayList<>();
    public ArrayList<Block> blks = new ArrayList<>();

    public void delete(int i){
        vals.remove(i);
        blks.remove(i);
    }

}
