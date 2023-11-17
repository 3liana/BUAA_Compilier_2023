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
    //能进行加减乘除运算的只有32位，所以没关系
    public BinaryInst(BasicBlock block,Value result, Value op1, Value op2, Operator operator) {
        super(block);
        this.result = result;
        this.op1 = op1;
        this.op2 = op2;
        this.operator = operator;
        this.result.setMyType(IntegerType.i32Type);
        switch (operator) {
            case add:
                this.result.setNum(this.op1.getNum() + this.op2.getNum());
                break;
            case sub:
                this.result.setNum(this.op1.getNum() - this.op2.getNum());
                break;
            case mul:
                this.result.setNum(this.op1.getNum() * this.op2.getNum());
                break;
            case sdiv:
                this.result.setNum(this.op1.getNum() / this.op2.getNum());
                break;
            case srem:
                this.result.setNum(this.op1.getNum() % this.op2.getNum());
        }
    }
    public String getPrint(){
        return result.getName() + " = " + operator.toString()
                + " " + type + " " + op1.getName() + " , " +
                op2.getName() + "\n";
    }
}
