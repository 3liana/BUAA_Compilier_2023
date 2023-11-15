package middleend.refill;

import middleend.value.user.BasicBlock;
import middleend.value.user.instruction.terminateInst.BrInst;

import java.util.ArrayList;

public class RefillIf extends RefillUtil {
    public RefillIf(){
    }
    public void refill(){
        super.refill();
        new BrInst(realTrueBlock2, endBlock);
        if (realFalseBlock != null) {
            new BrInst(realFalseBlock, endBlock);
        }
    }
}
