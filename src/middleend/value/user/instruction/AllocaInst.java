package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class AllocaInst extends Instruction {
    private Value result;
    private Type targetType = IntegerType.i32Type;
    public AllocaInst(BasicBlock block,Value result){
        //默认位i32的构造函数
        super(block);
        this.result = result;
        result.setMyType(new PointerType(targetType));
    }
    public AllocaInst(BasicBlock block,Value result,Type targetType){
        super(block);
        this.result = result;
        this.targetType = targetType;
        result.setMyType(new PointerType(targetType));
    }
    public String getPrint(){
        return result.getName() + " = alloca " +targetType + "\n";
    }
}
