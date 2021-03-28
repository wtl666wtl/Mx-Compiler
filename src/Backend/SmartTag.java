package Backend;

import Assembly.AsmInstruction.BaseAsmInstruction;
import Assembly.AsmOperand.PhyReg;
import Assembly.AsmOperand.VirtualReg;

public class SmartTag {

    public PhyReg phyReg;
    public VirtualReg vreg;
    public boolean dirty = false;

    public SmartTag(PhyReg phyReg, VirtualReg vreg){
        this.vreg = vreg;
        this.phyReg = phyReg;
    }

}
