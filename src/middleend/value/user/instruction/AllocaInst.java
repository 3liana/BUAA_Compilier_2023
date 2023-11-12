package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class AllocaInst extends Instruction {
    private Value result;
    private Type targetType = new IntegerType(32);
    public AllocaInst(BasicBlock block,Value result){
        super(block);
        this.result = result;
    }
    public String getPrint(){
        return result.getName() + " = alloca " +targetType + "\n";
    }
}
