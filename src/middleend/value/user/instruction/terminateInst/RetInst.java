package middleend.value.user.instruction.terminateInst;

import middleend.Value;
import middleend.type.Type;
import middleend.type.VoidType;
import middleend.value.ConstValue;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class RetInst extends Instruction {
    //ret <ty> <value>
    //ret void
    private Type type;
    public Value value;
    public RetInst(BasicBlock block,Type type,Value value) {
        super(block);
        this.type = type;
        this.value = value;
    }
    public RetInst(BasicBlock block){
        // 无value 即void型
        super(block);
        this.type = new VoidType();
    }
    public String getPrint(){
        if(this.type instanceof VoidType){
            return "ret void\n";
        } else {
            return "ret" + " " + this.type + " " + value.getName() + "\n";
        }
    }
    public void replaceValueWithConst(Value oldValue, ConstValue newConst){
        if (value != null && value.equals(oldValue)){
            value = newConst;
        }
    }
}
