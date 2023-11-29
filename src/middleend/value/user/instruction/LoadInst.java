package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class LoadInst extends Instruction {
    private Type type = new IntegerType(32);
    public Value result;
    public Value fromValue;
    public LoadInst(BasicBlock basicBlock,Value result,Value fromValue) {
        super(basicBlock);
        this.result = result;
        this.fromValue = fromValue;
        this.result.setNum(this.fromValue.getNum());

        //fromValue的MyType一定是pointType
        PointerType pointerType = (PointerType) fromValue.getMyType();
        Type targetType = pointerType.targetType;
        this.type = targetType;
        if(result.getMyType()==null){
            result.setMyType(targetType);
        }
    }
    public String getPrint(){
        return result.getName() + " = load " + type +", " +
                type + "* " + fromValue.getName() + "\n";
    }
}
