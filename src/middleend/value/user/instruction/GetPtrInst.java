package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class GetPtrInst extends Instruction {
    private Value result;
    private Value from;
    private Type targetType;
    private int type;
    private int n;
    private int m;
    public GetPtrInst(BasicBlock b,Value result,Value from,int n){
        //from肯定是一个PointType
        super(b);
        this.result = result;
        this.targetType = ((PointerType)from.getMyType()).targetType;
        this.from = from;

        this.n = n;
        this.type = 0;
        this.result.setMyType(new PointerType(IntegerType.i32Type));
    }
    public GetPtrInst(BasicBlock b,Value result,Value from,int n,int m){
        super(b);
        this.result = result;
        this.targetType = ((PointerType)from.getMyType()).targetType;
        this.from = from;

        this.n = n;
        this.m = m;
        this.type = 1;
        this.result.setMyType(new PointerType(IntegerType.i32Type));
    }
    public String getPrint(){
        String s0 = this.result.getName() + " = getelementptr " +
                this.targetType +", "+from.getMyType() + " " + from.getName() + ", ";
        String s1 = "i32 0, i32 " + this.n;
        if(this.type == 1){
            s1 = s1 + ", i32 " + this.m;
        }
        return s0 + s1 + "\n";
    }

}
