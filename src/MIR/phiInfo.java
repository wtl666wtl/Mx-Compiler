package MIR;

import MIR.IRoperand.BaseOperand;

import java.util.ArrayList;

public class phiInfo{

    public ArrayList<BaseOperand> vals = new ArrayList<>();
    public ArrayList<Block> blks = new ArrayList<>();

    public phiInfo(){

    }

    public phiInfo(phiInfo o){
        this.blks = o.blks;
        this.vals =o.vals;
    }

    public void delete(int i){
        vals.remove(i);
        blks.remove(i);
    }

}
