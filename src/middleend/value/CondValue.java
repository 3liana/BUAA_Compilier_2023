package middleend.value;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;

public class CondValue extends Value {
    private int registerNum;
    public CondValue(int registerNum){
        this.registerNum = registerNum;
    }
    public String getName(){
        return "%" + this.registerNum;
    }
    public Type getType() {
        return new IntegerType(1);
    }
}
