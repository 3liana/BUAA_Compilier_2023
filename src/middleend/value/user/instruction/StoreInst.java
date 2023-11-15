package middleend.value.user.instruction;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.PointerType;
import middleend.type.Type;
import middleend.value.user.BasicBlock;
import middleend.value.user.Instruction;

public class StoreInst extends Instruction {
    //store某Type的变量给它的Pointer
    private Value fromValue;
    private Value toValue;
    private Type type = new IntegerType(32);
    public StoreInst(BasicBlock basicBlock,Value fromValue,Value toValue){
        super(basicBlock);
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.toValue.setNum(this.fromValue.getNum());
        this.type = fromValue.getMyType();
        toValue.setMyType(new PointerType(this.type));
    }
    public String getPrint(){
        return "store " + type + " " + fromValue.getName() +", " +
                type + "* " + toValue.getName() + "\n";
    }
}
