package MIR;

import MIR.IRinstruction.*;
import MIR.IRoperand.BaseOperand;
import MIR.IRoperand.Register;

import java.util.ArrayList;
import java.util.HashMap;

public class phiToMove {

    public ArrayList<Move> moveLists = new ArrayList<>();
    public HashMap<BaseOperand, Integer> useTime = new HashMap<>();

    public void addMove(Move mv){
        moveLists.add(mv);
        if(mv.origin instanceof Register)
            useTime.put(mv.origin, useTime.containsKey(mv.origin) ? useTime.get(mv.origin) + 1 : 1);
    }

}
