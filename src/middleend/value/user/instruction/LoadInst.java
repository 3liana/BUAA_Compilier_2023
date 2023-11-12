package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class LoadInst extends Instruction {
    private Type type = new IntegerType(32);
    private Value result;
    private Value fromValue;
    public LoadInst(BasicBlock basicBlock,Value result,Value fromValue) {
        super(basicBlock);
        this.result = result;
        this.fromValue = fromValue;
    }
    public String getPrint(){
        return result.getName() + " = load " + type +", " +
                type + "* " + fromValue.getName() + "\n";
    }
}
