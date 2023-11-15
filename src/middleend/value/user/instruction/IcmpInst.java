package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class IcmpInst extends Instruction {
    private Type ty = new IntegerType(32);
    private Value result;
    private Value v0;
    private Value v1;
    private CondString cond;
    public IcmpInst(BasicBlock b,Value result,Value v0,Value v1,CondString cond){
        super(b);
        this.result = result;
        this.v0 = v0;
        this.v1 = v1;
        this.cond = cond;
    }
    public String getPrint(){
        return this.result.getName() + " = icmp " + cond + " " + ty + " " +
                this.v0.getName() + " , " + this.v1.getName() + "\n";
    }
}
