package middleend.value;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;

public class CondValue extends Value {
    private int registerNum;
    public CondValue(int registerNum){
        this.registerNum = registerNum;
        this.setMyType(IntegerType.i1Type);
    }
    public String getName(){
        return "%" + this.registerNum;
    }
}
