package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class AllocaInst extends Instruction {
    public Value result;
    private Type targetType = IntegerType.i32Type;
    public int int_type;
    public AllocaInst(BasicBlock block,Value result){
        //默认位i32的构造函数
        super(block);
        this.result = result;
        result.setMyType(new PointerType(targetType));
        this.int_type = 0;
    }
    public AllocaInst(BasicBlock block,Value result,Type targetType){
        super(block);
        this.result = result;
        this.targetType = targetType;
        result.setMyType(new PointerType(targetType));
        if(targetType.equals(IntegerType.i32Type)){
            this.int_type = 0;
        } else {
            this.int_type = 1;
        }
    }
    public Type getTargetType(){
        return this.targetType;
    }
    public String getPrint(){
        return result.getName() + " = alloca " +targetType + "\n";
    }
}
