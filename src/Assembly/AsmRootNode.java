package Assembly;

import Assembly.AsmOperand.GlobalReg;
import Assembly.AsmOperand.PhyReg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class AsmRootNode {

    public static ArrayList<String> phyRegName = new ArrayList<>(Arrays.asList(
            "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
    ));
    public enum saverType {
        none, caller, callee
    }
    public static ArrayList<saverType> saveStatus = new ArrayList<>(Arrays.asList(
            saverType.none,
            saverType.caller,
            saverType.none,
            saverType.none,
            saverType.none,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.callee,
            saverType.callee,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.callee,
            saverType.caller,
            saverType.caller,
            saverType.caller,
            saverType.caller
    ));
    public HashSet<AsmFunction> funcs = new HashSet<>(), builtInFuncs = new HashSet<>();
    public ArrayList<PhyReg> phyRegs = new ArrayList<>();
    public ArrayList<PhyReg> callerRegs = new ArrayList<>(), calleeRegs = new ArrayList<>();
    public HashSet<GlobalReg> globalRegs = new HashSet<>();
    public HashMap<GlobalReg, String> constStrings = new HashMap<>();

    public AsmRootNode(){
        for (int i = 0; i < 32; i++) {
            PhyReg PhyReg_i = new PhyReg(phyRegName.get(i));
            phyRegs.add(PhyReg_i);
            if(saveStatus.get(i) == saverType.caller)callerRegs.add(PhyReg_i);
            if(saveStatus.get(i) == saverType.callee)calleeRegs.add(PhyReg_i);
        }
        //todo:...
    }
}
