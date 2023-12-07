package middleend.value.user;

import middleend.Value;
import middleend.type.InstType;
import middleend.value.ConstValue;
import middleend.value.User;
import middleend.value.user.instruction.terminateInst.BrInst;
import middleend.value.user.instruction.terminateInst.RetInst;

public class Instruction extends User {
    private BasicBlock basicBlock;

    public Instruction(){}
    public Instruction(BasicBlock basicBlock){
        this.basicBlock = basicBlock;
        basicBlock.addInst(this);
        this.setMyType(InstType.instType);
    }
    public String getPrint(){
        return "";
    }
    public static boolean isTerInst(Instruction inst){
        if(inst instanceof BrInst ||
        inst instanceof RetInst){
            return true;
        } else {
            return false;
        }
    }
    public void replaceValueWithConst(Value oldValue, ConstValue newConst){
    }
}
