package middleend.value.user.instruction;

import middleend.Value;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class ZextInst extends Instruction {
    private Value result;
    private Value fromValue;
    public ZextInst(BasicBlock b,Value result, Value fromValue){
        super(b);
        this.result = result;
        this.fromValue = fromValue;
    }
    public String getPrint(){
        return result.getName() + " = zext i1 " + fromValue.getName() +
                " to i32" + "\n";
    }
}
