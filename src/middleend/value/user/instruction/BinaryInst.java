package middleend.value.user.instruction;

import middleend.Operator;
import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class BinaryInst extends Instruction {
    //<result>=<operator><ty><op1><op2>
    private Value result;
    private Value op1;
    private Value op2;
    private Operator operator;
    private Type type = new IntegerType(32);
    public BinaryInst(BasicBlock block,Value result, Value op1, Value op2, Operator operator) {
        super(block);
        this.result = result;
        this.op1 = op1;
        this.op2 = op2;
        this.operator = operator;
    }
    public String getPrint(){
        //todo 确定输出格式
        return result.getName() + " = " + operator.toString()
                + " " + type + " " + op1.getName() + " , " +
                op2.getName() + "\n";
    }
}
