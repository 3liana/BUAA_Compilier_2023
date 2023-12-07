package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;
import middleend.value.ConstValue;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class IcmpInst extends Instruction {
    private Type ty = new IntegerType(32);//能进行>,<,>=,<=也只有i32
    public Value result;
    public Value v0;
    public Value v1;
    public CondString cond;
    public IcmpInst(BasicBlock b,Value result,Value v0,Value v1,CondString cond){
        super(b);
        this.result = result;
        this.result.setMyType(IntegerType.i1Type);
        this.v0 = v0;
        this.v1 = v1;
        this.cond = cond;
    }
    public String getPrint(){
        return this.result.getName() + " = icmp " + cond + " " + ty + " " +
                this.v0.getName() + " , " + this.v1.getName() + "\n";
    }
    public void replaceValueWithConst(Value oldValue, ConstValue newConst){
        if(this.v0.equals(oldValue)){
            v0 = newConst;
        }
        if(v1.equals(oldValue)){
            v1 = newConst;
        }
    }
}
