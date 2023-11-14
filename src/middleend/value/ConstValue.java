package middleend.value;

import middleend.Value;
import middleend.type.IntegerType;
import middleend.type.Type;

public class ConstValue extends Value {
    private String num;

    public ConstValue(String num) {
        this.num = num;
    }

    public String getName() {
        return this.num;
    }
    public Type getType(){
        return new IntegerType(32);
    }
    public int getNum(){
        return Integer.parseInt(this.num);
    }
}
